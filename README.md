# foam

Om form on steroids.


### Todo

Add more field and example.

## Usage

```clj
(ns foobar.core
  (:require [om.core :as om :include-macros true]
            [cljs.foam.core :as foam]))

(def login-form
  [{:id :username
    :type :text
    :validations [[(fn [v]
                     (not (empty? v)))
                   "Cannot Empty"]
                  [(fn [v]
                     (> (count v) 5))
                   (fn [v]
                     (str "Length must be greater than 5. Current" (count v)))]]}
   {:id :password
    :type :password}])

(om/root foam/form
         {:fields
          :butotn}
         {:target (.getElementById js/document "main")})

```

## License

Copyright © 2015 Faris Nasution <faris.nasution156@gmail.com>

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
