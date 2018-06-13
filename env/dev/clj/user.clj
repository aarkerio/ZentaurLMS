(ns user
  (:require [mount.core :as mount]
            zentaur.core))

(defn start []
  (mount/start-without #'zentaur.core/repl-server))

(defn stop []
  (mount/stop-except #'zentaur.core/repl-server))

(defn restart []
  (stop)
  (start))


