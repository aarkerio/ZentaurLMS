(ns ^:test-model zentaur.models.quotes
  "Business logic for the tests section"
  (:require [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]
            [zentaur.db.core :as db]))

(defn one-quote
   "Get one random quote"
   []
   (db/get-one-quote))

