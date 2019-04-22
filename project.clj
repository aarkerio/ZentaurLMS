(defproject zentaur "0.0.6"
  :description "Zentaur. Clojure and ClojureScript LMS."
  :url "http://xalisco-labs.com/"
  :dependencies [[baking-soda "0.2.0"]                   ;; interface between clojurescript's reagent and bootstrap react components
                 [binaryage/devtools "0.9.10"]           ;; Chrome DevTools enhancements
                 [buddy "2.0.0"]                         ;; Security library for Clojure (sessions)
                 [buddy/buddy-auth "2.1.0"]              ;; Authentication
                 [cheshire "5.8.0"]                      ;; Clojure JSON and BSON encoding/decoding
                 [clj-time "0.14.0"]                     ;; date time-zone library
                 [cljs-ajax "0.7.4"]                     ;; Ajax
                 [cljs-http "0.1.45"]                    ;; cljs-http returns core.async channels
                 [cljsjs/jquery "3.2.1-0"]               ;; jQuery
                 [com.cognitect/transit-clj "0.8.313"]   ;; JSON on steroids
                 [com.fasterxml.jackson.core/jackson-core "2.9.6"]  ;; Streaming API, implementation for JSON
                 [com.fasterxml.jackson.datatype/jackson-datatype-joda "2.9.6"]  ;; time formats
                 [com.googlecode.log4jdbc/log4jdbc "1.2"]
                 [com.novemberain/pantomime "2.10.0"]    ;; A tiny Clojure library that deals with MIME types
                 [com.rpl/specter "1.1.1"]               ;; querying and transforming nested and recursive data
                 [compojure "1.6.1"]                     ;; routes for ring
                 [conman "0.8.2"]                        ;; Luminus database connection management and SQL query generation library
                 [cprop "0.1.11"]                        ;; where all configuration properties converge
                 [day8.re-frame/http-fx "0.1.6"]         ;; Ajax for re-frame
                 [day8.re-frame/async-flow-fx "0.0.11"]  ;; async control flow
                 [digest "1.4.8"]                        ;; Message digest library for Clojure.
                 [factory-time "0.1.2"]                  ;; Factory girl for clojure
                 [funcool/bide "1.6.0"]                  ;; A simple routing library for ClojureScript
                 [funcool/struct "1.3.0"]                ;; database validation
                 [hiccup "1.0.5"]                        ;; HTML render
                 [org.immutant/web "2.1.10"]             ;; libraries Ring + Undertow
                 [luminus-migrations "0.5.3"]            ;; migratus esentially
                 [luminus/ring-ttl-session "0.3.2"]      ;; ring.middleware.session.store library
                 [markdown-clj "1.0.2"]                  ;; parses md files
                 [metosin/compojure-api "1.1.11"]        ;; Sweet web apis with Compojure & Swagger
                 [metosin/muuntaja "0.6.0"]              ;; library for fast http api format negotiation, encoding and decoding.
                 [metosin/ring-http-response "0.9.0"]    ;; Handling HTTP Statuses with Clojure(Script)
                 [mount "0.1.13"]                        ;; managing Clojure and ClojureScript app state
                 [nrepl "0.6.0"]
                 [org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.439" :scope "provided"]
                 [org.clojure/java.jdbc "0.7.1"]
                 [org.clojure/tools.cli "0.3.5"]
                 [org.clojure/tools.logging "0.4.0"]
                 [org.clojure/tools.reader "1.3.0"]      ;;  Clojure reader and an EDN-only reader
                 [org.clojars.frozenlock/reagent-modals "0.2.8"]  ;; Bootstrap Modals
                 [org.postgresql/postgresql "42.2.2"]
                 [reagent "0.8.2-SNAPSHOT"]              ;;  Minimalistic React for ClojureScript
                 [re-frame "0.10.6"]                     ;;  A Clojurescript MVC-like Framework For Writing SPAs Using Reagent.
                 [ring/ring-core "1.6.3"]                ;;  a very thin HTTP abstraction
                 [ring/ring-codec "1.1.0"]               ;;  encoding and decoding into formats used in web
                 [ring/ring-defaults "0.3.2"]            ;;  Ring middleware defaults: wrap-multipart-params, wrap-cookies, wrap-flash, etc.
                 [ring-middleware-format "0.7.2"]        ;;  Middleware json + transit requests
                 [ring/ring-mock "0.3.2"]                ;;  library for creating Ring request maps for testing purposes.
                 [ring-webjars "0.2.0"]                  ;;  Web assets
                 [secretary "1.2.3"]                     ;;  A client-side router for ClojureScript.
                 [selmer "1.11.7"]                       ;;  Simple HTML Templates
                 [slugify "0.0.1"]]
  :min-lein-version "2.8.0"
  :source-paths ["src/clj" "src/cljc"]
  :test-paths ["test/clj"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :main ^:skip-aot zentaur.core
  :migratus {:store :database :classname "net.sf.log4jdbc.DriverSpy" :db ~(get (System/getenv) "DATABASE_URL")}
  :plugins [[cider/cider-nrepl "LATEST"]
            [com.jakemccrary/lein-test-refresh "LATEST"]  ;; faster tests
            [lein-auto "LATEST"]
            [lein-cprop "LATEST"]          ;; loads configuration
            [lein-cljsbuild "LATEST"]
            [lein-figwheel "LATEST"]
            [lein-sassc "LATEST"]
            [lein-kibit "LATEST"]           ;; rubocop for clojure
            [migratus-lein "LATEST"]]       ;; migrate everything!!
  :sassc [{:src "resources/scss/styles.scss"
           :output-to "resources/public/css/styles.css"
           :style "nested"
           :import-path "resources/scss"}]
  :auto {"sassc" {:file-pattern #"\.(scss|sass)$" :paths ["resources/scss"]}}
  :hooks [leiningen.sassc]
  :clean-targets ^{:protect false :doc "Keeps the cache clean"}
  [:target-path [:cljsbuild :builds :app :compiler :output-dir] [:cljsbuild :builds :app :compiler :output-to]]
  :aliases {"test-all" ["do" ["test"] ["specs"]]}
  :profiles {
             :uberjar {
                       :omit-source true
                       :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
                       :cljsbuild
                       {:builds
                        {:min
                         {:source-paths ["src/cljs" "env/prod/cljs"]
                          :compiler
                          :modules {:home {:entries #{"zentaur.core"}      :output-to "out/home.js"}
                                    :site {:entries #{"zentaur.site.core"} :output-to "out/site.js"}
                                    :tests {:entries #{"zentaur.tests.core"} :output-to "out/tests.js"}}
                          {:output-to "resources/public/js/app.js"
                           :optimizations :advanced
                           :pretty-print false
                           :closure-warnings
                           {:externs-validation :off :non-standard-jsdoc :off}}}}}
                       :aot :all
                       :uberjar-name "zentaur.jar"
                       :source-paths ["env/prod/clj"]
                       :resource-paths ["env/prod/resources"]}  ;; uberjar ends

             :dev  [:project/dev]
             :test [:project/dev :project/test]

             :project/dev {
                           :jvm-opts ["-Dconf=dev-config.edn"]
                           :dependencies [[com.cemerick/piggieback "LATEST"]      ;; nREPL support for ClojureScript REPLs
                                          [doo "LATEST"]                          ;; doo is a library and lein plugin to run cljs.test on different js environments.
                                          [expound "LATEST"]                      ;; Human-optimized error messages for clojure.spec
                                          [figwheel-sidecar "LATEST"]
                                          [funcool/bide "LATEST"]                 ;; A simple routing library for ClojureScript
                                          [org.clojure/test.check "LATEST"]
                                          [pjstadig/humane-test-output "LATEST"]  ;; Humane test output for clojure.test.
                                          [prone "LATEST"]                        ;; Better exception reporting middleware for Ring.
                                          [ring/ring-mock "LATEST"]               ;; Mocking request
                                          [ring/ring-devel "LATEST"]]
                           :plugins      [[com.jakemccrary/lein-test-refresh "LATEST"]
                                          [cider/cider-nrepl "LATEST"]
                                          [lein-doo "LATEST"]
                                          [venantius/ultra "LATEST"]]
                           :cljsbuild {
                                       :builds {
                                                :app {
                                                      :id "dev"
                                                      :source-paths ["src/cljs" "env/dev/cljs"]
                                                      :figwheel {
                                                                 :on-jsload "zentaur.app/main" }  ;; the path to the main function (launcher)
                                                      :compiler {
                                                                 :preloads [devtools.preload]
                                                                 :asset-path "/js/out"
                                                                 :output-dir "resources/public/js/out"
                                                                 :output-to  "resources/public/js/main.js"
                                                                 :modules {:home  {:entries #{"zentaur.core"}       :output-to "resources/public/js/out/home.js"}
                                                                           :site  {:entries #{"zentaur.site.core"}  :output-to "resources/public/js/out/site.js"}
                                                                           :tests {:entries #{"zentaur.tests.core"} :output-to "resources/public/js/out/tests.js"}
                                                                           :cljs-base {:output-to "resources/public/js/out/cljs_base.js"}}
                                                                 :main "zentaur.app"
                                                                 :source-map true
                                                                 :optimizations :none
                                                                 :verbose true
                                                                 :pretty-print true}}}}  ;; / cljsbuild ends
                           :doo {:build "test"}
                           :source-paths ["env/dev/clj"]
                           :resource-paths ["env/dev/resources"]
                           :repl-options {:init-ns zentaur.core
                                          ;; If nREPL takes too long to load it may timeout,
                                          ;; increase this to wait longer before timing out.
                                          ;; Defaults to 30000 (30 seconds)
                                          :timeout 120000
                                          }} ;; /project/dev ends

             :project/test {
                            :resource-paths ["env/test/resources"]
                            :jvm-opts ["-Dconf=test-config.edn"]
                            :test-paths ["test"]
                            :injections [(require 'pjstadig.humane-test-output)
                                         (pjstadig.humane-test-output/activate!)]
                            :cljsbuild {
                                        :builds {
                                                 :test {
                                                        :source-paths ["src/cljc" "src/cljs" "test/cljs"]
                                                        :compiler {
                                                                   :output-to "target/test.js"
                                                                   :main "zentaur.doo-runner"
                                                                   :optimizations :whitespace
                                                                   :pretty-print true}}}}}  ;; project/test ends
             })
