(ns zentaur.libs.graphql.resolvers
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.tools.logging :as log]
            [com.walmartlabs.lacinia.resolve :refer [resolve-as]]
            [zentaur.db.core :as db]
            [zentaur.libs.graphql.validations.validations-test :as val-test]
            [zentaur.libs.helpers :as h]))

(defn create-test! [params user-id]
  (let [full-params (assoc params :user-id user-id)
        errors      (val-test/validate-test full-params)]
    (if (nil? errors)
      (db/create-minimal-test! full-params)
      {:flash errors})))

(defn- ^:private get-answers
  "Get the answers for each question"
  [question]
  (let [pre-answers       (db/get-answers {:question-id (:id question)})
        answers           (map #(update % :id str) pre-answers)]
    (assoc question :answers answers)))

(defn- ^:private attach-questions
  "Get the questions for the test"
  [test-id]
  (let [questions (db/get-questions test-id)]
    (->> questions
         (map get-answers)
         (map #(update % :id str)))))

(defn- ^:private resolver-get-questions-by-test
  "Resolver to get and convert to map keyed"
  [context args value]
  (let [pre-test-id    (:id args)
        _              (log/info :msg (str ">>> args >>>>> " args))
        test-id        { :test-id (Integer/parseInt pre-test-id) }
        pre-full-test  (db/get-one-test test-id)
        full-test      (update pre-full-test :id str) ;; Graphql needs string IDs
        questions      (attach-questions test-id)]
    (assoc {} :test full-test :questions questions)))

(defn- ^:private resolve-test-by-id
  [context args value]
  (let [pre-test-id  (:id args)
        test-id      (Integer/parseInt pre-test-id)
        user-id      (:user-id args)]
    (db/get-one-test { :id id :user-id user-id})))

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

(defn- ^:private id-to-string
  [my-map]
  (update my-map :id str))

(defn- ^:private create-question!
  [context args value]
  (let [full-args (assoc args :active true)
        test-id   (:test_id args)
        _         (log/info :msg (str ">>> full-args >>>>> " full-args))
        errors    (val-test/validate-question full-args)]
    (if (nil? errors)
      (do
        (as-> full-args v
          (db/create-question! v)
          (link-test-question! v test-id))
        (id-to-string (db/get-last-question {:test-id test-id})))
      (resolve-as nil {:message "Question not saved." :status 404 :ok false}))))

(defn resolver-map
  "Public. Match resolvers."
  []
  {:test-by-id (partial resolve-test-by-id)
   :questions-by-test (partial resolver-get-questions-by-test)
   :add-question (partial create-question!)})
