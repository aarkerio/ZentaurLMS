(ns zentaur.controllers.export-controller
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [zentaur.models.exports :as model-export]
            [ring.util.http-response :as response]))

(defn export-test-pdf
  "GET /vclass/tests/exporttestpdf/:id. Create PDF."
  [{:keys [path-params identity]}]
  (log/info (str ">>> PATH PARAM >>>>> " path-params   " >>>> identity >>>"  identity ))
  (let [test-id  (:id (update path-params :id #(Integer/parseInt %)))
        user-id  (:id identity)
        pdf-path (model-export/export-pdf test-id user-id)
        file     (io/input-stream pdf-path)]
    (log/info (str ">>> PARAM DOC PDF >>>>> " pdf-path))
    (response/content-type (response/ok file) "application/pdf")))

(defn export-test-odf
  "GET /vclass/tests/exporttestodf/:id. Create ODF."
  [{:keys [params]}]
  (let [test-id  (:id params)
        user-id  (:user-id params)]
       ;; (model-export/export-odf test-id user-id)
   ))
