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

(defn get-langs
  "Data for populates the test form"
  []
  (db/get-langs))

(defn gral-data
  "Data for populates the test form"
  []
  (let [subjects (db/get-subjects)
        levels   (db/get-levels)
        langs    (db/get-langs)]
    (assoc {} :subjects subjects :levels levels :langs langs)))

(defn create-test! [params user-id]
  (let [uurlid      (sh/gen-uuid)
        pre-params  (if (int? (:subject_id params)) params (sh/str-to-int params :subject_id :level_id :lang_id))
        full-params (assoc pre-params :user_id user-id :uurlid uurlid)
        errors      (val-test/validate-test full-params)]
    (if (nil? errors)
      (db/create-minimal-test full-params)
      (log/info (str ">> CREATE tests errors >> " errors)))))

(defn- ^:private link-test-question!
  [question-id test-id]
  (let [next-ordnen (inc (or (:ordnen (sh/get-last-ordnen "questions" test-id)) 0))
        _           (db/create-question-test! {:question_id question-id :test_id test-id :ordnen next-ordnen})]
    next-ordnen))

(defn- ^:private insert-and-link-question [params test-id]
  (let [created-question (db/create-question! params)
        question-id      (:id created-question)
        ordnen           (link-test-question! question-id test-id)]
    (assoc created-question :ordnen ordnen)))

(defn create-question!
  [params]
  (let [test         (get-one-test (:uurlid params))
        test-id      (:id test)
        full-params  (assoc params :subject_id (:subject_id test) :level_id (:level_id test) :level_id (:level_id test) :origin 0)
        errors       (val-test/validate-question full-params)]
    (if (nil? errors)
      (insert-and-link-question full-params test-id)
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

;;; NEW TEST & CLONING QUESTIONS
(defn clone-answers [old-question-id new-question-id]
  (let [answers (db/get-answers {:question_id old-question-id})]
    (doseq [a answers]
      (db/create-answer! (assoc a :question_id new-question-id)))))   ;; :question_id, :answer, :correct, :ordnen

(defn clone-question [question uurlid]
  (let [test          (get-one-test uurlid)
        previous-id   (:id question)
        params        (assoc {} :user-id (:user_id test) :id previous-id)
        qnew-id       (db/clone-question params)
        _             (link-test-question! (:id qnew-id) (:id test))]
    (clone-answers previous-id (:id qnew-id))))

(defn generate-questions [params uurlid]
  (let [questions (db/random-questions params)]
    (doseq [q questions]
      (clone-question q uurlid))))

(defn generate-test [params user-id]
  (let [pre-params  (sh/str-to-int params :subject_id :level_id :lang_id :limit)
        full-params (assoc pre-params :title "Your new test" :tags "list of tags")
        _ (log/info (str ">>> FULLLL PARAM >>>>> " full-params))
        test        (create-test! full-params user-id)
        _           (generate-questions pre-params (:uurlid test))]
    (:uurlid test)))

(defn link-test-questions [uurlid user-id]
  (let [questions (db/get-all-questions-by-user-id {:user-id user-id})]
    (doseq [q questions]
      (clone-question q uurlid))))

(defn build-test
  "Build a test after search and select questions"
  [user-id]
  (let [user-map    {:user-id user-id}
        question    (db/get-last-question-by-user-id user-map)
        full-params (assoc question :title "Your new test" :tags "list of tags")
        new-test    (create-test! full-params user-id)
        uurlid      (:uurlid new-test)
        _           (link-test-questions uurlid user-id)]
    (db/remove-all-user-keep-questions user-map)
    uurlid))

;;;;; TEST BUILD SECTION STARTS

(defn- ^:private get-answers [{:keys [id] :as question}]
  (let [answers          (db/get-answers {:question_id id})
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
        levels        (db/get-levels)]
    (try
      (assoc test :questions questions :subjects subjects :levels levels)
      (catch Exception e (str "******** >>> Caught exception: " (.getMessage e)))
      (finally (assoc {} :error "function get-test-nodes in model error")))))

;;;;; TEST BUILD SECTION ENDS

;;;;;;;;;;;;      UPDATES ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn update-question! [params]
  (let [qid       (db/update-question! params)
        qupdated  (db/get-one-question {:id (:id qid)})]
    (get-answers qupdated)))

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
  (let [result (db/remove-answer! params)]
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
          (db/get-answers {:question_id question_id}))
       {:error "Not enough answer rows"})))

;; SEARCH QUESTIONS
(defn load-search [args]
  (let [subjects (get-subjects)
        levels   (get-levels)
        langs    (get-langs)]
    (assoc {} :uurlid "nope" :title "Foo" :subjects subjects :levels levels :langs langs)))

(defn search-questions
  "Used to build a random test"
  [args]
  (let [pre-params  (sh/str-to-int args :subject_id :level_id :lang_id)
        full-params (assoc pre-params :limit 20)]
    (db/search-questions full-params)))

(defn str-to-v [string]
  (let [first-v (clojure.string/split string  #" ")]
    (mapv #(Integer/parseInt % ) first-v)))

(defn full-search [{:keys [subjects levels langs terms offset limit] :or {offset 0 limit 10}}]
  (let [isubjects (str-to-v subjects)
        ilevels   (str-to-v levels)
        ilangs    (str-to-v langs)]
    (db/full-search-questions { :subjects isubjects :levels ilevels :langs ilangs  :terms terms :offset offset :limit limit})))

(defn hold-question
  "Save a selected question"
  [{:keys [question_id user_uuid]}]
  (let [user    (db/get-user {:id 0 :email "" :uuid user_uuid})
        user_id (:id user)]
    (db/create-keep-question {:question_id question_id :user_id user_id})))

(defn remove-hold-question
  "Remove a selected question"
  [{:keys [question_id user_uuid]}]
  (let [user    (db/get-user {:id 0 :email "" :uuid user_uuid})
        user_id (:id user)]
    (db/remove-keep-question {:question_id question_id :user_id user_id})))
