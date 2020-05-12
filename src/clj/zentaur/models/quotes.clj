(ns ^:test-model zentaur.models.quotes
  "Business logic for the tests section"
  (:require [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]
            [zentaur.db.core :as db]))

(defn one-quote
   "Get one random quote"
   []
   (db/get-one-quote))

(defn get-quotes
  "Get all published quotes"
  ([]      (get-quotes 1))
  ([page]  (get-quotes 1 8))
  ([page items-per-page]
   (let [offset (* (dec page) items-per-page)]
     (db/get-quotes {:limit items-per-page :offset offset}))))
