(ns cljs.foam.field.core
  (:require [om-tools.core :refer-macros [defcomponentmethod]]
            [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [cljs.core.async :as async :refer [<! put! chan]]
            [cljs.foam.field.input :as fi]
            [cljs.foam.field.textarea :as ft]
            [cljs.foam.field.select :as fs]
            [cljs.foam.field.mixin :as fm]
            [cljs.foam.field.button :as fb])
  (:use-macros [cljs.core.async.macros :only [go-loop]]))

(defmulti field
  (fn [data owner opts]
    (:type data)))

(defcomponentmethod field :text
  [data owner opts]
  (display-name [_] "text-field")
  (render [_]
          (fi/->input data {:opts opts})))

(defcomponentmethod field :password
  [data owner opts]
  (display-name [_] "password-field")
  (render [_]
          (fi/->input data {:opts opts})))

(defcomponentmethod field :email
  [data owner opts]
  (display-name [_] "email-field")
  (render [_]
          (fi/->input data {:opts opts})))

(defcomponentmethod field :url
  [data owner opts]
  (display-name [_] "url-field")
  (render [_]
          (fi/->input data {:opts opts})))

(defcomponentmethod field :file
  [data owner opts]
  (display-name [_] "file-field")
  (render [_]
          (fi/->input data {:opts opts})))

(defcomponentmethod field :textarea
  [data owner opts]
  (display-name [_] "textarea-field")
  (render [_]
          (ft/->textarea data {:opts opts})))

(defcomponentmethod field :select
  [data owner opts]
  (display-name [_] "select-field")
  (render [_]
          (fs/->select data {:opts opts})))

(defcomponentmethod field :button
  [data owner opts]
  (display-name [_] "button-field")
  (render [_]
          (html (fb/->button data {:opts opts}))))
