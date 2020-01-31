(ns zentaur.handler
  (:require
   [mount.core :as mount]
   [reitit.ring :as ring]
   [ring.middleware.content-type :refer [wrap-content-type]]
   [ring.middleware.webjars :refer [wrap-webjars]]   ;; WebJars are client-side web libraries packaged into JAR (Java Archive) files.
   [reitit.swagger-ui :as swagger-ui]
   [zentaur.env :refer [defaults]]
   [zentaur.controllers.company-controller :as ccon]
   [zentaur.middleware :as middleware]
   [zentaur.routes.home :refer [home-routes]]
   [zentaur.routes.services :refer [service-routes]]))

(mount/defstate init-app
  :start ((or (:init defaults) (fn [])))
  :stop  ((or (:stop defaults) (fn []))))

(mount/defstate app-routes
  :start
  (ring/ring-handler
    (ring/router
      [(home-routes)
       (service-routes)])
    (ring/routes
      (swagger-ui/create-swagger-ui-handler
        {:path   "/swagger-ui"
         :url    "/api/swagger.json"
         :config {:validator-url nil}})
      (ring/create-resource-handler {:path "/"}) ;; Serve static resources avoiding conflicting paths
      (wrap-content-type                         ;; Ring middleware that adds a content-type header to the response. Defaults to 'application/octet-stream'.
       (ring/redirect-trailing-slash-handler {:method :strip})
      (ring/create-default-handler
        {:not-found
         (constantly (ccon/display-error {:status 404 :title "404 - Page not found"}))
         :method-not-allowed
         (constantly (ccon/display-error {:status 405 :title "405 - Not allowed"}))
         :not-acceptable
         (constantly (ccon/display-error{:status 406 :title "406 - Not acceptable"}))})))))

(defn app []
  (middleware/wrap-base #'app-routes))
