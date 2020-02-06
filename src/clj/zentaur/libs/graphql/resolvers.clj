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
  (log/info (str ">>> ARGS >>>>> " args))
  (let [full-args    (assoc args :active true)
        new-question (mt/create-question! full-args)]
    (log/info (str ">>> new-question >>>>> " new-question))
    new-question))

(defn resolver-map
  "Public. Match resolvers."
  []
  {:test-by-id (partial resolve-test-by-id)
   :get-all-tests (partial resolve-all-tests)
   :create-question (partial create-question)})

