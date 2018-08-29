(ns zentaur.libs.models.shared
  (:require [clj-time.local :as l]
            [clj-time.format :as f]
            [clojure.tools.logging :as log]
            [zentaur.db.core :as db]))

(defn get-last-id [table]
  (db/clj-generic-last-id {:table table}))
