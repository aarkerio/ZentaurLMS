(defproject zentaur "0.0.4"
  :description "Zentaur. Clojure and ClojureScript LMS."
  :url "http://chipotle-software.com/"
  :dependencies [[buddy "2.0.0"]                         ;; Security library for Clojure (sessions)
                 [buddy/buddy-auth "2.1.0"]              ;; Authentication
                 [cheshire "5.8.0"]                      ;; Clojure JSON and BSON encoding/decoding
                 [cider/cider-nrepl "0.16.0"]
                 [clj-time "0.14.0"]                     ;; date time-zone library
                 [cljs-ajax "0.7.3"]                     ;; Ajax
                 [com.billpiel/sayid "0.0.15"]           ;; clojure debugger
                 [com.cognitect/transit-cljs "0.8.243"]  ;; String -> Transit
                 [com.googlecode.log4jdbc/log4jdbc "1.2"]
                 [com.novemberain/pantomime "2.10.0"]
                 [compojure "1.6.0"]                     ;; routes for ring
                 [conman "0.7.9"]                        ;; Luminus database connection management and SQL query generation library
                 [cprop "0.1.11"]                        ;; Read properties, environments, configs, profiles
                 [digest "1.4.8"]                        ;; Message digest library for Clojure.
                 [domina "1.0.3"]                        ;; A DOM manipulation library for ClojureScript
                 [funcool/bide "1.6.0"]                  ;; A simple routing library for ClojureScript
                 [funcool/struct "1.1.0"]                ;; database validation
                 [hiccup "1.0.5"]                        ;; templates
                 [org.immutant/web "2.1.10"]             ;; libraries Ring + Undertow
                 [luminus-migrations "0.4.2"]            ;; migratus esentially
                 [luminus/ring-ttl-session "0.3.2"]      ;; ring.middleware.session.store library
                 [markdown-clj "1.0.1"]                  ;; parses md files
                 [metosin/compojure-api "1.1.11"]
                 [metosin/muuntaja "0.4.1"]              ;; library for fast http api format negotiation, encoding and decoding.
                 [metosin/ring-http-response "0.9.0"]    ;; Handling HTTP Statuses with Clojure(Script)
                 [mount "0.1.11"]
                 [org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.238" :scope "provided"]
                 [org.clojure/java.jdbc "0.7.1"]
                 [org.clojure/tools.cli "0.3.5"]
                 [org.clojure/tools.logging "0.4.0"]
                 [org.clojure/tools.reader "1.1.0"]
                 [org.postgresql/postgresql "42.2.2"]
                 [reagent "0.8.1"]                       ;;  Minimalistic React for ClojureScript
                 [ring/ring-core "1.6.3"]                ;;  a very thin HTTP abstraction
                 [ring/ring-codec "1.1.0"]               ;;  encoding and decoding into formats used in web
                 [ring/ring-defaults "0.3.2"]            ;;  Ring middleware defaults: wrap-multipart-params, wrap-cookies, wrap-flash, etc.
                 [ring/ring-mock "0.3.2"]                ;; library for creating Ring request maps for testing purposes.
                 [ring-middleware-format "0.7.2"]        ;;  Middleware json + transit requests
                 [selmer "1.11.7"]                       ;;  Simple HTML Templates
                 [slugify "0.0.1"]]
  :min-lein-version "2.8.0"
  :jvm-opts ["-server" "-Dconf=.lein-env"]
  :source-paths ["src/clj" "src/cljc"]
  :test-paths ["src/specs"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :main ^:skip-aot zentaur.core
  :migratus {:store :database :classname "net.sf.log4jdbc.DriverSpy" :db ~(get (System/getenv) "DATABASE_URL")}
  :plugins [[lein-cprop "1.0.3"]
            [migratus-lein "0.5.2"]
            [lein-cljsbuild "1.1.7"]
            [lein-kibit "0.1.5"]           ;; rubocop for clojure
            [lein-immutant "2.1.0"]]
  :clean-targets ^{:protect false}
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
            {:output-to "resources/public/js/app.js"
             :optimizations :advanced
             :pretty-print false
             :closure-warnings
             {:externs-validation :off :non-standard-jsdoc :off}}}}}
         :aot :all
         :uberjar-name "zentaur.jar"
         :source-paths ["env/prod/clj"]
         :resource-paths ["env/prod/resources"]}

    :dev  [:project/dev :profiles/dev]
    :test [:project/dev :project/test :profiles/test]

    :project/dev {
      :dependencies [[binaryage/devtools "0.9.4"]
                     [com.cemerick/piggieback "0.2.2"]    ;; nREPL support for ClojureScript REPLs
                     [doo "0.1.8"]                        ;; doo is a library and lein plugin to run cljs.test on different js environments.
                     [figwheel-sidecar "0.5.14"]
                     [funcool/bide "1.6.0"]               ;; A simple routing library for ClojureScript
                     [midje "1.9.2"]                      ;; TDD for Clojure
                     [org.clojure/test.check "0.9.0"]
                     [pjstadig/humane-test-output "0.8.3"]
                     [prone "1.1.4"]                      ;; Better exception reporting middleware for Ring.
                     [ring/ring-mock "0.3.1"]             ;; Mocking request
                     [ring/ring-devel "1.6.2"]]
      :plugins      [[com.jakemccrary/lein-test-refresh "0.19.0"]
                     [lein-doo "0.1.8"]
                     [lein-figwheel "0.5.14"]
                     [org.clojure/clojurescript "1.9.946"]]
      :cljsbuild {
        :builds {
         :app {
          :id "dev"
          :source-paths ["src/cljs" "env/dev/cljs"]
          :figwheel {
            :on-jsload "zentaur.app/main" }  ;; the path to the main function (launcher)
          :compiler {
            :main "zentaur.app"
            :asset-path "/js/out"
            :output-to "resources/public/js/app.js"
            :output-dir "resources/public/js/out"
            :source-map true
            :optimizations :none
            :pretty-print true}}}}  ;; / cljsbuild ends

      :doo {:build "test"}
      :source-paths ["env/dev/clj"]
      :resource-paths ["env/dev/resources"]
      :repl-options {:init-ns zentaur.core
                     ;; If nREPL takes too long to load it may timeout,
                     ;; increase this to wait longer before timing out.
                     ;; Defaults to 30000 (30 seconds)
                     :timeout 120000
                     }
      :injections [(require 'pjstadig.humane-test-output)
                   (pjstadig.humane-test-output/activate!)] } ;; /project/dev ends

    :project/test {
      :resource-paths ["env/test/resources"]
      :cljsbuild {
        :builds {
          :test {
            :source-paths ["src/cljc" "src/cljs" "test/cljs"]
            :compiler {
              :output-to "target/test.js"
              :main "zentaur.doo-runner"
              :optimizations :whitespace
              :pretty-print true}}}}}
    })
