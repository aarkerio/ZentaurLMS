(ns zentaur.hiccup.posts-view
  (:require [clojure.tools.logging :as log]
            [hiccup.core :as c]
            [hiccup.form :as f]
            [hiccup.element :refer [link-to]]
            [markdown.core :as md]
            [zentaur.hiccup.helpers-view :as hv]))

(defn format-post
  ([post] (format-post post true))
  ([post view]
   (let [div-blog   [:div {:class "blog-post"} [:h2 {:class "blog-post-title"} (:title post)]
                      [:div {:class "blog-post-meta"} (hv/format-date (:created_at post)) " " [:a {:href (str "/user/" (:uname post))} (:uname post)]]
                      [:div {:class "blog-body"} (md/md-to-html-string (:body post))]
                      [:div {:class "blog-tags"} (:tags post)]]
         view-link  (cond view (conj div-blog [:p [:a {:href (str "/posts/view/" (:id post))} "View"]]))]
     (if (= view true)
       view-link
       div-blog))))

(defn format-comment [comment]
    [:div {:class "user_comments"}
        [:div {:style "font-size:8pt;"} (:created_at comment)]
        [:div {:style "font-size:8pt;font-weight:bold;"} (str (:last_name comment) " wrote: ")]
        [:div {:class "font"} (:comment comment)]])

(defn index [posts]
  (let [formatted-posts (doall (for [post posts]
                                 (format-post post)))]
    [:div {:id "cont"}
      [:div {:id "content"} formatted-posts]
      [:nav {:class "blog-pagination"}
        [:a {:class "btn btn-outline-primary" :href "#"} "Older"]
        [:a {:class "btn btn-outline-secondary disabled" :href "#"} "Newer"]]]))

(defn comment-form [base id]
  (when-let [email (-> base :identity :email)]
            [:form {:id "submit-comment-form" :action "/vclass/posts/comments" :method "post" :class "css-class-form"}
                (f/hidden-field {:value (:csrf-field base)} "__anti-forgery-token")
                (f/hidden-field {:value id} "post_id")
                [:div (f/text-area {:cols 90 :rows 5} "comment-textarea")]
                (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :id "button-save" :name "button-save"} "Speichern")]))

(defn show [post base comments]
  (let [formatted-post (format-post post false)
        formatted-comments (for [comment comments]
                             (format-comment comment))
        comment-form (comment-form base (:id post))]
    [:div {:id "cont"}
     [:div {:id "content"} formatted-post]
     [:div {:id "comments"} formatted-comments]
     [:div {:id "comment-form"} comment-form]]))


