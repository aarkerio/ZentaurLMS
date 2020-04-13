(ns zentaur.models.posts
  (:require [clojure.tools.logging :as log]
            [struct.core :as st]
            [zentaur.db.core :as db]
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
  ([]      (get-posts 1))
  ([page]  (get-posts 1 5))
  ([page items-per-page]
   (let [offset (* (dec page) items-per-page)]
     (db/get-posts {:limit items-per-page :offset offset}))))

(defn get-post [id]
  (db/get-post {:id id}))

(defn get-comments [id]
  (db/get-comments {:id id}))

(defn save-comment!
  [params]
  (if-let [errors (validate-post params)]
    (db/save-comment params)))

(defn search
  [terms]
  (let [errors 1]
    "(db/search terms)"
    ))

;;;;;;;;;;;   ADMIN FUNCTIONS  ;;;;;;;;;

(defn admin-get-posts
  [user-id page items-per-page]
  (let [offset (* (dec page) items-per-page)] ;; dec because we need pagination to starts at 0 and "page" starts at 1
      (db/admin-get-posts {:user-id user-id :offset offset :limit items-per-page})))

;;  End with ! functions that change state for atoms, metadata, vars, transients, agents and io as well.
(defn save-post!
  [{:keys [params identity]}]
  (if-let [errors (validate-post params)]
    {:flash errors}
    (let [slug      (sh/slugify (:title params))
          published (contains? params :published)
          discussion (contains? params :discussion)
          user_id   (:id identity)]
      (db/save-post! (assoc params :published published :discussion discussion :user_id user_id :slug slug))
      {:ok true})))

(defn update-post! [params]
  (let [first-step   (sh/update-booleans params [:published :discussion])
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
