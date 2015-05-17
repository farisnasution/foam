(ns cljs.foam.field.mixin
  (:require [om.core :as om :include-macros true]
            [om-tools.mixin :refer-macros [defmixin]]
            [cljs.core.async :as async :refer [<! put! chan]]
            [cljs.foam.util.event :as ue]
            [plumbing.core :as plumbing :refer-macros [letk]])
  (:use-macros [cljs.core.async.macros :only [go-loop]]
               [plumbing.map :only [keyword-map]]))

(defmixin field-mixin
  (will-mount [owner]
              (let [ch (om/get-state owner :ch)
                    parent-ch (om/get-state owner :parent-ch)]
                (go-loop []
                  (let [event (<! ch)]
                    (om/update-state! owner #(assoc %
                                               :field-value (ue/event->value event)
                                               :event event)))
                  (recur))))
  (did-mount [owner]
             (let [callback-fn (om/get-state owner :callback-fn)
                   events (om/get-state owner :events)
                   node (om/get-node owner)]
               (mapv (fn [event]
                       (ue/listen node event callback-fn))
                     events)))
  (did-update [owner prev-props prev-state]
              (let [id (om/get-state owner :id)
                    event (om/get-state owner :event)
                    value (om/get-state owner :field-value)
                    parent-ch (om/get-state owner :parent-ch)]
                (when event
                  (put! parent-ch (keyword-map id event value)))))
  (will-unmount [owner]
                (let [callback-fn (om/get-state owner :callback-fn)
                      events (om/get-state owner :events)
                      node (om/get-node owner)]
                  (mapv (fn [event]
                          (ue/unlisten node event callback-fn))
                        events))))

(defn update-value-error
  [owner content]
  (letk [[id value error event] content]
        (om/update-state! owner (fn [current]
                                  (-> current
                                      (assoc-in [:value id] value)
                                      (assoc-in [:error id] error)
                                      (assoc :event event))))))

(defmixin map-fieldset-mixin
  (will-mount [owner]
              (let [ch (om/get-state owner :ch)]
                (go-loop []
                  (let [content (<! ch)]
                    (update-value-error owner content))
                  (recur))))
  (did-update [owner prev-props prev-state]
              (let [parent-ch (om/get-state owner :parent-ch)
                    event (om/get-state owner :event)
                    value (om/get-state owner :value)
                    error (om/get-state owner :error)
                    id (om/get-state owner :id)]
                (put! parent-ch (keyword-map event value error id)))))

(defmixin vector-fieldset-mixin
  (will-mount [owner]
              (let [ch (om/get-state owner :ch)]
                (go-loop []
                  (let [content (<! ch)]
                    (update-value-error owner content))
                  (recur))))
  (did-update [owner prev-props prev-state]
              (let [parent-ch (om/get-state owner :parent-ch)
                    event (om/get-state owner :event)
                    value (map (fn [[_ v]] v)
                               (om/get-state owner :value))
                    error (map (fn [[_ v]] v)
                               (om/get-state owner :error))
                    id (om/get-state owner :id)]
                (put! parent-ch (keyword-map event value error id)))))
