(ns zentaur.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [compojure.route :as route]
            [mount.core :as mount]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.util.http-response :as response]
            [zentaur.env :refer [defaults]]
            [zentaur.middleware :as middleware]
            [zentaur.layout :refer [error-page]]
            [zentaur.routes.home :refer [home-routes]]))

(mount/defstate init-app
  :start ((or (:init defaults) identity))
  :stop  ((or (:stop defaults) identity)))

(mount/defstate app
  :start
  (middleware/wrap-base
    (routes
      (-> #'home-routes
          (wrap-routes middleware/wrap-csrf)
          (wrap-cors :access-control-allow-origin [#"http://localhost:8888" #"http://localhost:8888/graphql" #"http://localhost:3000"]
                     :access-control-allow-methods [:get :put :post :delete])
          (wrap-routes middleware/wrap-formats))
          (route/not-found
             (:body
               (error-page {:status 404
                            :title "page not found"}))))))
