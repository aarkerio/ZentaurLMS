(ns zentaur.models.tests
  (:require [zentaur.db.core :as db]
            [zentaur.env :as env]
            [struct.core :as st]
            [clojure.tools.logging :as log]
            [clj-time.local :as l]))

;;;;;;;;;;;;;;;;;;;;;;
;;    VALIDATIONS    NIL == all was fine!!
;;;;;;;;;;;;;;;;;;;;;

(def test-schema
  [[:user-id st/required st/integer]
   [:title
    st/required
    st/string
    {:title "Title field must contain at least 2 characters"
     :validate #(> (count %) 2)}]])

(defn validate-test [params]
  (first
    (st/validate params test-schema)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;          ACTIONS
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-tests [user-id]
  (db/get-tests { :user-id user-id }))

(defn get-one-test [user-id id]
  (db/get-one-test {:user-id user-id :id id}))

;;  End with ! functions that change state for atoms, metadata, vars, transients, agents and io as well.
(defn create-test! [params user-id]
  (let [full-params (assoc params :user-id user-id)
        _ (log/info (str ">>> full-params >>>>> " full-params))
        errors      (-> full-params (validate-test))]
      (if (= errors nil)
        (db/create-minimal-test! full-params)
        {:flash errors})))

(defn- get-answers [question]
  (let [answers (db/get-answers { :question-id (:id question) })]
    { :question question :answers { :answers answers} }))

(defn- get-questions [test-id]
  (let [questions     (db/get-questions { :test-id test-id })
        root-question {}]
    (map get-answers questions)))

(defn get-test-nodes [test-id user-id]
  (let [test      (db/get-one-test { :id test-id :user-id user-id })
        questions (get-questions test-id)]
     (log/info (str ">>> questions >>>>> " questions))
     (assoc test :questions questions)))

(defn destroy [params]
  (db/delete-test! params))

;;;;;;;;;;;   ADMIN FUNCTIONS  ;;;;;;;;;
(defn admin-get-tests [user-id]
  (db/admin-get-tests))

