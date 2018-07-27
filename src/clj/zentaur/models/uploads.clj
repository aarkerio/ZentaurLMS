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
  (db/get-upload {:id id}))

(defn save-upload! [params]
  (if-let [errors (validate-upload params)]
      (db/save-upload! params)))

(defn copy-file [source-path dest-path]
  (io/copy (io/file source-path) (io/file dest-path)))

(defn long-str [& strings] (clojure.string/join "\n" strings))

(defn initial_json_string
  "Just an initial string as template"
  [origin]
   (str "{ \"title\": \"Some title\",
          \"description\": \"Some description\",
          \"instructions\": \"\",
          \"level\": \"1\",
          \"lang\": \"en\",
          \"origin\": \" " origin " \",
          \"tags\": \"tag_one, tag_two\",
          \"status\": \"1\",
          \"questions\": [
            {
              \"status\": \"1\",
              \"qtype\" : \"1\",
              \"hint\" : \"Some hint\",
              \"explanation\": \"\",
              \"question\": \"Some question\",
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
(defn extract-text [id]
  (str "extract-text"))

(defn extract-textkk [id]
  (let [db-record  (db/get-upload {:id id})
        file       (:file db-record)
        all-file   (extract/parse (str "path/" file))
        text       (:text all-file)]
        (db/clj-expr-generic-update {:table   "uploads"
                                     :updates {:content text}
                                     :id      (:id db-record)})))
(defn backup-pdf-to-text [params]
  (let [id       (:id params)
        upload   (db/get-upload {:id id})
        _        (log/info (str ">>> upload >>>>> " upload))
        all-file (extract/parse "test/resources/pdf/qrl.pdf")
        text     (:text all-file)
        ;; json (initial_json_string hashvar)
        ]
    ;; (db/clj-expr-generic-update {:content text :json json :id id})
    (db/clj-expr-generic-update {:table "test"
                               :updates {:name "X"}
                               :id 3})))

