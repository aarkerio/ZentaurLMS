(ns zentaur.routes.services
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.ring.coercion :as coercion]                ;; Coercion is a process of transforming parameters (and responses) from one format into another.
            [reitit.coercion.spec :as spec-coercion]
            [reitit.ring.middleware.muuntaja :as muuntaja]     ;; Middleware for content-negotiation, request and response formatting.
            [reitit.ring.middleware.multipart :as multipart]   ;; for upload stuff
            [reitit.ring.middleware.parameters :as parameters]
            [ring.util.http-response :refer :all]
            [zentaur.routes.services.graphql :as graphql]
            [zentaur.middleware.formats :as formats]
            [zentaur.middleware.exception :as exception]))

(defn graphql-call [req]
  (let [body       (-> req :body slurp)
        full-query (json/read-str body :key-fn keyword)]
    (ok (graphql/execute-request full-query))))

(defn service-routes []
  ["/api"
   {
    :coercion spec-coercion/coercion
    :muuntaja formats/instance
    :swagger {:id ::api}
    :middleware [;; query-params & form-params
                 parameters/parameters-middleware
                 ;; content-negotiation
                 muuntaja/format-negotiate-middleware
                 ;; encoding response body
                 muuntaja/format-response-middleware
                 ;; exception handling
                 exception/exception-middleware
                 ;; decoding request body
                 muuntaja/format-request-middleware
                 ;; coercing response bodys
                 coercion/coerce-response-middleware
                 ;; coercing request parameters
                 coercion/coerce-request-middleware
                 ;; multipart
                 multipart/multipart-middleware
                 ]}

   ["/graphql" {:post graphql-call}]

   ;; swagger documentation
   ["" {:no-doc true
        :swagger {:info {:title "Zentaur API"
                         :description "https://cljdoc.org/d/metosin/reitit"}}}

    ["/swagger.json"
     {:get (swagger/create-swagger-handler)}]

    ["/api-docs/*"
     {:get (swagger-ui/create-swagger-ui-handler
             {:url "/api/swagger.json"
              :config {:validator-url nil}})}]]

   ["/ping"
    {:get (constantly (ok {:message "pong"}))}]


   ["/files"
    {:swagger {:tags ["files"]}}

    ["/upload"
     {:post {:summary "upload a file"
             :parameters {:multipart {:file multipart/temp-file-part}}
             :responses {200 {:body {:name string?, :size int?}}}
             :handler (fn [{{{:keys [file]} :multipart} :parameters}]
                        {:status 200
                         :body {:name (:filename file)
                                :size (:size file)}})}}]]])
