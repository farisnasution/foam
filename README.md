# foam

Om form on steroids.


### Todo

Add more field and example.

## Usage

for each field in `:fields`, there are several mandatory keys:

* `:id`
* `:type`
* `:label`

and in `:button`:

* `:id`

```clj
(ns foobar.core
  (:require [om.core :as om :include-macros true]
            [cljs.foam.core :as foam]))

(def login-form
  {:fields [{:id :username
             :type :text
             :validations [[(fn [whole v]
                              (not (empty? v)))
                            "Cannot Empty"]
                           [((fn [arg-list] ) [whole v]
                             (> (count v) 5))
                            (fn [whole v]
                              (str "Length must be greater than 5. Current" (count v)))]]}
            {:id :password
             :type :password
             :label "Password"}]
   :button {:id :submitbutton
            :text "Submit"
            :classes "btn-primary"}})

(om/build foam/form
          login-form
          {:opts {:submit-fn (fn [owner value]
                               (js/console.log value))}})

```

## License

Copyright © 2015 Faris Nasution <faris.nasution156@gmail.com>

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
