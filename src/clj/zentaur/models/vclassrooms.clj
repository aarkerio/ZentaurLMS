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
  [uurlid user-id]
  (db/get-vclass {:user-id user-id :uurlid uurlid}))

(defn create-vclass
  "Create a vclassroom"
  [params user-id]
  (let [uurlid      (sh/gen-uuid)
        pre-params  (dissoc params :__anti-forgery-token)
        _    (log/info (str ">>> PARAMS BEFORE gggg ******* >>>>> " pre-params))
        bool-params (sh/checkboxboolean pre-params :draft :public :historical)
        full-params (assoc bool-params :user-id user-id :uurlid uurlid)]
    (log/info (str ">>> PARAM AFTER bool-paramsbool-paramsbool-params **** >>>>> " full-params))
    (db/create-vclass! full-params)))

(defn update-vclass [params user-id]
  (log/info (str ">>> PARAM update-vclass **** >>>>> " params))
  (let [full-params (dissoc params :active)
        bool-params (sh/checkboxboolean full-params :draft :public :historical)]
    (log/info (str ">>> PARAM bool-paramsbool-paramsbool-params **** >>>>> " bool-params))
    (db/update-vclass (assoc bool-params :user-id user-id))))

(defn toggle [{:keys [uurlid draft]}]
  (let [new-draft (if (= draft "true") false true)]
    (db/toggle-vclassroom {:uurlid uurlid :draft new-draft})))

(defn destroy [uuid]
  (let [int-uuid (Integer/parseInt uuid)]
    (db/delete-vclassroom {:uuid int-uuid})))

