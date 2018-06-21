(ns zentaur.controllers.tests-controller
  (:require [zentaur.controllers.base-controller :as basec]
            [clojure.tools.logging :as log]
            [zentaur.hiccup_templating.layout-view :as layout]
            [ring.util.http-response :as response]))

;; GET /admin/tests
(defn get-tests [request]
  (let [base     (basec/set-vars request)
        posts    (model-post/admin-get-posts user-id)]
    (layout/application
        (merge base {:title "Tests" :contents (posts-view/index posts) }))))

(defn get-admin [request]
  (let [base     (basec/set-vars request)]
    (layout/application "admin/index.html")))

(defn about-page [request]
  (let [base     (basec/set-vars request)]
     (layout/application {:contents base})))

;;;;;  ADMIN FUNCTIONS

;; GET /admin/tests
(defn get-posts [request]
  (let [base     (basec/set-vars request)
        posts    (model-post/get-posts)]
    (layout/application
        (merge base {:title "Tests" :contents (test-view/index posts) }))))
