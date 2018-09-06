(ns zentaur.models.tests
  (:require [clj-time.local :as l]
            [clojure.tools.logging :as log]
            [struct.core :as st]
            [zentaur.db.core :as db]
            [zentaur.env :as env]
            [zentaur.hiccup.helpers-view :as helper]))

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
  (let [_                (log/info (str ">>> CREATED AT TYPE >>>>> " (type (:created_at question))))
        answers          (db/get-answers {:question-id (:id question)})
        question-updated (update question :created_at (fn [v] (helper/format-date v) (str (:created_at question))))]
    { :question question-updated :answers { :answers answers} }))

(defn- get-questions [test-id]
  (let [questions  (db/get-questions { :test-id test-id })]
    (map get-answers questions)))

(defn get-test-nodes [test-id user-id]
  (let [test      (db/get-one-test { :id test-id :user-id user-id })
        test-updated (update test :created_at (fn [v] (helper/format-date v) (str (:created_at test))))
        questions (get-questions test-id)]
     (log/info (str ">>> questions >>>>> " questions))
     (assoc test-updated :questions (apply pr-str questions))))

(defn destroy [params]
  (db/delete-test! params))

;;;;;;;;;;;   ADMIN FUNCTIONS  ;;;;;;;;;
(defn admin-get-tests [user-id]
  (db/admin-get-tests))

