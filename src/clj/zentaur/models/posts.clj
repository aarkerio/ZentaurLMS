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
  [[:title st/required st/string
    {:title "title must contain at least 2 characters"
     :validate #(> (count %) 1)}]
   [:body st/required st/string
    {:body "the body must contain at least 10 characters"
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

(defn get-posts
  "Get all published posts"
  []
   (db/get-posts))

(defn get-post [id]
  (db/get-post id))

(defn get-comments [id]
  (db/get-comments id))

(defn save-comment! [params]
  (if-let [errors (validate-post params)]
      (db/save-comment params)))

;;;;;;;;;;;   ADMIN FUNCTIONS  ;;;;;;;;;

(defn admin-get-posts [user-id]
    (db/admin-get-posts {:user-id user-id}))

;;  End with ! functions that change state for atoms, metadata, vars, transients, agents and io as well.
(defn save-post! [params]
  (log/info (str ">>> PARAM MODEL >>>>> " params))
  (if-let [errors (-> params (validate-post))]
    {:flash errors}
    (let [slug      (slugify (:title params))
          active    (contains? params :active)
          discution (contains? params :discution)
          int_ui    (Integer/parseInt (:user_id params))]
      (db/save-post! (assoc params :active active :discution discution :user_id int_ui :slug slug)))))

(defn destroy [id]
    (db/delete-post! {:id id}))

