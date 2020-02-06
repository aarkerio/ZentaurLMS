(ns zentaur.routes.services.graphql
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [com.walmartlabs.lacinia.util :refer [attach-resolvers]]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia :as lacinia]
            [mount.core :refer [defstate]]
            [zentaur.libs.graphql.resolvers :as resolvers]))

(defstate compiled-schema
  :start
  (-> "graphql/schema.edn"
      io/resource
      slurp
      edn/read-string
      (attach-resolvers (resolvers/resolver-map))
      schema/compile))

(defn execute-request [{:keys [variables query context]}]
  (lacinia/execute compiled-schema query variables context))
