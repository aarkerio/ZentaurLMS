(ns ^:test-model zentaur.models.vclassrooms
  "Business logic for the tests section"
  (:require [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]
            [zentaur.db.core :as db]))

(defn get-vclassrooms
  "Get all users vclassrooms"
  ([user-id] (get-vclassrooms user-id false))
  ([user-id historical] (db/get-vclassrooms {:user-id user-id :historical historical})))

(defn get-vclass
  "Get a single vclassroom"
  [vclassroom-id]
  (db/get-vclass {:vclass-id vclass-id}))

(defn create-vclass
  "Create a vclassroom"
  [params]
  (db/create-vclass! params))


