(ns cljs.foam.field.button
  (:require [om-tools.core :refer-macros [defcomponentk]]
            [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.foam.util.event :as ue]
            [cljs.core.async :as async :refer [<! put! chan]])
  (:use-macros [cljs.core.async.macros :only [go-loop]]))

(defcomponentk button
  [[:data id text disabled classes] owner [:opts parent-ch]]
  (display-name [_] "button")
  (init-state [_]
              (let [ch (chan)]
                {:ch ch
                 :callback-fn (fn [event]
                                (ue/prevent-default event)
                                (put! ch event))}))
  (will-mount [_]
              (let [ch (om/get-state owner :ch)]
                (go-loop []
                  (let [event (<! ch)]
                    (put! parent-ch {:id id
                                     :event event}))
                  (recur))))
  (did-mount [_]
             (let [callback-fn (om/get-state owner :callback-fn)
                   node (om/get-node owner)]
               (ue/listen node "click" callback-fn)))
  (will-unmount [_]
                (let [callback-fn (om/get-state owner :callback-fn)
                      node (om/get-node owner)]
                  (ue/unlisten node "click" callback-fn)))
  (render [_]
          (html [:button {:style {:border-radius 0}
                          :type "submit"
                          :disabled disabled
                          :class (str "btn " classes)}
                 text])))
