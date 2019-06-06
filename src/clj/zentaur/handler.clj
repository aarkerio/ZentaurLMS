(ns zentaur.handler
  (:require [mount.core :as mount]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.webjars :refer [wrap-webjars]]
            [reitit.ring :as ring]
            [zentaur.env :refer [defaults]]
            [zentaur.middleware :as middleware]
            [zentaur.layout :refer [error-page]]
            [zentaur.routes.home :refer [home-routes]]))

(mount/defstate init-app
  :start ((or (:init defaults) (fn [])))
  :stop  ((or (:stop defaults) (fn []))))

(mount/defstate app   ;; put togheter all the middleware
  :start
  (middleware/wrap-base
    (ring/ring-handler
      (ring/router
        [(home-routes)])
      (ring/routes
        (ring/create-resource-handler {:path "/"})
        (wrap-content-type
          (wrap-webjars (constantly nil)))
        (ring/create-default-handler
          {:not-found
           (constantly (error-page {:status 404, :title "404 - Page not found"}))
           :method-not-allowed
           (constantly (error-page {:status 405, :title "405 - Not allowed"}))
           :not-acceptable
           (constantly (error-page {:status 406, :title "406 - Not acceptable"}))})))))

