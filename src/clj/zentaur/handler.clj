(ns zentaur.handler
  (:require
   [mount.core :as mount]
   [reitit.ring :as ring]
   [ring.middleware.content-type :refer [wrap-content-type]]
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
      (ring/redirect-trailing-slash-handler {:method :strip})
      (ring/create-default-handler
       {:not-found
        (constantly {:status 404 :title "404 - Page not found"})
        :method-not-allowed
        (constantly {:status 405 :title "405 - Not allowed"})
        :not-acceptable
        (constantly  {:status 406 :title "406 - Not acceptable"})}))))

(defn app []
  (middleware/wrap-base #'app-routes))
