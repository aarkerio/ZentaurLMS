(ns zentaur.hiccup_templating.admin.posts-view
  (:require [hiccup.form :as f]
            [hiccup.core :as c]
            [zentaur.hiccup_templating.posts-view :as posts-view]
            [clojure.tools.logging :as log]
            [hiccup.element :only (link-to)]))

(defn index [posts]
  (let [formatted-posts (doall (for [post posts]
                                 (posts-view/format-post post)))]
    [:div {:id "cont"}
      [:div {:id "content"} [:a {:class "btn btn-outline-primary" :href "/admin/posts/new"} "Neuer Beitrag"]]
      [:div {:id "content"} formatted-posts]
      [:nav {:class "blog-pagination"}
        [:a {:class "btn btn-outline-primary" :href "#"} "Older"]
        [:a {:class "btn btn-outline-secondary disabled" :href "#"} "Newer"]]]))

(defn new [base user-id]
  [:div {:id "cont"}
             (f/form-to [:post "/admin/posts"]
                (f/hidden-field { :value (:csrf-field base)} "__anti-forgery-token")
                (f/hidden-field { :value user-id} "user_id")
                [:div (f/text-field {:maxlength 150 :size 90 :placeholder "Title"} "title")]
                [:div (f/text-field {:maxlength 150 :size 70 :placeholder "tags"} "tags")]
                [:div (f/text-area {:cols 90 :rows 20} "body")]
                [:div (f/label "published" "Published") (f/check-box {:title "Publish this" :value "1"} "published")]
                [:div (f/label "discution" "Discution") (f/check-box {:title "Active comments" :value "1"} "discution")]
                (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :id "button-save" :name "button-save"} "Einrichen")) ] )

