(ns zentaur.controllers.export-controller
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [ring.util.http-response :as response]
            [zentaur.models.exports :as model-export]))

(defn export-test-pdf
  "GET /vclass/tests/exportpdf/:uurlid. Create PDF."
  [{:keys [path-params identity]}]
  (let [uurlid   (:uurlid path-params)
        pdf-path (model-export/export-pdf uurlid)
        file     (io/input-stream pdf-path)]
    (log/info (str ">>> PARAM DOC PDF >>>>> " pdf-path))
    (response/content-type (response/ok file) "application/pdf")))

(defn export-test-odt
  "GET /vclass/tests/exportodt/:uurlid. Create OpenDocument file."
  [{:keys [path-params]}]
  (let [uurlid   (:uurlid path-params)
        odt-path (model-export/export-odt uurlid)
        _        (log/info (str ">>> odt-path //////////////// odt-path >>>>> " odt-path))
        name     (last (clojure.string/split odt-path #"/"))
        file     (io/file odt-path)]
     (-> (response/ok (io/input-stream file))
         (response/header "Content-Type" "application/vnd.oasis.opendocument.text")
         (response/header "Content-Disposition" (str "filename=" name ""))
         (response/header "Content-Length" (.length file))
         (response/status 200))))
