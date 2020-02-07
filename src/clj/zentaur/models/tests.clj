(ns ^:test-model zentaur.models.tests
  "Business logic for the tests section"
  (:require [clojure.spec.alpha :as s]
            [clojure.tools.logging :as log]
            [zentaur.db.core :as db]
            [zentaur.libs.helpers :as h]
            [zentaur.hiccup.helpers-view :as hv]
            [zentaur.libs.models.shared :as sh]
            [zentaur.models.validations.validations-test :as val-test]))

(defn get-tests [user-id]
  (db/get-tests {:user-id user-id}))

(defn get-one-test
  ([id] (get-one-test id false))
  ([id archived] (db/get-one-test {:id id :archived archived})))

(defn get-subjects []
  (db/get-subjects))

;;  End with ! functions that change state for atoms, metadata, vars, transients, agents and io as well.
(defn create-test! [params user-id]
  (let [pre-params  (assoc params :user-id user-id)
        full-params (update pre-params :subject-id #(Integer/parseInt %))
        _           (log/info (str ">>> full-paramsCREATE TEST >>>>> " full-params))
        errors      (val-test/validate-test full-params)]
    (if (nil? errors)
      (db/create-minimal-test! full-params)
      false)))

(defn- ^:private link-test-question!
  [question-id test-id]
  (let [next-ordnen (or (:ordnen (sh/get-last-ordnen "questions" test-id)) 0)]
    (db/create-question-test! {:question_id question-id :test_id test-id :ordnen (inc next-ordnen)})))

(defn- ^:private get-last-question
  [params]
  (let [test-id          (:test_id params)
        created-question (db/create-question! (dissoc params :test_id))
        question-id      (:id created-question)
        _                (link-test-question! question-id test-id)]
    (assoc created-question :answers [])))

(defn create-question! [params]
  (let [errors (val-test/validate-question params)]
    (if (nil? errors)
      (get-last-question params)
      {:flash errors :ok false})))

(defn- ^:private create-new-answer
  [params]
  (let [last-answer  (db/create-answer! params)]
    (assoc {} (:id last-answer) last-answer)))

(defn create-answer! [params]
  (let [question-id  (:question-id params)
        next-ordnen  (or (:ordnen (sh/get-last-ordnen "answers" question-id)) 0)
        full-params  (assoc params :ordnen (inc next-ordnen))
        errors       (val-test/validate-answer full-params)]
    (if (nil? errors)
      (create-new-answer full-params)
      {:flash errors :ok false})))

;;;;; TEST BUILD SECTION STARTS

(defn- ^:private get-answers [{:keys [id] :as question}]
  (let [answers          (db/get-answers {:question-id id})
        index-seq        (map #(keyword (str (% :id))) answers)
        mapped-answers   (zipmap index-seq answers)]
    (assoc question :answers mapped-answers)))

(defn- ^:private get-questions
  "Get and convert to map keyed"
  [test-id]
  (let [questions        (db/get-questions { :test-id test-id })
        questions-index  (map-indexed
                            (fn [idx question]
                              (assoc question :index (inc idx))) questions)
        index-seq        (map #(keyword (str (% :id))) questions-index)]
    (->> questions-index
         (map get-answers)
         (zipmap index-seq))))

(defn build-test-structure
  "Build the map with the test, the questions and the answers.
   Function used by the Web and the Phone App."
  [test-id archived]
  (let [test          (db/get-one-test {:id test-id :archived archived})
        questions     (get-questions test-id)
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
        full-params  (dissoc params :active)]
    (db/update-question! (assoc full-params :qtype qtype))))

(defn update-answer!
  "Update answer after editing with Re-frame"
  [params]
  (let [full-params (dissoc params :active)]
    (db/update-answer! full-params)))

(defn update-test!
  "Update test after editing with Re-frame"
  [params]
  (let [full-params (dissoc params :active)]
    (log/info (str ">>> **** update-test! >>>>> full-params: " full-params))
    (db/update-test! full-params)))

;;;;;;;;;;;;    DELETES ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
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

