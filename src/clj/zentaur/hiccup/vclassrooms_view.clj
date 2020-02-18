(ns zentaur.hiccup.vclassrooms-view
  (:require [clojure.tools.logging :as log]
            [hiccup.core :as c]
            [hiccup.form :as f]
            [hiccup.element :refer [link-to]]
            [markdown.core :as md]
            [zentaur.hiccup.helpers-view :as hv]))

(defn format-row
  [vclassroom]
  [:div {:class "blog-post"} [:a {:class "btn btn-outline-primary" :href (str "/vclass/show/" (:id vclassroom))} (:name vclassroom)] ]
  [:div {:class "blog-post-meta"} (hv/format-date (:created_at vclassroom)) " "
   [:div {:class "blog-body"} (md/md-to-html-string (:body post))]
   [:div {:class "blog-tags"} (:tags post)]]])

(defn format-comment [comment]
    [:div {:class "user_comments"}
        [:div {:style "font-size:8pt;"} (:created_at comment)]
        [:div {:style "font-size:8pt;font-weight:bold;"} (str (:last_name comment) " wrote: ")]
        [:div {:class "font"} (:comment comment)]])

(defn index [vclassrooms]
  (let [formatted-vclassrooms (doall (for [vc vclassrooms]
                                 (format-row vc)))]
    [:div {:id "cont"}
      [:div {:id "content"} formatted-vclassrooms]
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


