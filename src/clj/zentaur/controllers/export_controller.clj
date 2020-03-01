(ns zentaur.controllers.export-controller
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [ring.util.http-response :as response]
            [zentaur.models.exports :as model-export]))

(defn export-test-pdf
  "GET /vclass/tests/exportpdf/:uurlid. Create PDF."
  [{:keys [path-params identity]}]
  (log/info (str ">>> PATH PARAM >>>>> " path-params   " >>>> identity >>>"  identity ))
  (let [uurlid   (:uurlid path-params)
        pdf-path (model-export/export-pdf uurlid)
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
