(ns zentaur.libs.models.shared
  (:require [clojure.tools.logging :as log]
            [zentaur.db.core :as db]))

(defn get-last-id
  "Get the last id for any table"
  [table]
  (db/clj-generic-last-id {:table-name table}))

(defn get-last-ordnen
  "Get last ordnen for a table with that column"
  [table id]
  (case table
    "answers"   (db/get-last-ordnen-answer {:question-id id})
    "questions" (db/get-last-ordnen-questions {:test-id id})))

