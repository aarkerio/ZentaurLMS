(ns ^:test-model zentaur.models.tests
  "Business logic for the tests section"
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
  [question-id test-id]
  (let [next-ordnen (or (:ordnen (get-last-ordnen "questions" test-id)) 0)]
    (db/create-question-test! {:question-id question-id :test-id test-id :ordnen (inc next-ordnen)})))

(defn- ^:private get-last-question
  [params]
  (log/info (str ">>> PARAMS LASTT  >>>>> " params))
  (let [test-id        (:test-id params)
        question-row   (db/create-question! params)
        question-id    (:id (first question-row))
        _              (link-test-question! question-id test-id)
        last-question  (db/get-last-question {:question-id question-id :test-id test-id})
        full-question  (assoc last-question :answers {})
        _              (log/info (str ">>> PARfull-questionfull-questionfull-question >>>>> " full-question "      CLASS >>>>" (class (:created_at full-question))))
        all-question (update full-question :created_at #(h/format-time %))
        qid            (:id full-question)]
    (assoc {} :qid qid :full-question all-question)))

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

(defn- ^:private get-answers [{:keys [id] :as question}]
  (let [answers          (db/get-answers {:question-id id})
        keys-answers     (map #(assoc % :key (str "keyed-" (:id %))) answers) ;; add a unique key so React doesn't complain.
        index-seq        (map #(keyword (% :id)) keys-answers)
        question-updated (update question :created_at #(h/format-time %))
        mapped-answers   (zipmap index-seq keys-answers)
        final-question   (assoc question-updated :answers mapped-answers)]
    (assoc {} :qid id :full-question final-question)))

(defn- ^:private get-questions
  "Get and convert to map keyed"
  [test-id]
  (let [questions  (db/get-questions { :test-id test-id })
        index-seq  (map #(keyword (% :id)) questions)]
    (->> questions
         (map get-answers)
         (zipmap index-seq)  ;; add the index
         )))

(defn get-test-nodes
  "JSON response for the API"
  [test-id user-id]
  (let [test          (db/get-one-test { :id test-id :user-id user-id })
        test-updated  (update test :created_at #(h/format-time %))
        questions     (get-questions test-id)]
    (ches/encode (assoc test-updated :questions questions))))

(defn update-answer!
  "Update after editing with ClojureScript"
  [params]
  (let [full-params (dissoc params :active)]
    (db/update-answer! (assoc full-params :updated_at (h/format-time)))
    (db/get-answer {:id (:id params)})))

(defn destroy [params]
  (db/delete-test! params))

(defn admin-get-tests [user-id]
  (db/admin-get-tests user-id))

(defn remove-question [params]
  (let [test-id     (:test-id params)
        question-id (:question-id params)]
    (db/remove-question! {:test-id test-id :question-id question-id})))
