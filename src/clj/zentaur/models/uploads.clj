(ns #^{:author "Manuel Montoya",
       :doc "Message Digest function for Clojure"}
  zentaur.models.uploads
  (:require [zentaur.db.core :as db]
            [struct.core :as st]
            [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [pantomime.extract :as extract]
            [digest :as dgt]
            [clj-time.local :as l])
  (:import  [java.security MessageDigest]
            [java.math BigInteger]))

(def FILE_FORM_PARAM "upload")
(def OUTPUT_DIR "/tmp/")

(def imgstore (atom {}))

;;;;;;;;;;;;;;;;;;;;;;
;;    VALIDATIONS
;;;;;;;;;;;;;;;;;;;;;
(def upload-schema
  [[:title st/required st/string]
   [:body
    st/required
    st/string
    {:body "message must contain at least 10 characters"
     :validate #(> (count %) 9)}]])

(defn validate-upload [params]
  (first
    (st/validate params upload-schema)))

(defn get-uploads [user-id]
  (db/get-uploads {:user-id user-id}))

(defn get-upload [id]
  (let [int-id (Integer/parseInt id)]
    (db/get-upload {:id int-id})))

(defn save-upload! [params]
  (if-let [errors (validate-upload params)]
      (db/save-upload! params)))

(defn copy-file [source-path dest-path]
  (io/copy (io/file source-path) (io/file dest-path)))

(defn long-str [& strings] (clojure.string/join "\n" strings))

(defn initial_json_string
  "Just an initial string as template"
  [origin]
   (str "{ \"title\": \"Some title\",   \n
          \"description\": \"Some description\", \n
          \"instructions\": \"\",  \n
          \"level\": \"1\",  \n
          \"lang\": \"en\",   \n
          \"origin\": \" " origin " \",  \n
          \"tags\": \"tag_one, tag_two\",  \n
          \"status\": \"1\",  \n
          \"questions\": [  \n
            {   \n
              \"status\": \"1\",  \n
              \"qtype\" : \"1\",   \n
              \"hint\" : \"Some hint\",  \n
              \"explanation\": \"\",  \n
              \"question\": \"Some question\", \n
              \"answers\": [
                 { \"answer\": \"\", \"correct\": \"false\" },
                 { \"answer\": \"\", \"correct\": \"false\" }
             ]
           }
          ] }"))

(defn upload-file [params user-id]
  (let [root-path (.getCanonicalPath (io/file "."))
        user-file (:userfile params)
        filename  (:filename user-file)
        tempfile  (:tempfile user-file)
        tags      (:tags params)
        hashvar   (dgt/sha-256 (io/as-file tempfile))
        _         (log/info (str ">>> hashvar >>>>> " hashvar))
        rand5     (crypto.random/hex 5)]
    (copy-file tempfile (str root-path "/resources/public/uploads/" (str rand5 "-" filename)))
    (save-upload!
      (assoc params :filename (str rand5 "-" filename) :created_at (l/local-now) :hashvar hashvar
                    :active true :user_id user-id :tags tags :done false))))

(defn extract-text
  "Convert PDF to txt and save it in the DB"
  [id]
  (let [db-record  (get-upload id)
        filename   (:filename db-record)
        all-file   (extract/parse (str "resources/public/uploads/" filename))
        text       (:text all-file)]
        (db/clj-expr-generic-update {:table   "uploads"
                                     :updates {:content text}
                                     :id      (:id db-record)})))
(defn bbb-download
  [params]
  (let [id       (:id params)
        upload   (db/get-upload {:id id})
        _        (log/info (str ">>> upload >>>>> " upload))
        all-file (extract/parse (str "resources/public/uploads/" (:filename upload)))
        text     (:text all-file)]
    ;; (db/clj-expr-generic-update {:content text :json json :id id})
    (db/clj-expr-generic-update {:table "test"
                                 :updates {:name "X"}
                                 :id 3})))

(defn- download-without-db
  "GET /admin/uploads/download/:id"
  [upload]
  (let [filename (:filename upload)
        body     (clojure.java.io/file (str "resources/public/uploads/" filename))]
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

(defn catto []
  (let [files (mapv str (filter #(.isFile %) (file-seq (clojure.java.io/file "resources/sql/"))))]
      (apply str (for [f files] (slurp f)))))
