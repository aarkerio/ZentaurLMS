(ns ^:test-model zentaur.models.vclassrooms
  "Business logic for the tests section"
  (:require [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]
            [zentaur.db.core :as db]
            [zentaur.libs.models.shared :as sh]))

(defn get-vclassrooms
  "Get all users vclassrooms"
  ([user-id] (get-vclassrooms user-id false))
  ([user-id historical] (db/get-vclassrooms {:user-id user-id :historical historical})))

(defn get-vclass
  "Get a single vclassroom"
  [vclass-id]
  (db/get-vclass {:id vclass-id}))

(defn create-vclass
  "Create a vclassroom"
  [params user-id]
  (let [uurlid      (sh/gen-uuid)
        pre-params  (dissoc params :__anti-forgery-token)
        full-params (assoc pre-params :user-id user-id :uurlid uurlid)]
    (log/info (str ">>> PARAMs full-params *** >>>>> " full-params))
    (db/create-vclass! full-params)))


