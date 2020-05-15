(ns ^:test-model zentaur.models.quotes
  "Business logic for the tests section"
  (:require [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]
            [zentaur.db.core :as db]
            [zentaur.models.validations.validations-quote :as vq]))

(defn one-quote
   "Get one random quote"
   []
   (db/get-one-random-quote))

(defn get-quotes
  "Get all published quotes"
  ([]      (get-quotes 1))
  ([page]  (get-quotes 1 8))
  ([page items-per-page]
   (let [offset (* (dec page) items-per-page)]
     (db/get-quotes {:limit items-per-page :offset offset}))))


(defn create-quote
  [params]
  (let [errors (vq/validate-quote params)]
    (if (nil? errors)
      (db/create-quote params)
      (log/info (str ">>> ERRORS >>>>> " errors)))))

(defn update-quote
  [params]
  (let [errors (vq/validate-quote params)]
    (if (nil? errors)
      (db/update-quote params)
      (log/info (str ">>> ERRORS >>>>> " errors)))))

(defn delete-quote [id]
  (let [int-id (Integer/parseInt id)]
    (db/delete-quote {:id int-id})))
