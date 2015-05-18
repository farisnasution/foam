(ns cljs.foam.field.select
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.core.async :as async :refer [<! put! chan]]
            [om-tools.core :refer-macros [defcomponentk defcomponent]]
            [cljs.foam.field.mixin :as fm]))

(defcomponentk inner-option
  [[:data value text] owner opts]
  (display-name [_] "inner-option")
  (render [_]
          (html [:option (assoc opts :value value)
                 text])))

(defcomponent option
  [{:keys [data]} owner opts]
  (display-name [_] "option")
  (render [_]
          (html (->inner-option data {:opts opts}))))

(defcomponentk select
  [[:data {value ""} options id] owner [:opts parent-ch id]]
  (:mixins fm/field-mixin)
  (display-name [_] "select")
  (init-state [_]
              (let [ch (chan)]
                {:ch ch
                 :parent-ch parent-ch
                 :callback-fn #(put! ch %)
                 :id id
                 :field-value value
                 :events ["change"]}))
  (render-state [_ {:keys [field-value]}]
                (html [:select {:id (name id)
                                :default-value field-value}
                       (om/build-all option
                                     (map-indexed (fn [idx d]
                                                    {:data d
                                                     :react-key idx})
                                                  options)
                                     {:key :react-key})])))
