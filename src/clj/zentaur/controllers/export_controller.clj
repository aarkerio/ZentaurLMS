(ns zentaur.controllers.export-controller
  (:require [clojure.tools.logging :as log]
            [zentaur.models.exports :as model-export]
            [ring.util.http-response :as response]))


(defn export-test-pdf
  "GET /admin/tests/exporttestpdf/:id. Create PDF."
  [{:keys [params]}]
  (let [test-id  (:id params)
        user-id  (:user-id params)]
    ;; (model-export/export-pdf test-id user-id)
    ))

(defn export-test-odf
  "GET /admin/tests/exporttestodf/:id. Create PDF."
  [{:keys [params]}]
  (let [test-id  (:id params)
        user-id  (:user-id params)]
       ;; (model-export/export-odf test-id user-id)
   ))
