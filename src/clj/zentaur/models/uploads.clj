(ns zentaur.models.uploads
  (:require [zentaur.db.core :as db]
            [struct.core :as st]
            [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [clj-time.local :as l]))

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

(defn get-uploads [user_id]
  (log/info (str ">>> idddddddd ######## >>>>> " user_id))
  (db/get-uploads user_id))

(defn save-upload! [params]
  (if-let [errors (validate-upload params)]
      (db/save-upload! params)))

(defn copy-file [source-path dest-path]
  (io/copy (io/file source-path) (io/file dest-path)))

(defn upload-file [params identity]
  (let [root-path (.getCanonicalPath (io/file "."))
        user-file (:userfile params)
        user-id   (:id identity)
        rand5     (crypto.random/hex 5)]
    ;; (log/info (str ">>> FILE >>>>> " params " root-path >>> " root-path " user-file >>> " (str rand5 "-" (:filename user-file))))
    (copy-file (get user-file :tempfile) (str root-path "/resources/public/img/uploads/" (str rand5 "-" (:filename user-file))))
    (save-upload!
       (assoc params :filename (str rand5 "-" (:filename user-file)) :created_at (l/local-now) :active true :user_id  user-id :tags "etwas tags"))))

