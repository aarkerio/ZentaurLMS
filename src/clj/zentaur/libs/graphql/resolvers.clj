(ns zentaur.libs.graphql.resolvers
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.tools.logging :as log]
            [com.walmartlabs.lacinia.resolve :refer [resolve-as]]
            [zentaur.db.core :as db]
            [zentaur.models.posts :as mp]
            [zentaur.models.quotes :as mq]
            [zentaur.models.tests :as mt]))

(defn- ^:private id-to-string
  [my-map]
  (update my-map :id str))

(defn create-test! [params user-id]
  (let [full-params  (assoc params :user-id user-id)]
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
  (let [updated-answer (mt/update-answer! args)]
    (update updated-answer :id str)))

(defn- ^:private delete-question
  [context args value]
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
    (assoc {} :uurlid "uurlid" :title "title" :questions ques-updated)))

(defn- ^:private load-comments
  [context args value]
  (let [comments    (mp/get-comments args)
        comments-1  (map #(assoc % :username (str (:fname %) "_" (:lname %))) comments)
        comments-2  (map #(dissoc % :fname :lname) comments-1)]
    (assoc {} :comments comments-2)))

(defn- ^:private create-comment
  [context args value]
  (let [new-comment (mp/create-comment args)
        new-comment-2 (assoc new-comment :username (str (:fname new-comment) "_" (:lname new-comment)))]
   (dissoc new-comment-2 :fname :lname)))

(defn- ^:private search-fullq
  [context args value]
  (log/info (str ">>> ARGSSS search-fullq >>>>> " args))
  (let [questions (mt/full-search args)]
    (assoc {} :questions questions)))

(defn- ^:private load-quotes
  [context args value]
  (let [quotes  (mq/get-quotes args)]
    (assoc {} :quotes quotes)))

(defn- ^:private create-quote
  [context args value]
  (let [new-quote (mq/create-quote args)]
    new-quote))

(defn- ^:private update-quote
  [context args value]
  (let [updated-quote (mq/update-quote args)]
    updated-quote))

(defn- ^:private delete-quote
  [context args value]
  (mq/delete-quote args))

(defn- ^:private hold-question
  [context args value]
  (let [returned    (mt/hold-question args)
        question_id (:question_id returned)]
    {:id question_id :question "foo"}))

(defn- ^:private remove-hold-question
  [context args value]
  (let [returned    (mt/remove-hold-question args)
        question_id (:question_id returned)]
    {:id question_id :question "foo"}))

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
   :load-comments (partial load-comments)
   :create-comment (partial create-comment)
   :search-fullq (partial search-fullq)
   :load-quotes (partial load-quotes)
   :create-quote (partial create-quote)
   :update-quote (partial update-quote)
   :delete-quote (partial delete-quote)
   :hold-question (partial hold-question)
   :remove-hold-question (partial remove-hold-question)
   })

