(ns cljs.foam.field.select
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.core.async :as async :refer [<! put! chan]]
            [om-tools.core :refer-macros [defcomponentk]]
            [cljs.foam.field.mixin :as fm]))

(defcomponentk option
  [[:data value text] owner opts]
  (display-name [_] "option")
  (render [_]
          (html [:option (assoc opts :value value)
                 text])))

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
                       (mapv identity options)])))
