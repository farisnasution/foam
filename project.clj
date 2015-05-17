(defproject foam "0.1.1"
  :description "Form library."
  :url "https://github.com/farisnasution/foam"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-3211"]
                 [org.omcljs/om "0.8.8"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [prismatic/om-tools "0.3.11"]
                 [prismatic/plumbing "0.4.3"]
                 [hiccup "1.0.5"]
                 [sablono "0.3.4"]
                 [figwheel "0.2.6"]]
  :profiles {:dev {:plugins [[lein-cljsbuild "1.0.5"]
                             [jonase/eastwood "0.2.1"]
                             [hiccup-watch "0.1.2"]
                             [lein-figwheel "0.2.6"]
                             [lein-ancient "0.6.7"]
                             [lein-kibit "0.1.2"]
                             [lein-bikeshed "0.2.0"]]}}
  :figwheel {:http-server-root "public"
             :port 3449}
  :jvm-opts ["-Xmx1G"]
  :aliases {"dev" ["cljsbuild" "auto" "dev" "prod"]
            "devfw" ["figwheel" "devfw"]
            "hw" ["hiccup-watch"]
            "omni" ["do" ["au"] ["kibit"] ["eastwood"] ["bikeshed"]]
            "au" ["ancient" "upgrade" ":all"]}
  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/cljs"]
                        :compiler {:output-to "resources/public/js/foam_dev.js"
                                   :output-dir "resources/public/js/outdev"
                                   :optimizations :none
                                   :source-map true
                                   :pretty-print true}}
                       {:id "devfw"
                        :source-paths ["src/figwheel"
                                       "src/cljs"]
                        :compiler {:output-to "resources/public/js/foam_devfw.js"
                                   :output-dir "resources/public/js/outdevfw"
                                   :optimizations :none
                                   :source-map true
                                   :pretty-print true}}
                       {:id "prod"
                        :source-paths ["src/cljs"]
                        :compiler {:output-to "resources/public/js/foam_prod.js"
                                   :optimizations :advanced
                                   :pretty-print false}}]}
  :hiccup-watch {:input-dir "src/hiccup/foam"
                 :output-dir "resources/public"}
  :eastwood {:exclude-linters [:unlimited-use]
             :exclude-namespaces [:test-paths]})
