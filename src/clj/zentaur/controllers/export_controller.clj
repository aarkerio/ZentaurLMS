(ns zentaur.controllers.export-controller
  (:require [clojure.tools.logging :as log]
            [zentaur.models.exports :as model-export]
            [ring.util.http-response :as response]))


(defn export-test-pdf
  "GET /vclass/tests/exporttestpdf/:id. Create PDF."
  [{:keys [params identity]}]
  (let [test-id  (:id params)
        user-id  (:id identity)
        pdf-path (model-export/export-pdf test-id user-id)]
        file     (slurp pdf-path)
    (log/info (str ">>> PARAM DOC PDF >>>>> " file))
    (response/content-type (response/ok file) "application/pdf")
     ))

(defn export-test-odf
  "GET /vclass/tests/exporttestodf/:id. Create ODF."
  [{:keys [params]}]
  (let [test-id  (:id params)
        user-id  (:user-id params)]
       ;; (model-export/export-odf test-id user-id)
   ))
