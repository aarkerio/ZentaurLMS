(ns zentaur.routes.services.graphql
  (:require [clojure.data.json :as json]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.walmartlabs.lacinia.util :refer [attach-resolvers]]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia :as lacinia]
            [mount.core :refer [defstate]]
            [ring.util.http-response :refer :all]
            [zentaur.libs.graphql.resolvers :as resolvers]))

(defstate compiled-schema
  :start
  (-> "graphql/schema.edn"
      io/resource
      slurp
      edn/read-string
      (attach-resolvers (resolvers/resolver-map))
      schema/compile))

(defn execute-request [query]
    (let [vars    nil
          context nil]
    (json/write-str (lacinia/execute compiled-schema query vars context))))
