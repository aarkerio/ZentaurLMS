(defproject zentaur "0.0.7"
  :description "Zentaur. Clojure and ClojureScript LMS."
  :url "http://xalisco-labs.com/"
  :dependencies [[acyclic/squiggly-clojure "0.1.8" :exclusions [org.clojure/tools.reader]]
                 [binaryage/devtools "0.9.10"]           ;; Chrome DevTools enhancements
                 [buddy "2.0.0"]                         ;; Security library for Clojure (sessions)
                 [cheshire "5.8.1"]                      ;; Clojure JSON and BSON encoding/decoding
                 [cljs-ajax "0.8.0"]                     ;; simple Ajax client for ClojureScript and Clojure
                 [clj-commons/secretary "1.2.4"]         ;; A client-side router for ClojureScript.
                 [cljs-http "0.1.45"]                    ;; cljs-http returns core.async channels
                 [clojure.java-time "0.3.2"]             ;; Java 8 Date-Time API for Clojure
                 [com.cognitect/transit-clj "0.8.313"]   ;; Marshalling Transit data to/from Clojure
                 [com.novemberain/pantomime "2.10.0"]    ;; A tiny Clojure library that deals with MIME types
                 [conman "0.8.3"]                        ;; Luminus database connection management and SQL query generation library
                 [cprop "0.1.13"]                        ;; where all configuration properties converge
                 [day8.re-frame/http-fx "0.1.6"]         ;; CLJS framework
                 [digest "1.4.8"]                        ;; Message digest library for Clojure.
                 [funcool/struct "1.3.0"]                ;; Structural validation library for Clojure(Script)
                 [kee-frame "0.3.3" :exclusions [metosin/reitit-core]] ;; re-frame libraries
                 [luminus-immutant "0.2.5"]              ;; Serve web requests using Ring handlers, Servlets, or Undertow HttpHandlers
                 [luminus-migrations "0.6.5"]            ;; transit serialization helpers for Luminus
                 [luminus-transit "0.1.1"]               ;; Transit helpers
                 [markdown-clj "1.10.0"]                 ;; MD support
                 [metosin/muuntaja "0.6.4"]              ;; library for fast http api format negotiation, encoding and decoding.
                 [metosin/reitit "0.3.5"]                ;; A fast data-driven router for Clojure(Script).
                 [metosin/ring-http-response "0.9.1"]    ;; Handling HTTP Statuses with Clojure(Script)
                 [mount "0.1.16"]                        ;; managing Clojure and ClojureScript app state
                 [org.clojure/clojure "1.10.1"]          ;; The sweet core!!
                 [org.clojure/clojurescript "1.10.520" :scope "provided"]
                 [org.clojure/tools.cli "0.4.2"]
                 [org.clojure/tools.logging "0.4.1"]
                 [org.postgresql/postgresql "42.2.5"]
                 [org.webjars.npm/bulma "0.7.4"]                  ;; WebJar for bulma (Bulma is a free, open source CSS framework)
                 [org.webjars.npm/material-icons "0.3.0"]
                 [org.webjars/webjars-locator "0.36"]
                 [org.webjars/webjars-locator-jboss-vfs "0.1.0"]  ;; Extension For The JBoss AS Virtual File System
                 [re-frame "0.10.6"]                     ;;  A Clojurescript MVC-like Framework For Writing SPAs Using Reagent.
                 [re-graph "0.1.9"]                      ;;  Graphql client
                 [reagent "0.8.1"]                       ;;  Minimalistic React for ClojureScript
                 [ring-webjars "0.2.0"]                  ;;  Web assets
                 [ring/ring-core "1.7.1"]                ;;  a very thin HTTP abstraction
                 [ring/ring-defaults "0.3.2"]            ;;  Ring middleware defaults: wrap-multipart-params, wrap-cookies, wrap-flash, etc.
                 [selmer "1.12.12"]                      ;;  Templates
                 [slugify "0.0.1"]]
  :min-lein-version "2.0.0"
  :source-paths ["src/clj" "src/cljs" "src/cljc"]
  :test-paths ["test/clj"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :main ^:skip-aot zentaur.core

  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-immutant "2.1.0"]]  ;;  plugin for deploying/testing Immutant apps with WildFly
  :clean-targets ^{:protect false}
  [:target-path [:cljsbuild :builds :app :compiler :output-dir] [:cljsbuild :builds :app :compiler :output-to]]
  :figwheel
  {:http-server-root "public"
   :server-logfile "log/figwheel-logfile.log"
   :nrepl-port 7002
   :css-dirs ["resources/public/css"]
   :nrepl-middleware [cider.piggieback/wrap-cljs-repl]}

  :profiles
  {:uberjar {:omit-source true
             :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
             :cljsbuild{:builds
              {:min
               {:source-paths ["src/cljc" "src/cljs" "env/prod/cljs"]
                :compiler
                {:output-dir "target/cljsbuild/public/js"
                 :output-to "target/cljsbuild/public/js/app.js"
                 :source-map "target/cljsbuild/public/js/app.js.map"
                 :optimizations :advanced
                 :pretty-print false
                 :infer-externs true
                 :closure-warnings
                 {:externs-validation :off :non-standard-jsdoc :off}
                 :externs ["react/externs/react.js"]}}}}

             :aot :all
             :uberjar-name "zentaur.jar"
             :source-paths ["env/prod/clj"]
             :resource-paths ["env/prod/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {:jvm-opts ["-Dconf=dev-config.edn"]
                  :dependencies [[binaryage/devtools "0.9.10"]
                                 [cider/piggieback "0.4.0"]
                                 [org.clojure/core.typed "0.6.0"]
                                 [doo "0.1.11"]     ;;  library and lein plugin to run cljs.test on different js environments
                                 [expound "0.7.2"]  ;;  Human-optimized error messages for clojure.spec
                                 [figwheel-sidecar "0.5.18"]
                                 [nrepl "0.6.0"]
                                 [pjstadig/humane-test-output "0.9.0"]
                                 [prone "1.6.3"]  ;; Better exception reporting middleware for Ring.
                                 [re-frisk "0.5.4.1"]
                                 [ring/ring-devel "1.7.1"]
                                 [ring/ring-mock "0.4.0"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.24.1"]
                                 [lein-doo "0.1.11"]      ;;  plugin to run clj.test on different js environments
                                 [lein-environ "1.0.0"]   ;; elint for Clojure
                                 [lein-figwheel "0.5.18"]]
                  :squiggly {:checkers [:eastwood]
                              :eastwood-exclude-linters [:unlimited-use]}
                  :cljsbuild{:builds
                   {:app
                    {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
                     :figwheel {:on-jsload "zentaur.core/mount-components"}
                     :compiler
                     {:output-dir "target/cljsbuild/public/js/out"
                      :closure-defines {"re_frame.trace.trace_enabled_QMARK_" true}
                      :optimizations :none
                      :preloads [re-frisk.preload]
                      :output-to "target/cljsbuild/public/js/app.js"
                      :asset-path "/js/out"
                      :source-map true
                      :main "zentaur.app"
                      :pretty-print true}}}}

                  :doo {:build "test"}
                  :source-paths ["env/dev/clj"]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:jvm-opts ["-Dconf=test-config.edn"]
                  :resource-paths ["env/test/resources"]
                  :cljsbuild
                  {:builds
                   {:test
                    {:source-paths ["src/cljc" "src/cljs" "test/cljs"]
                     :compiler
                     {:output-to "target/test.js"
                      :main "zentaur.doo-runner"
                      :optimizations :whitespace
                      :pretty-print true}}}}
                  }
   :profiles/dev {}
   :profiles/test {}})
