(ns zentaur.routes.home
  (:require [clojure.java.io :as io]
            [ring.util.http-response :as response]
            [zentaur.controllers.posts-controller :as cont-post]
            [zentaur.db.core :as db]
            [zentaur.layout :as layout]
            [zentaur.middleware :as middleware]))

(defn home-page [request]
  (layout/render request "home.html"))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/posts" {:get cont-post/get-posts}]
   ["/docs" {:get (fn [_]
                    (-> (response/ok (-> "docs/docs.md" io/resource slurp))
                        (response/header "Content-Type" "text/plain; charset=utf-8")))}]])
