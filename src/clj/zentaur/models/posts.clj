(ns zentaur.models.posts
  (:require [clojure.tools.logging :as log]
            [struct.core :as st]
            [zentaur.db.core :as db]
            [zentaur.libs.helpers :as h]
            [zentaur.libs.models.shared :as sh]))

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
  (db/get-post {:id id}))

(defn get-comments [id]
  (db/get-comments {:id id}))

(defn save-comment!
  "POST. /posts/savecomment"
  [params]
  (if-let [errors (validate-post params)]
    (db/save-comment params)))

;;;;;;;;;;;   ADMIN FUNCTIONS  ;;;;;;;;;

(defn admin-get-posts
  [user-id]
   (db/admin-get-posts {:user-id user-id}))

;;  End with ! functions that change state for atoms, metadata, vars, transients, agents and io as well.
(defn save-post!
  [{:keys [params identity]}]
  (if-let [errors (validate-post params)]
    {:flash errors}
    (let [slug      (sh/slugify (:title params))
          published (contains? params :published)
          discution (contains? params :discution)
          user_id   (:id identity)]
      (db/save-post! (assoc params :published published :discution discution :user_id user_id :slug slug))
      {:ok true})))

(defn update-post! [params]
  (let [first-step   (h/update-booleans params [:published :discution])
        second-step  (update first-step :id #(Integer/parseInt %))]
    (db/update-post! second-step)
    {:ok true}))

(defn toggle [{:keys [id published]}]
  (let [new-state (if (= published "true") false true)
        int-id    (Integer/parseInt id)]
    (db/toggle-post! {:id int-id :published new-state})))

(defn destroy [id]
  (let [int-id (Integer/parseInt id)]
    (db/delete-post! {:id int-id})))
