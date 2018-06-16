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

(defn get-uploads [user-id]
  (db/get-uploads {:user-id user-id}))

(defn save-upload! [params]
  (if-let [errors (validate-upload params)]
      (db/save-upload! params)))

(defn copy-file [source-path dest-path]
  (io/copy (io/file source-path) (io/file dest-path)))

(defn upload-file [params user-id]
  (let [root-path (.getCanonicalPath (io/file "."))
        user-file (:userfile params)
        filename  (:filename user-file)
        tempfile  (:tempfile user-file)
        tags      (:tags params)
        rand5     (crypto.random/hex 5)]
    (copy-file tempfile (str root-path "/resources/public/uploads/" (str rand5 "-" filename)))
    (save-upload!
      (assoc params :filename (str rand5 "-" filename) :created_at (l/local-now) :hashvar rand5
                    :active true :user_id user-id :tags tags))))

