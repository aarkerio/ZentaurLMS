(ns ^:test-model zentaur.models.exports
  "Business logic for the export section"
  (:require [clojure.tools.logging :as log]
            [zentaur.db.core :as db]
            [zentaur.libs.helpers :as h]))

;; (defn export-pdf [test-id]
;;   (let [test-id (inc test-id)]
;;     (db/remove-test! {:test-id test-id})))

;; (defn export-odf
;;   "Export to open document format"
;;   [test-id]
;;   (let [test-id (inc test-id)]
;;     (db/remove-test! {:test-id test-id})))
