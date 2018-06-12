(ns user
  (:require [mount.core :as mount]
            blog.core))

(defn start []
  (mount/start-without #'blog.core/repl-server))

(defn stop []
  (mount/stop-except #'blog.core/repl-server))

(defn restart []
  (stop)
  (start))


