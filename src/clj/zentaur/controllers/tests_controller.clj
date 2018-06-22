(ns zentaur.controllers.tests-controller
  (:require [clojure.tools.logging :as log]
            [zentaur.controllers.base-controller :as basec]
            [zentaur.models.tests :as model-test]
            [zentaur.hiccup_templating.layout-view :as layout]
            [zentaur.hiccup_templating.tests-view :as tests-view]
            [ring.util.http-response :as response]))

;; GET /tests
(defn get-tests [request]
  (let [base     (basec/set-vars request)
        user-id  (-> request :identity :id)
        posts    (model-test/admin-get-posts user-id)]
    (layout/application
        (merge base {:title "Tests" :contents (tests-view/index posts) }))))

(defn get-admin [request]
  (let [base     (basec/set-vars request)]
    (layout/application "admin/index.html")))

(defn about-page [request]
  (let [base     (basec/set-vars request)]
     (layout/application {:contents base})))

;;;;;  ADMIN FUNCTIONS

;; GET /admin/tests
(defn admin-tests [request]
  (let [base     (basec/set-vars request)
        posts    (model-test/get-posts)]
    (layout/application
        (merge base {:title "Tests" :contents (tests-view/index posts) }))))
