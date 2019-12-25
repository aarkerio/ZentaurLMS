(ns ^:test-model zentaur.models.tests
  "Business logic for the tests section"
  (:require [clojure.tools.logging :as log]
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
  [question-id test-id]
  (let [next-ordnen (or (:ordnen (get-last-ordnen "questions" test-id)) 0)]
    (db/create-question-test! {:question-id question-id :test-id test-id :ordnen (inc next-ordnen)})))

(defn- ^:private get-last-question
  [params]
  (let [test-id        (:test-id params)
        question-row   (db/create-question! params)
        question-id    (:id (first question-row))
        _              (link-test-question! question-id test-id)
        last-question  (db/get-last-question {:question-id question-id :test-id test-id})
        full-question  (assoc last-question :answers {})
        all-question   (h/update-dates full-question)
        qid            (:id full-question)]
    (assoc {} qid all-question)))

(defn create-question! [params]
  (let [full-params (-> params
                        (update :qtype   #(Integer/parseInt %))
                        (update :test-id #(Integer/parseInt %)))
        errors      (val-test/validate-question full-params)]
    (if (nil? errors)
      (get-last-question full-params)
      {:flash errors :ok false})))

(defn update-question! [params]
  (let [qtype       (if (int? (:qtype params)) (:qtype params) (Integer/parseInt (:qtype params)))
        full-params (dissoc params :active)
        _           (db/update-question! (assoc full-params :qtype qtype :updated_at (h/format-time)))
        question    (db/get-question {:id (:id params)})]
    (h/update-dates question)))

(defn- ^:private create-new-answer
  [params question-id]
  (let [new-answer     (db/create-answer! params)
        last-answer    (db/get-last-answer {:question-id question-id})
        updated-answer (h/update-dates last-answer)]
    (assoc {} (:id updated-answer) updated-answer)))

(defn create-answer! [params]
  (let [question-id  (:question-id params)
        next-ordnen  (or (:ordnen (get-last-ordnen "answers" question-id)) 0)
        full-params  (assoc params :ordnen (inc next-ordnen))
        errors       (val-test/validate-answer full-params)]
    (if (nil? errors)
      (create-new-answer full-params question-id)
      {:flash errors :ok false})))

(defn- ^:private get-answers [{:keys [id] :as question}]
  (let [answers          (db/get-answers {:question-id id})
        index-seq        (map #(keyword (str (% :id))) answers)
        question-updated (h/update-dates question)
        mapped-answers   (zipmap index-seq answers)]
    (assoc question-updated :answers mapped-answers)))

(defn- ^:private get-questions
  "Get and convert to map keyed"
  [test-id]
  (let [questions  (db/get-questions { :test-id test-id })
        index-seq  (map #(keyword (str (% :id))) questions)]
    (->> questions
         (map get-answers)
         (zipmap index-seq))))

(defn get-test-nodes
  "JSON response for the API"
  [test-id user-id]
  (let [test          (db/get-one-test { :id test-id :user-id user-id })
        test-updated  (h/update-dates test)
        questions     (get-questions test-id)]
    (assoc test-updated :questions questions)))

(defn update-answer!
  "Update after editing with ClojureScript"
  [params]
  (let [full-params (dissoc params :active)]
    (db/update-answer! (assoc full-params :updated_at (h/format-time))
    (db/get-answer {:id (:id params)}))))

(defn export-pdf [test-id]
  (let [test-id (inc test-id)]
    (db/remove-test! {:test-id test-id})))

(defn remove-test [params]
  (let [test-id (:test-id params)]
    (db/remove-test! {:test-id test-id})))

(defn remove-question [params]
  (let [test-id     (:test-id params)
        question-id (:question-id params)]
    (db/remove-question! {:test-id test-id :question-id question-id})))

(defn remove-answer [params]
  (let [result   (db/remove-answer! params)]
    (assoc params :ok (:bool result))))
