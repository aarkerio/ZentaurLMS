(ns zentaur.libs.graphql.resolvers
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.tools.logging :as log]
            [com.walmartlabs.lacinia.resolve :refer [resolve-as]]
            [zentaur.db.core :as db]
            [zentaur.libs.helpers :as h]
            [zentaur.models.tests :as mt]))

(defn- ^:private id-to-string
  [my-map]
  (update my-map :id str))

(defn create-test! [params user-id]
  (let [full-params (assoc params :user-id user-id)]
    (mt/create-test! full-params user-id)))

(defn- ^:private resolve-test-by-id
  "Resolver to get and convert to map keyed"
  [context args value]
  (let [test-id    (Integer/parseInt (:id args))
        archived   (:archived args)
        full-test  (mt/build-test-structure test-id archived)]
    (update full-test :id str))) ;; Graphql needs string IDs

(defn- ^:private resolve-all-tests
  [context args value]
  (let [all-tests  (mt/get-tests {:test-id (:test_id args)} )]
    all-tests))

(defn- ^:private create-question
  [context args value]
  (let [full-args    (assoc args :active true)
        new-question (mt/create-question! full-args)]
    (update new-question :id str)))  ;; graphql wants strings on :ids

(defn- ^:private create-answer
  [context args value]
  (let [full-args  (assoc args :active true)
        new-answer (mt/create-answer! full-args)]
    (update new-answer :id str)))  ;; graphql wants strings on :ids

(defn- ^:private update-test
  [context args value]
  (let [updated-test (mt/update-test! args)
        reload-test  (mt/get-one-test (:id updated-test))]
    (update reload-test :id str)))

(defn- ^:private update-question
  [context args value]
  (log/info (str ">>> update-question ARGS >>>>> " args))
  (let [updated-question (mt/update-question! args)]
    (update updated-question :id str)))

(defn- ^:private update-answer
  [context args value]
  (log/info (str ">>> update-answer ARGS >>>>> " args))
  (let [updated-answer (mt/update-answer! args)]
    (update updated-answer :id str)))

(defn- ^:private delete-question
  [context args value]
  (let [deleted-question (mt/remove-question args)]
    {:id (str (:question_id args))}))

(defn- ^:private delete-answer
  [context args value]
  (let [deleted-answer (mt/remove-answer args)]
    {:id (str (:answer_id args))}))

(defn resolver-map
  "Public. Matches resolvers in schema.edn file."
  []
  {:test-by-id (partial resolve-test-by-id)
   :get-all-tests (partial resolve-all-tests)
   :create-question (partial create-question)
   :create-answer (partial create-answer)
   :update-test (partial update-test)
   :update-question (partial update-question)
   :update-answer (partial update-answer)
   :delete-question (partial delete-question)
   :delete-answer (partial delete-answer)
   })

