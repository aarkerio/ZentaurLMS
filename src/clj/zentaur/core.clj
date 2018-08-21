(ns zentaur.core
  (:require [zentaur.handler :as handler]
            [zentaur.config :refer [env]]
            [luminus.repl-server :as repl]
            [luminus.http-server :as http]
            [luminus-migrations.core :as migrations]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.tools.logging :as log]
            [mount.core :as mount]
            [clojure.tools.nrepl.server :as nrepl-server]
            [cider.nrepl :refer (cider-nrepl-handler)])
  (:gen-class))

;; https://github.com/clojure/tools.cli
(def cli-options
  [["-p" "--port PORT" "Port number"
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 1000) "Must be a number between 0 and 1000"]]])

(mount/defstate ^{:on-reload :noop}
                http-server
                :start
                (http/start
                  (-> env
                      (assoc :handler (handler/app))
                      (update :port #(or (-> env :options :port) %))))
                :stop
                (http/stop http-server))

(mount/defstate ^{:on-reload :noop}
                repl-server
                :start
                (when-let [nrepl-port (env :nrepl-port)]
                  (repl/start {:port nrepl-port}))
                :stop
                (when repl-server
                  (repl/stop repl-server)))


(defn stop-app []
  (doseq [component (:stopped (mount/stop))]
    (log/info component "stopped"))
  (shutdown-agents))

(defn start-app [args]
  (doseq [component (-> args
                        (parse-opts cli-options)
                        mount/start-with-args
                        :started)]
    (log/info component ">>>>>>>>>>>>>>> ZentaurLMS started ##########"))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn -main [& args]
  (cond
    (some #{"init"} args)
    (do
      (mount/start #'zentaur.config/env)
      (migrations/init (select-keys env [:database-url :init-script]))
      (System/exit 0))
    (some #{"migrate" "rollback"} args)
    (do
      (mount/start #'zentaur.config/env)
      (migrations/migrate args (select-keys env [:database-url]))
      (System/exit 0))
    :else
    (start-app args)))
