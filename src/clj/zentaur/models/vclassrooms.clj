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
  [uurlid]
  (db/get-vclass {:uurlid uurlid}))

(defn create-vclass
  "Create a vclassroom"
  [params user-id]
  (let [uurlid      (sh/gen-uuid)
        pre-params  (dissoc params :__anti-forgery-token)
        draft       (contains? params :draft)
        public      (contains? params :public)
        historical  (contains? params :historical)
        full-params (assoc pre-params :user-id user-id :uurlid uurlid :draft draft :public public :historical historical)]
    (db/create-vclass! full-params)))

(defn toggle [{:keys [uurlid draft]}]
  (let [new-draft (if (= draft "true") false true)]
    (db/toggle-vclassroom {:uurlid uurlid :draft new-draft})))

(defn destroy [uuid]
  (let [int-uuid (Integer/parseInt uuid)]
    (db/delete-vclassroom {:uuid int-uuid})))

