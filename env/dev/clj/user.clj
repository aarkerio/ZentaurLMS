(ns user
  (:require [zentaur.config :refer [env]]
            [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [mount.core :as mount]
            [zentaur.core :refer [start-app]]
            [zentaur.db.core]
            [conman.core :as conman]
            [luminus-migrations.core :as migrations]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(defn start []
  ;; (mount/start-without #'zentaur.core/repl-server)  DEPRECATED by CIDER
  )

(defn stop []
  ;; (mount/stop-except #'zentaur.core/repl-server)  DEPRECATED by CIDER
  )

(defn restart []
  (stop)
  (start))

(defn restart-db []
  (mount/stop #'zentaur.db.core/*db*)
  (mount/start #'zentaur.db.core/*db*)
  (binding [*ns* 'zentaur.db.core]
    (conman/bind-connection zentaur.db.core/*db* "sql/queries.sql" "sql/users.sql")))

(defn reset-db []
  (migrations/migrate ["reset"] (select-keys env [:database-url])))

(defn migrate []
  (migrations/migrate ["migrate"] (select-keys env [:database-url])))

(defn rollback []
  (migrations/migrate ["rollback"] (select-keys env [:database-url])))

(defn create-migration [name]
  (migrations/create name (select-keys env [:database-url])))


