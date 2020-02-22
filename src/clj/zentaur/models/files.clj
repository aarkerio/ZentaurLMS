(ns zentaur.models.files
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [digest :as dgt]
            [java-time :as jt]
            [pantomime.extract :as extract]
            [struct.core :as st]
            [zentaur.db.core :as db]
            [zentaur.libs.models.shared :as sh])
  (:import  [java.security MessageDigest]
            [java.math BigInteger]))

(def FILE_FORM_PARAM "upload")
(def OUTPUT_DIR "/tmp/")

(def imgstore (atom {}))

;;;;;;;;;;;;;;;;;;;;;;
;;    VALIDATIONS
;;;;;;;;;;;;;;;;;;;;;
(def file-schema
  [[:uurlid  st/required st/string {:body "identifier field is obligatory"}]
   [:user-id st/required st/integer {:body "user-id field is obligatory"}]
   [:file
    st/required
    st/string
    {:body "File name must contain at least 5 characters"
     :validate #(> (count %) 5)}]])

(defn validate-file [params]
  (first
    (st/validate params file-schema)))

;;;;;;;;;;;;;;;;;;;;;;
;;    ACTIONS
;;;;;;;;;;;;;;;;;;;;;

(defn get-files [user-id archived]
  (db/get-files {:user-id user-id :archived archived}))

(defn get-one-file [user-id uurlid]
    (db/get-one-file {:uurlid uurlid :user-id user-id}))

(defn save-file! [params]
  (if-let [errors (validate-file params)]
    {:errors errors}
    (db/save-file! params)))

(defn upload-file [params user-id uname]
  (let [root-path     (.getCanonicalPath (io/file "."))
        rand7         (crypto.random/hex 7)
        uploaded-file (:file params)
        filename      (:filename uploaded-file)
        tempfile      (:tempfile uploaded-file)
        unique-name   (str rand7 "-" filename)
        uurlid        (str rand7 "-" (dgt/sha-256 (io/as-file tempfile)))
        final-path    (str root-path "/resources/public/files/" uname "/" unique-name)
        _             (io/make-parents final-path) ;; create the path if it doesn't exist
        db-row        (assoc {} :file unique-name :user-id user-id :uurlid uurlid :img true)
        _             (log/info (str ">>> DB-ROW >>>>> " db-row))]
    (if-not (and (db/get-one-file {:user-id user-id :uurlid uurlid})
                 (seq filename))
      (do (sh/copy-file tempfile final-path)
          (save-file! db-row))
      false)))

(defn download
  "Download the file"
  [uurlid user-id]
  (let [file (db/get-one-file {:uurlid uurlid :user-id user-id})
        filename (:filename file)
        body     (io/file (str "resources/public/files/" filename))]
          {:status 200
           :body body
           :headers {"Content-Type" "application/pdf"
                     "Content-Length" (str (.length body))
                     "Cache-Control" "no-cache"
                     "Content-Disposition" (str "attachment; filename=" filename)}}))

(defn toggle-archive
  "Chnage the archive flag field"
  [uurlid archived]
  (let [new-state (if (= archived "true") false true)]
    (db/toggle-file {:uurlid uurlid :archived new-state})))
