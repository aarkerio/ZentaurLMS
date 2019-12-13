(defproject zentaur "0.0.8"
  :description "Zentaur. Clojure and ClojureScript LMS."
  :url "http://xalisco-labs.com/"
  :dependencies [[buddy "2.0.0"]                         ;; Security library for Clojure (sessions)
                 [cheshire "5.9.0"]                      ;; Clojure JSON and BSON encoding/decoding
                 [cljs-ajax "0.8.0"]                     ;; simple Ajax client for ClojureScript and Clojure
                 [clj-commons/secretary "1.2.4"]         ;; A client-side router for ClojureScript.
                 [cljs-http "0.1.46"]                    ;; cljs-http returns core.async channels
                 [clojure.java-time "0.3.2"]             ;; Java 8 Date-Time API for Clojure
                 [com.cognitect/transit-clj "0.8.319"]   ;; Marshalling Transit data to/from Clojure
                 [com.novemberain/pantomime "2.11.0"]    ;; A tiny Clojure library that deals with MIME types
                 [conman "0.8.4"]                        ;; Luminus database connection management and SQL query generation library
                 [cprop "0.1.14"]                        ;; where all configuration properties converge
                 [digest "1.4.9"]                        ;; Message digest library for Clojure.
                 [funcool/struct "1.4.0"]                ;; Structural validation library for Clojure(Script)
                 [kee-frame "0.3.3" :exclusions [metosin/reitit-core]] ;; re-frame libraries
                 [luminus-immutant "0.2.5"]              ;; Serve web requests using Ring handlers, Servlets, or Undertow HttpHandlers
                 [luminus-migrations "0.6.5"]            ;; Migrations library for Luminus
                 [luminus-transit "0.1.2"]               ;; Transit helpers
                 [markdown-clj "1.10.0"]                 ;; MD support
                 [metosin/muuntaja "0.6.6"]              ;; library for fast http api format negotiation, encoding and decoding.
                 [metosin/reitit "0.3.10"]                ;; A fast data-driven router for Clojure(Script).
                 [metosin/ring-http-response "0.9.1"]    ;; Handling HTTP Statuses with Clojure(Script)
                 [mount "0.1.16"]                        ;; managing Clojure and ClojureScript app state
                 [org.clojure/clojure "1.10.1"]          ;; The sweet core!!
                 [org.clojure/tools.cli "0.4.2"]
                 [org.clojure/tools.logging "0.4.1"]
                 [org.postgresql/postgresql "42.2.8"]
                 [org.webjars.npm/bulma "0.7.1"]         ;; WebJar for bulma (Bulma is a free, open source CSS framework)
                 [re-frame "0.11.0-rc3"]                 ;;  A Reagent Framework For Writing SPAs, in Clojurescript.
                 [reagent "0.9.0-rc3"]                   ;;  Minimalistic React for ClojureScript
                 [ring-webjars "0.2.0"]                  ;;  Web assets
                 [ring/ring-core "1.8.0"]                ;;  a very thin HTTP abstraction
                 [ring/ring-defaults "0.3.2"]            ;;  Ring middleware defaults: wrap-multipart-params, wrap-cookies, wrap-flash, etc.
                 [selmer "1.12.12"]                      ;;  Templates
                 [slugify "0.0.1"]]
  :managed-dependencies [[org.clojure/core.rrb-vector "0.0.13"]
                         [org.flatland/ordered "1.5.7"]]
  :min-lein-version "2.0.0"
  :source-paths ["src/clj" "src/cljs" "src/cljc" "target"]
  :test-paths ["test/clj"]
  :resource-paths ["resources" "target/cljsbuild"]
  :aliases {"fig" ["trampoline" "run" "-m" "figwheel.main"]
            "fig:dev" ["trampoline" "run" "-m" "figwheel.main" "--" "--build" "dev" "--repl"]
            "fig:deploy" ["run" "-m" "figwheel.main" "-O" "advanced" "-bo" "deploy"]}
  :target-path "target/%s/"
  :main ^:skip-aot zentaur.core
  :migratus {:store :database}
  :plugins [[migratus-lein "0.7.2"]]  ;;  plugin for deploying/testing Immutant apps with WildFly
  :clean-targets ^{:protect false}
  [:target-path [:builds :app :compiler :output-to]]
  :profiles {
            :uberjar {:omit-source true
                      :prep-tasks ["compile" ["once" "min"]]
                      :aot :all
                      :uberjar-name "zentaur.jar"
                      :source-paths ["env/prod/clj"]
                      :resource-paths ["env/prod/resources"]}
            :dev      [:project/dev :profiles/dev]
            :test     [:project/dev :project/test :profiles/test]

            :project/dev  {:jvm-opts ["-Dconf=dev-config.edn"]
                           :dependencies [[binaryage/devtools "0.9.11"]              ;; CLJS DevTools
                                          [com.bhauman/figwheel-main "0.2.3"]        ;; Hot Reload cljs
                                          [com.bhauman/rebel-readline-cljs "0.1.4"]  ;; Terminal readline library for Clojure dialects
                                          [day8.re-frame/re-frame-10x "0.4.5"]       ;;  Debugging re-frame applications.
                                          [doo "0.1.11"]                        ;;  library and lein plugin to run cljs.test on different js environments
                                          [nrepl "0.6.0"]                       ;;  nREPL is a Clojure network REPL that provides a REPL server and client
                                          [org.clojure/clojurescript "1.10.597"]
                                          [prone "2019-07-08"]                  ;;  Better exception reporting middleware for Ring.
                                          [re-frisk "0.5.4"]                    ;;  Visualize re-frame pattern data, watch re-frame events and export state in the debugger.
                                          [ring/ring-devel "1.8.0"]             ;;  Ring dev options
                                          [ring/ring-mock "0.4.0"]]             ;;  Library to create mock Ring requests for unit tests
                           :source-paths ["env/dev/clj" "target" "env/dev/cljs"]
                           :resource-paths ["env/dev/resources"]
                           :repl-options {:init-ns user}}
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
                      :pretty-print true}}}}}
             :profiles/dev {}
             :profiles/test {}})
