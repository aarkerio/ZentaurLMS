(ns ^:test-model zentaur.models.tests
  (:require [cheshire.core :as ches]
            [clojure.tools.logging :as log]
            [java-time :as jt]
            [zentaur.db.core :as db]
            [zentaur.libs.helpers :as h]
            [zentaur.models.validations.validations-test :as val-test]))

(defn get-tests [user-id]
  (db/get-tests {:user-id user-id}))

(defn get-one-test [user-id id]
  (db/get-one-test {:user-id user-id :id id}))

;;  End with ! functions that change state for atoms, metadata, vars, transients, agents and io as well.
(defn create-test! [params user-id]
  (let [full-params (assoc params :user-id user-id)
        errors      (val-test/validate-test full-params)]
    (if (nil? errors)
      (db/create-minimal-test! full-params)
      {:flash errors})))

(defn- ^:private get-last-ordnen
  [table id]
  (case table
    "answers"   (db/get-last-ordnen-answer {:question-id id})
    "questions" (db/get-last-ordnen-questions {:test-id id})))

(defn- ^:private link-test-question!
  [last-question test-id]
  (let [question-id (get-in (first last-question) [:id])
        next-ordnen (or (:ordnen (get-last-ordnen "questions" test-id)) 0)]
    (db/create-question-test! {:question-id question-id :test-id test-id :ordnen (inc next-ordnen)})))

(defn create-question! [params]
  (let [full-params (-> params
                        (update :qtype   #(Integer/parseInt %))
                        (update :test-id #(Integer/parseInt %)))
        errors      (val-test/validate-question full-params)]
    (if (nil? errors)
      (as-> full-params v
        (db/create-question! v)
        (link-test-question! v (:test-id full-params))
        (db/get-last-question {:test-id (:test-id full-params)}))
      {:flash errors :ok false})))

(defn update-question! [params]
  (let [qtype       (if (int? (:qtype params)) (:qtype params) (Integer/parseInt (:qtype params)))
        full-params (dissoc params :active)]
    (db/update-question! (assoc full-params :qtype qtype :updated_at (jt/local-date-time)))
    (db/get-question {:id (:id params)})))

(defn create-answer! [params]
  (let [question-id  (:question-id params)
        next-ordnen  (or (:ordnen (get-last-ordnen "answers" question-id)) 0)
        full-params  (assoc params :ordnen (inc next-ordnen))
        errors       (val-test/validate-answer full-params)]
    (if (nil? errors)
      (do
        (db/create-answer! full-params)
        (db/get-last-answer {:question-id question-id}))
      {:flash errors :ok false})))

(defn- ^:private get-answers [question]
  (let [answers          (db/get-answers {:question-id (:id question)})
        keys-answers     (map #(assoc % :key (str "keyed-" (:id %))) answers)
        question-updated (update question :created_at #(h/format-time %))]
    (assoc question-updated :answers keys-answers)))

(defn update-answer! [params]
  (let [full-params (dissoc params :active)]
    (db/update-answer! (assoc full-params :updated_at (h/format-time)))
    (db/get-answer {:id (:id params)})))

(defn- ^:private get-questions
  "get and convert to map keyed"
  [test-id]
  (let [questions  (db/get-questions {:test-id test-id})
        index-seq  (map #(% :id) questions)]
        (->> questions
             (map get-answers)
             (zipmap index-seq)  ;; add the index
             )))

(defn get-test-nodes
  "JSON response for the API"
  [test-id user-id]
  (let [test         (db/get-one-test { :id test-id :user-id user-id })
        test-updated (update test :created_at #(h/format-time %))
        questions    (get-questions test-id)]
     (ches/generate-string (assoc test-updated :questions questions))))

(defn destroy [params]
  (db/delete-test! params))

(defn admin-get-tests [user-id]
  (db/admin-get-tests user-id))

(defn remove-question [params]
  (let [test-id     (:test-id params)
        question-id (:question-id params)]
    (db/remove-question! {:test-id test-id :question-id question-id})))
