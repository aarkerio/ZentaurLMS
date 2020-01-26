(ns ^:test-model zentaur.models.exports
  "Business logic for the export section"
  (:require [clj-pdf.core :as pdf]
            [clojure.tools.logging :as log]
            [zentaur.db.core :as db]
            [zentaur.libs.helpers :as h]))

(defn export-pdf [test-id user-id]
  (let [test (db/get-one-test {:id test-id :user-id user-id})
        file-name "resources/public/tmp/document-perro-sfwewr.pdf"]
    (pdf/pdf
     [{}
      [:list {:roman true}
       [:chunk {:style :bold} "a bold item"]
       "another item"
       "yet another item"]
      [:phrase "some text"]
      [:phrase "some more text"]
      [:paragraph "yet more text"]]
     file-name)
    ))

;; (defn export-odf
;;   "Export to open document format"
;;   [test-id]
;;   (let [test-id (inc test-id)]
;;     (db/remove-test! {:test-id test-id})))
