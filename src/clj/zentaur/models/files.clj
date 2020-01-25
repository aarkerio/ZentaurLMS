(ns zentaur.models.files
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [digest :as dgt]
            [java-time :as jt]
            [pantomime.extract :as extract]
            [struct.core :as st]
            [zentaur.db.core :as db]
            [zentaur.libs.helpers :as h])
  (:import  [java.security MessageDigest]
            [java.math BigInteger]))

(def FILE_FORM_PARAM "upload")
(def OUTPUT_DIR "/tmp/")

(def imgstore (atom {}))

;;;;;;;;;;;;;;;;;;;;;;
;;    VALIDATIONS
;;;;;;;;;;;;;;;;;;;;;
(def file-schema
  [[:identifier st/required st/string {:body "identifier field is obligatory"}]
   [:user-id  st/required st/integer {:body "user-id field is obligatory"}]
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

(defn get-files [user-id]
  (db/get-files {:user-id user-id}))

(defn get-upload [identifier user-id]
  (let [int-id (Integer/parseInt user-id)]
    (db/get-file {:identifier identifier :user-id user-id})))

(defn save-file! [params]
  (if-let [errors (validate-file params)]
    {:errors errors}
    (db/save-file! params)))

(defn upload-file [params user-id uname]
  (let [root-path    (.getCanonicalPath (io/file "."))
        rand7        (crypto.random/hex 7)
        upload-image (:upload-image params)
        _            (log/info (str ">>> UPLOAD PARAM params >>>>> " params))
        filename     (:filename upload-image)
        tempfile     (:tempfile upload-image)
        _            (log/info (str ">>> UPLOAD filename >>>>> " filename  " and tempfile " tempfile))
        unique-name  (str rand7 "-" filename)
        identifier   (str rand7 "-" (dgt/sha-256 (io/as-file tempfile)))
        final-path   (str root-path "/resources/public/files/" uname "/" unique-name)
        _            (io/make-parents final-path)
        db-row       (assoc {} :file unique-name :user-id user-id :identifier identifier :img true)
        _            (log/info (str ">>> DB-ROW >>>>> " db-row))
        ]
    (if-not (db/get-file-by-identifier {:identifier identifier})
      (do (log/info (str ">>> tempfile >>>>> " tempfile "   und final-path >>>>>" final-path))
          (h/copy-file tempfile final-path)
          (save-file! db-row))
      false)))

(defn- download-without-db
  "GET. /admin/uploads/download/:id"
  [upload]
  (let [filename (:filename upload)
        body     (io/file (str "resources/public/uploads/" filename))]
          {:status 200
           :body body
           :headers {"Content-Type" "application/pdf"
                     "Content-Length" (str (.length body))
                     "Cache-Control" "no-cache"
                     "Content-Disposition" (str "attachment; filename=" filename)}}))

(defn- get-upload-from-db [id]
  (get-upload id))

(defn download [id]
  (-> id
      get-upload-from-db
      download-without-db))

(defn toggle-archive
  "Pass the file to the archive zone"
  [{:keys [id published]}]
  (let [new-state (if (= published "true") false true)
        int-id    (Integer/parseInt id)]
    (db/toggle-post! {:id int-id :published new-state})))
