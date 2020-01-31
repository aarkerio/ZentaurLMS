(ns zentaur.controllers.tests-controller
  (:require [cheshire.core :as ches]
            [clojure.tools.logging :as log]
            [zentaur.controllers.base-controller :as basec]
            [zentaur.models.tests :as model-test]
            [zentaur.hiccup.admin.tests-view :as tests-view]
            [zentaur.hiccup.layouts.application-layout :as layout]
            [ring.util.http-response :as response]))

(defn index
  "GET /vclass/tests. Display user's tests. Html response."
  [request]
  (let [base     (basec/set-vars request)
        user-id  (-> request :identity :id)
        tests    (model-test/get-tests user-id)
        subjects (model-test/get-subjects)]
    (basec/parser
     (layout/application (merge base {:title "Quiz Tests" :contents (tests-view/index tests base subjects)})))))

(defn edit
  "GET /vclass/tests/edit/:id. Html response."
  [request]
  (let [base     (basec/set-vars request)
        test-id  (-> request :path-params :id)]
    (basec/parser
     (layout/application (merge base {:title "New Quiz Tests" :contents (tests-view/edit base test-id) })))))

