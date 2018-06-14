(ns zentaur.models.posts
  (:require [zentaur.db.core :as db]
            [zentaur.env :as env]
            [struct.core :as st]
            [clojure.tools.logging :as log]
            [slugify.core :refer [slugify]]
            [clj-time.local :as l]))

;;;;;;;;;;;;;;;;;;;;;;
;;    VALIDATIONS
;;;;;;;;;;;;;;;;;;;;;
(def post-schema
  [[:title st/required st/string]
   [:body
    st/required
    st/string
    {:body "message must contain at least 10 characters"
     :validate #(> (count %) 9)}]])

(defn validate-post [params]
  (first
    (st/validate params post-schema)))

(def comment-schema
  [[:comment st/required st/string]
   [:post_id st/required st/integer]])

(defn validate-comment [params]
  (first
    (st/validate params comment-schema)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;          ACTIONS
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-posts []
     (db/get-posts))

(defn get-post [id]
  (db/get-post id))

(defn get-comments [id]
  (db/get-comments id))

;;  End with ! functions that change state for atoms, metadata, vars, transients, agents and io as well.
(defn save-post! [params]
  (if-let [errors (validate-post params)]
      (db/save-post! params)))

(defn save-comment! [params]
  (if-let [errors (validate-post params)]
      (db/save-comment params)))

(defn destroy [params]
  (do
    (db/delete-post! params)))


;;;;;;;;;;;   ADMIN FUNCTIONS  ;;;;;;;;;
(defn admin-get-posts [user-id]
    (db/admin-get-posts {:user-id user-id}))

