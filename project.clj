(defproject zentaur "0.0.11"
  :description "Zentaur. Clojure and ClojureScript LMS."
  :url "http://xalisco-labs.com/"
  :dependencies [[buddy "2.0.0"]                         ;; Security library for Clojure (sessions)
                 [cheshire "5.9.0"]                      ;; Clojure JSON and BSON encoding/decoding
                 [cljs-ajax "0.8.0"]                     ;; Simple Ajax client for ClojureScript and Clojure
                 [clj-commons/secretary "1.2.4"]         ;; A client-side router for ClojureScript.
                 [cljs-http "0.1.46"]                    ;; cljs-http returns core.async channels
                 [clj-pdf "2.4.0"]                       ;; PDF generation library
                 [clojure.java-time "0.3.2"]             ;; Java 8 Date-Time API for Clojure
                 [crypto-random "1.2.0"]                 ;; generating cryptographically secure random bytes and strings
                 [com.cognitect/transit-clj "0.8.319"]   ;; Marshalling Transit data to/from Clojure
                 [com.novemberain/pantomime "2.11.0"]    ;; A tiny Clojure library that deals with MIME types
                 [com.walmartlabs/lacinia "0.36.0-alpha-4"] ;; GraphQL implementation in pure Clojure
                 [conman "0.8.4"]                        ;; Luminus database connection management and SQL query generation library
                 [cprop "0.1.14"]                        ;; where all configuration properties converge
                 [day8.re-frame/http-fx "v0.2.0"]        ;; A re-frame effects handler for performing Ajax tasks
                 [digest "1.4.9"]                        ;; Message digest library for Clojure.
                 [funcool/struct "1.4.0"]                ;; Structural validation library for Clojure(Script)
                 [hiccup "1.0.5"]                        ;; HTML templates
                 [luminus-migrations "0.6.5"]            ;; Migrations library for Luminus
                 [luminus-transit "0.1.2"]               ;; Transit helpers
                 [luminus/ring-ttl-session "0.3.3" :exclusions [joda-time clj-time]] ;; Ring's TTL (time to live) session
                 [markdown-clj "1.10.1"]                 ;; MD support
                 [metosin/muuntaja "0.6.6"]              ;; library for fast http api format negotiation, encoding and decoding.
                 [metosin/reitit "0.4.1" :exclusions [joda-time clj-time borkdude/edamame]] ;; A fast data-driven router for Clojure(Script).
                 [metosin/ring-http-response "0.9.1" :exclusions [joda-time]]    ;; Handling HTTP Statuses with Clojure(Script)
                 [mount "0.1.16"]                        ;; Managing Clojure and ClojureScript app state
                 [org.clojure/clojure "1.10.1"]          ;; The sweet core!!
                 [org.clojure/clojurescript "1.10.597"]
                 [org.clojure/tools.cli "0.4.2"]         ;; parses command line arguments and stuff like that
                 [org.clojure/tools.logging "0.4.1"]     ;; Logs duh!
                 [org.immutant/web "2.1.10" :exclusions [joda-time]] ;; Serve web requests using Ring handlers, Servlets, or Undertow HttpHandlers
                 [org.postgresql/postgresql "42.2.9"]    ;; PostgreSQL rulez!
                 [re-frame "0.11.0"]                     ;; A Reagent Framework For Writing SPAs, in Clojurescript.
                 [reagent "0.9.1"]                       ;; Minimalistic React for ClojureScript
                 [re-graph "0.1.11" :exclusions [org.eclipse.jetty/jetty-http]] ;; A graphql client for clojurescript and clojure
                 [ring-webjars "0.2.0" :exclusions [joda-time clj-time]] ;; Web assets
                 [ring/ring-core "1.8.0"]                ;; A very thin HTTP abstraction
                 [ring/ring-defaults "0.3.2" :exclusions [joda-time clj-time]]] ;; Ring middleware defaults: wrap-multipart-params, wrap-cookies, wrap-flash, etc.
  :managed-dependencies [[org.clojure/core.rrb-vector "0.0.13"]]  ;; necessary for JDK 11
  :min-lein-version "2.9.0"    ;; current CIDER needs 2.9 or +
  :source-paths ["src/clj" "src/cljs" "src/cljc"]
  :test-paths ["test/clj"]
  :resource-paths ["resources"]
  :paths ["src" "resources"]
  :aliases {"fig" ["trampoline" "run" "-m" "figwheel.main"]
            "fig:dev" ["trampoline" "run" "-m" "figwheel.main" "--" "--build" "dev" "--repl"]
            "fig:deploy" ["run" "-m" "figwheel.main" "-O" "advanced" "-bo" "dev"]
            "l:test" ["test" ":only" "zentaur.model.tests-test/create-test!"]
            "l:bl" ["test" ":only" "business-logic"]
            "tree" ["deps" ":tree"]}
  :target-path "target/%s/"
  :main ^:skip-aot zentaur.core
  :migratus {:store :database}
  :plugins [[migratus-lein "0.7.2"]]  ;;  plugin for deploying/testing Immutant apps with WildFly
  :clean-targets ^{:protect false}
  [:target-path [:builds :app :compiler :output-to]]
  :profiles {
            :uberjar {:omit-source true
                      :prep-tasks ["javac" "compile"]
                      :aot :all
                      :uberjar-name "zentaur.jar"
                      :source-paths ["env/prod/clj" "env/prod/cljs"]
                      :resource-paths ["env/prod/resources"]}
            :dev      [:project/dev :profiles/dev]
            :test     [:project/dev :project/test :profiles/test]

            :project/dev  {:jvm-opts ["-Dconf=dev-config.edn" "--illegal-access=warn"]
                           :dependencies [[binaryage/devtools "0.9.11"]              ;; CLJS DevTools
                                          [com.bhauman/rebel-readline-cljs "0.1.4"]  ;; Terminal readline library for Clojure dialects
                                          [com.bhauman/figwheel-main "0.2.3" :exclusions [joda-time clj-time]]  ;; Hot Reload cljs
                                          [day8.re-frame/re-frame-10x "0.5.1"]       ;; Debugging re-frame applications.
                                          [doo "0.1.11"]                             ;; Library and lein plugin to run cljs.test on different JS environments
                                          [enlive "1.1.6"]
                                          [factory-time "0.1.2"]                     ;; Factory-bot like library for tests
                                          [prone "2019-07-08"]                       ;; Better exception reporting middleware for Ring.
                                          [ring/ring-devel "1.8.0"]                  ;; Ring dev options
                                          [ring/ring-mock "0.4.0"]]                  ;; Library to create mock Ring requests for unit tests
                           :source-paths ["env/dev/clj" "target" "env/dev/cljs"]
                           :resource-paths ["env/dev/resources"]
                           :repl-options {:init-ns user :timeout 120000}}
             :project/test {:jvm-opts ["-Dconf=test-config.edn" "--illegal-access=warn"]
                            :dependencies [[com.jakemccrary/lein-test-refresh "0.24.1"]
                                           [lein-autoexpect "1.9.0"]]
                            :resource-paths ["env/test/resources"]
                            :source-paths ["env/test/clj" "test/clj"]
                            :test-selectors {:default (complement :integration)
                                             :integration :integration
                                             :business-logic :business-logic}}
             :profiles/dev {}
             :profiles/test {}})
