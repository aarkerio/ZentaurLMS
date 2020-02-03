(ns zentaur.middleware.lacinia
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [com.walmartlabs.lacinia :as lacinia]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia.util :refer [attach-resolvers]]
            [zentaur.libs.graphql.resolvers :as resolvers]))

(defn wrap-lacinia [handler]
  (let [path             "/graphql"
        context          nil
        compiled-schema  (-> "graphql/schema.edn"
                             io/resource
                             slurp
                             edn/read-string
                             (attach-resolvers (resolvers/resolver-map))
                             schema/compile)]
    (fn [{:keys [uri] :as request}]
      (if (= uri path)
        (let [{{:keys [query variables operationName]} :params lacinia :lacinia} request
              result (lacinia/execute compiled-schema
                                      query
                                      variables
                                      (merge (:context lacinia) context)
                                      {:operation-name operationName})]
          (log/info (str ">>> URI >>>>> " request))
          {:status 200 :headers {} :body result})
        (handler request)))))
