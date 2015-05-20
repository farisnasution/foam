(ns cljs.foam.field.textarea
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.core.async :as async :refer [<! put! chan]]
            [om-tools.core :refer-macros [defcomponentk]]
            [cljs.foam.field.mixin :as fm]))

(defcomponentk textarea
  [[:data {value ""} id] owner [:opts parent-ch]]
  (:mixins fm/field-mixin)
  (display-name [_] "textarea")
  (init-state [_]
              (let [ch (chan)]
                {:ch ch
                 :parent-ch parent-ch
                 :callback-fn #(put! ch %)
                 :id id
                 :field-value value
                 :events ["blur" "keyup"]}))
  (render-state [_ {:keys [field-value]}]
                (html [:textarea {:class "form-control"
                                  :id (name id)
                                  :value field-value
                                  :onChange (fn [e])}])))
