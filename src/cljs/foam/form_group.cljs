(ns cljs.foam.form-group
  (:require [om.core :as om :include-macros true]
            [om-tools.core :refer-macros [defcomponentk]]
            [sablono.core :as html :refer-macros [html]]
            [cljs.foam.util.event :as ue]
            [cljs.foam.field.core :as fc :refer [field]]
            [cljs.core.async :as async :refer [<! put! chan]])
  (:use [cljs.foam.util.generic :only [not-nil?]])
  (:use-macros [cljs.core.async.macros :only [go-loop]]))

(defn error-messages
  [fns value]
  (->> fns
       (map (fn [[f f-msg]]
              (when-not (f value)
                (if (fn? f-msg)
                  (f-msg value)
                  f-msg))))
       (filter #(not-nil? %))))

(defn form-group-class
  [messages]
  (str "form-group"
       (if (empty? messages)
         " has-success"
         " has-error")))

(defcomponentk form-group
  [[:data label id {validations []} :as data] owner [:opts parent-ch]]
  (display-name [_] "form-group")
  (init-state [_]
              {:ch (chan)
               :messages []})
  (will-mount [_]
              (let [ch (om/get-state owner :ch)]
                (go-loop []
                  (let [content (<! ch)
                        new-messages (error-messages validations
                                                     (:value content))]
                    (om/update-state! owner #(assoc %
                                               :messages new-messages
                                               :content content)))
                  (recur))))
  (did-update [_ prev-props prev-state]
              (let [messages (om/get-state owner :messages)
                    content (assoc (om/get-state owner :content)
                              :error (boolean (seq messages)))]
                (when content
                  (put! parent-ch content))))
  (render-state [_ {:keys [ch messages]}]
                (html [:div {:class (form-group-class messages)}
                       [:label {:for (name id)}
                        label]
                       [:div {}
                        (om/build field data {:opts {:parent-ch ch
                                                     :id id}})]
                       (when-not (empty? messages)
                         [:div {:class "help-block with-errors"}
                          [:ul {:class "list-unstyled"}
                           (map-indexed (fn [idx message]
                                          [:li {:key idx}
                                           message])
                                        messages)]])])))
