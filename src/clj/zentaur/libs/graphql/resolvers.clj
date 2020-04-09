(ns zentaur.libs.graphql.resolvers
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.tools.logging :as log]
            [com.walmartlabs.lacinia.resolve :refer [resolve-as]]
            [zentaur.db.core :as db]
            [zentaur.models.tests :as mt]))

(defn- ^:private id-to-string
  [my-map]
  (update my-map :id str))

(defn create-test! [params user-id]
  (let [full-params (assoc params :user-id user-id)]
    (mt/create-test! full-params user-id)))

(defn- ^:private test-by-uurlid
  "Resolver to get and convert to map keyed"
  [context args value]
  (let [uurlid     (:uurlid args)
        archived   (:archived args)
        full-test  (mt/build-test-structure uurlid archived)]
    (update full-test :id str))) ;; Graphql needs string IDs

(defn- ^:private create-question
  [context args value]
  (let [full-args    (assoc args :active true)
        created-question (mt/create-question! full-args)]
    (update created-question :id str)))  ;; graphql wants strings on :ids

(defn- ^:private create-answer
  [context args value]
  (let [new-answer (mt/create-answer! args)]
    (update new-answer :id str)))  ;; graphql wants strings on :ids

(defn- ^:private update-test
  [context args value]
  (let [updated-test (mt/update-test! args)]
    (mt/get-one-test (:uurlid updated-test))))

(defn- ^:private update-question
  [context args value]
  (mt/update-question! args))

(defn- ^:private reorder-question
  [context args value]
  (let [pre-questions (mt/reorder-question args)
        questions (map #(update % :id str) pre-questions)]
    (assoc {} :uurlid "uurlid" :title "fake title" :questions questions)))

(defn- ^:private reorder-answer
  [context args value]
  (let [pre-answers (mt/reorder-answer args)
        id-str      (str (:question_id args))
        answers     (map #(update % :id str) pre-answers)]
    (log/info (str ">>> QUESTIONS answers >>>>> " (pr-str answers)))
    (assoc {} :id id-str :question "Foo" :answers answers)))

(defn- ^:private update-fulfill
  [context args value]
  (let [updated-question (mt/update-fulfill! args)]
    (update updated-question :id str)))

(defn- ^:private update-answer
  [context args value]
  (log/info (str ">>> update-answer data ARGS >>>>> " args))
  (let [updated-answer (mt/update-answer! args)]
    (log/info (str ">>> updated-answer AFTER update>>>>> " updated-answer))
    (update updated-answer :id str)))

(defn- ^:private delete-question
  [context args value]
  (log/info (str ">>> delete-question data ARGS >>>>> " args))
  (let [deleted-question (mt/remove-question args)]
    {:id (str (:question_id args))}))

(defn- ^:private delete-answer
  [context args value]
  (let [deleted-answer (mt/remove-answer args)]
    {:id (str (:answer_id args)) :question_id (:question_id args) }))

(defn- ^:private load-search
  [context args value]
  (mt/load-search args))

(defn- ^:private search-questions
  [context args value]
  (let [questions    (mt/search-questions args)
        ques-updated (map #(update % :id str) questions)]
    (log/info (str ">>> questions >>>>> " (prn-str ques-updated)))
     (assoc {} :uurlid "uurlid" :title "title" :questions ques-updated)))

(defn resolver-map
  "Public. Matches resolvers in schema.edn file."
  []
  {:test-by-uurlid (partial test-by-uurlid)
   :create-question (partial create-question)
   :create-answer (partial create-answer)
   :update-test (partial update-test)
   :update-question (partial update-question)
   :reorder-question (partial reorder-question)
   :reorder-answer (partial reorder-answer)
   :update-fulfill (partial update-fulfill)
   :update-answer (partial update-answer)
   :delete-question (partial delete-question)
   :delete-answer (partial delete-answer)
   :load_search (partial load-search)
   :search_questions (partial search-questions)
   })

