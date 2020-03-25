(ns ^:test-model zentaur.models.tests
  "Business logic for the tests section"
  (:require [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]
            [zentaur.db.core :as db]
            [zentaur.hiccup.helpers-view :as hv]
            [zentaur.libs.models.shared :as sh]
            [zentaur.models.validations.validations-test :as val-test]))

(defn get-one-test
  "Used by GraphQL resolver"
  ([uurlid] (get-one-test uurlid false))
  ([uurlid archived] (db/get-one-test {:uurlid uurlid :archived archived})))

(defn get-tests
  "Get the list of test by user"
  [user-id]
  (db/get-tests {:user-id user-id}))

(defn get-subjects
  "Data for populates the test form"
  []
  (db/get-subjects))

(defn get-levels
  "Data for populates the test form"
  []
  (db/get-levels))

;;  End with ! functions that change state for atoms, metadata, vars, transients, agents and io as well.
(defn create-test! [params user-id]
  (let [uurlid      (sh/gen-uuid)
        pre-params  (sh/str-to-int params :subject_id :level_id)
        full-params (assoc pre-params :user_id user-id :uurlid uurlid)
        errors      (val-test/validate-test full-params)]
    (if (nil? errors)
      (db/create-minimal-test full-params)
      {:error errors :ok false})))

(defn- ^:private link-test-question!
  [question-id test-id]
  (let [next-ordnen (or (:ordnen (sh/get-last-ordnen "questions" test-id)) 0)]
    (db/create-question-test! {:question_id question-id :test_id test-id :ordnen (inc next-ordnen)})))

(defn- ^:private get-last-question
  [params]
  (let [test             (get-one-test (:uurlid params))
        test-id          (:id test)
        created-question (db/create-question! params)
        question-id      (:id created-question)
        _                (link-test-question! question-id test-id)]
    created-question))

(defn create-question! [params]
  (let [errors (val-test/validate-question params)]
    (if (nil? errors)
      (get-last-question params)
      {:flash errors :ok false})))

(defn create-answer! [params]
  (let [question-id  (:question_id params)
        last-ordnen  (sh/get-last-ordnen "answers" question-id)
        next-ordnen  (or (:ordnen last-ordnen) 0)
        full-params  (assoc params :ordnen (inc next-ordnen))
        errors       (val-test/validate-answer full-params)]
    (if (nil? errors)
      (db/create-answer! full-params)
      {:flash errors :ok false})))

(defn link-questions [test-id params]
  (let [questions (db/random-questions params)]
    (doseq [q questions]
      (link-test-question! (:id question) test-id))))

(defn generate-test [params]
  (let [test     (create-test! params 2)
        _        link-questions (:id test) params]
    {:error nil :ok false}))

;;;;; TEST BUILD SECTION STARTS

(defn- ^:private get-answers [{:keys [id] :as question}]
  (let [answers          (db/get-answers {:question-id id})
        answers-graphql  (map #(update % :id str) answers)
        question-graphql (update question :id str)]
    (assoc question-graphql :answers answers-graphql)))

(defn- ^:private get-questions
  "Get questions and convert to map keyed"
  [test-id]
  (let [questions (db/get-questions {:test-id test-id})]
     (map get-answers questions)))

(defn build-test-structure
  "Build the map with the test, the questions and the answers.
   Function used by the Web and the Phone App."
  [uurlid archived]
  (let [test          (db/get-one-test {:uurlid uurlid :archived archived})
        questions     (get-questions (:id test))
        subjects      (db/get-subjects)
        subj-strs     (map #(update % :id str) subjects)]
    (try
      (assoc test :questions questions :subjects subj-strs)
      (catch Exception e (str "******** >>> Caught exception: " (.getMessage e)))
      (finally (assoc {} :error "function get-test-nodes in model error")))))

;;;;; TEST BUILD SECTION ENDS

;;;;;;;;;;;;      UPDATES ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn update-question! [params]
  (let [qtype        (if (int? (:qtype params)) (:qtype params) (Integer/parseInt (:qtype params)))
        full-params  (dissoc params :active)
        qid          (db/update-question! (assoc full-params :qtype qtype))]
    (db/get-one-question qid)))

(defn update-fulfill! [params]
  (db/update-question-fulfill! params))

(defn update-answer!
  "Update answer after editing with Re-frame"
  [params]
  (let [full-params (dissoc params :active)]
    (db/update-answer! full-params)))

(defn update-test!
  "Update test after editing it with Re-frame"
  [params]
    (db/update-test! params))

;;;;;;;;;;;;    DELETES ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn remove-test [params]
  (let [uurlid (:uurlid params)]
    (db/toggle-test {:uurlid uurlid})))

(defn remove-question
  "Not a real delete, just unlink the question from test"
  [params]
  (let [test        (get-one-test (:uurlid params))
        test-id     (:id test)
        full-params (assoc params :test_id test-id)]
    (db/unlink-question! full-params)))

(defn remove-answer [params]
  (let [result   (db/remove-answer! params)]
    (assoc params :ok (:bool result))))

;;;; REORDERS

(defn reorder-question-rows
   "Reorder questions"
  [rows direction]
  (let [first       (first rows)
        second      (second rows)
        new-one     (assoc {} :id (:id first)  :ordnen (:ordnen second))
        new-two     (assoc {} :id (:id second) :ordnen (:ordnen first))]
    (db/update-question-order new-one)
    (db/update-question-order new-two)))

(defn reorder-question [{:keys [uurlid ordnen direction]}]
  (let [test         (get-one-test uurlid)
        test-id      (:id test)
        data         (assoc {} :test_id test-id :ordnen ordnen)
        qt-rows      (if (= "up" direction) (db/question-order-up data) (db/question-order-down data))]
     (if (= 2 (count qt-rows))
       (do (reorder-question-rows qt-rows direction)
           (get-questions test-id))
       {:error "Not enough rows"})))

(defn reorder-answer-rows
   "Reorder answer"
  [rows]
  (let [first       (first rows)
        second      (second rows)
        new-one     (assoc {} :id (:id first)  :ordnen (:ordnen second))
        new-two     (assoc {} :id (:id second) :ordnen (:ordnen first))]
    (db/update-answer-order new-one)
    (db/update-answer-order new-two)))

(defn reorder-answer
  [{:keys [ordnen question_id  direction]}]
  (let [data         (assoc {} :ordnen ordnen :question-id question_id)
        answer-rows  (if (= "up" direction) (db/answer-order-up data) (db/answer-order-down data))]
     (if (= 2 (count answer-rows))
       (do (reorder-answer-rows answer-rows)
          (db/get-answers {:question-id question_id}))
       {:error "Not enough answer rows"})))
