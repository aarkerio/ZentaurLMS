(ns blog.handler
  (:require [blog.hiccup_templating.layout-view :as layout]
            [blog.hiccup_templating.helpers-view :as helper-view]
            [blog.middleware :as middleware]
            [blog.routes.home :refer [base-routes]]
            [blog.env :refer [defaults]]
            [mount.core :as mount]
            [compojure.route :as route]
            [compojure.core :refer [routes wrap-routes]]))

(mount/defstate init-app
                :start ((or (:init defaults) identity))
                :stop  ((or (:stop defaults) identity)))

(def app-routes
  (routes
    (-> #'base-routes
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-restricted))
    (route/not-found
      (layout/application {:title "Page not found"
                           :contents (helper-view/http-status {:status 404
                                                               :title "Seite nicht gefunden"
                                                               :message "Entschuldigung, Seite nicht gefunden"})}))))

;;  #' link to code not to value
(defn app [] (middleware/wrap-base #'app-routes))

