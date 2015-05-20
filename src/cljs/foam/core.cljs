(ns cljs.foam.core
  (:require [om.core :as om :include-macros true]
            [om-tools.core :refer-macros [defcomponentk defcomponent]]
            [cljs.core.async :as async :refer [<! put! chan]]
            [cljs.foam.field.core :as fc]
            [cljs.foam.form-group :as fg]
            [sablono.core :as html :refer-macros [html]]
            [plumbing.core :as plumbing :refer-macros [letk]])
  (:use-macros [cljs.core.async.macros :only [go-loop]]))

(enable-console-print!)

(defn button-disabled?
  [m]
  (let [values (vals m)]
    (not (boolean (every? false? values)))))

(defn get-all-id
  [config]
  (set (map :id config)))

(defn update-form-field-state
  [owner content]
  (letk [[id value error] content]
        (om/update-state! owner (fn [current]
                                  (-> current
                                      (assoc-in [:value id] value)
                                      (assoc-in [:error id] error))))))

(defn init-value-state
  [config]
  (->> config
       (map (fn [cfg]
              (let [{:keys [value id]} cfg]
                {id value})))
       (apply merge)))

(defn init-error-state
  [config]
  (let [current-value (init-value-state config)]
    (->> config
         (map (fn [cfg]
                (let [{:keys [value validations id]} cfg]
                  {id (-> (fg/error-messages validations
                                             current-value
                                             value)
                          seq
                          boolean)})))
         (apply merge))))

(defcomponentk form
  [[:data fields [:button id {text "Submit"} {classes "btn-primary"}]]
   owner
   [:opts submit-fn]]
  (display-name [_] "form")
  (init-state [_]
              {:ch (chan)
               :message ""
               :value (init-value-state fields)
               :error (init-error-state fields)})
  (will-mount [_]
              (let [ch (om/get-state owner :ch)]
                (go-loop []
                  (let [content (<! ch)]
                    (when ((get-all-id fields) (:id content))
                      (update-form-field-state owner content))
                    (when (= id (:id content))
                      (submit-fn owner (om/get-state owner :value))))
                  (recur))))
  (render-state [_ {:keys [ch message error]}]
                (html [:form {:role "form"}
                       [:fieldset {}
                        (map-indexed (fn [idx cfg]
                                       (fg/->form-group cfg
                                                        {:react-key idx
                                                         :opts {:parent-ch ch}}))
                                     fields)]
                       [:footer {}
                        (om/build fc/field
                                  {:type :button
                                   :disabled (button-disabled? error)
                                   :id id
                                   :text text
                                   :classes classes}
                                  {:opts {:parent-ch ch}})
                        (when-not (empty? message)
                          [:div {}
                           [:ul {:class "list-unstyled"}
                            [:li {}
                             message]]])]])))

;; (defn example
;;   [data owner]
;;   (reify
;;     om/IRender
;;     (render [_]
;;       (html
;;        (->form {:fields [{:id :name
;;                           :type :text
;;                           :label "Name"
;;                           :validations [[(fn [_ v]
;;                                            (> (count v) 3))
;;                                          "Error!"]]
;;                           :value "1234fwefwe"}
;;                          {:id :random
;;                           :type :select
;;                           :label "Random"
;;                           :options [{:value "1"
;;                                      :text "first"}
;;                                     {:value "2"
;;                                      :text "second"}
;;                                     {:value "3"
;;                                      :text "third"}]
;;                           :validations []}]
;;                 :button {:id :button
;;                          :text "Submit"
;;                          :classes "btn-danger"}}
;;                {:opts {:submit-fn (fn [e v]
;;                                     (println v))}})))))

;; (om/root example
;;          {}
;;          {:target (.getElementById js/document "main")})
