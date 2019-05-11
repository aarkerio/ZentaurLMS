(ns zentaur.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [compojure.route :as route]
            [mount.core :as mount]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.ring :as ring]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.webjars :refer [wrap-webjars]]
            [ring.util.http-response :as response]
            [zentaur.env :refer [defaults]]
            [zentaur.middleware :as middleware]
            [zentaur.layout :refer [error-page]]
            [zentaur.routes.home :refer [home-routes]]
            [zentaur.routes.services :refer [service-routes]]))

(mount/defstate init-app
  :start ((or (:init defaults) identity))
  :stop  ((or (:stop defaults) identity)))

(mount/defstate app
  :start
  (middleware/wrap-base
   (routes
    (-> #'home-routes
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
    (route/not-found
     (:body
      (error-page {:status 404
                   :title  "page not found"}))))))

(mount/defstate app
  :start
  (middleware/wrap-base
   (ring/ring-handler
    (ring/router
     [(home-routes)
      (service-routes)])
    (ring/routes
     (swagger-ui/create-swagger-ui-handler
      {:path   "/swagger-ui"
       :url    "/api/swagger.json"
       :config {:validator-url nil}})
     (ring/create-resource-handler
      {:path "/"})
     (wrap-content-type
      (wrap-webjars (constantly nil)))
     (ring/create-default-handler
      {:not-found
       (constantly (error-page {:status 404, :title "404 - Page not found"}))
       :method-not-allowed
       (constantly (error-page {:status 405, :title "405 - Not allowed"}))
       :not-acceptable
       (constantly (error-page {:status 406, :title "406 - Not acceptable"}))})))))
