(ns zentaur.hiccup.admin.posts-view
  (:require [clojure.tools.logging :as log]
            [hiccup.form :as f]
            [hiccup.core :as c]))

(defn formatted-post [{:keys [title created_at tags discution published id]}]
  [:tr
    [:td [:a {:href (str "/admin/posts/edit/" id)} [:img {:src "/img/icon_edit_test.png" :alt "Bearbeiten"  :title "Bearbeiten"}]]]
    [:td title]
    [:td tags]
    [:td [:a {:href (str "/admin/posts/published/" id "/" published)}  published]]
    [:td created_at]
    [:td [:a {:onclick (str "zentaur.posts.deletepost("id")")} [:img {:src "/img/icon_delete.png" :alt "Delete post" :title "Delete post"}]]]])

(defn index [posts]
  (let [formatted-posts (doall (for [post posts]
                                 (formatted-post post)))]
    [:div {:id "cont"}
      [:div {:id "button-post-neuer"} [:a {:class "btn btn-outline-primary-green" :href "/admin/posts/new"} "Neuer Beitrag"]]
      [:div {:id "content"}
        [:table {:class "some-table-class"}
          [:thead
            [:tr
              [:th "Edit"]
              [:th "Title"]
              [:th "Tags"]
              [:th "Publish"]
              [:th "Created"]
              [:th "Delete"]]]
          [:tbody formatted-posts]]]
      [:nav {:class "blog-pagination"}
        [:a {:class "btn btn-outline-primary-green" :href "#"} "Older"]
       [:a {:class "btn btn-outline-primary-green disabled" :href "#"} "Newer"]]]))

(defn image-icon []
  [:div {:style "text-align:right;padding:8px;float:right;width:30%;"}
        [:img {:src "/img/icon_open_window.png" :alt "Images" :title "images" :id "open_images"}]])

(defn new [base]
  [:div {:id "cont"}
   (image-icon)
   [:form {:action "/admin/posts" :method "post" :id "new-post-form"}
    (f/hidden-field { :value (:csrf-field base)} "__anti-forgery-token")
    [:div (f/text-field {:maxlength 150 :size 90 :placeholder "Title"} "title")]
    [:div (f/text-field {:maxlength 150 :size 70 :placeholder "tags"} "tags")]
    [:div (f/text-area {:cols 90 :rows 20} "body")]
    [:div (f/label "published" "Published") (f/check-box {:title "Publish this" :value "1"} "published")]
    [:div (f/label "discution" "Discution") (f/check-box {:title "Active comments" :value "1"} "discution")]
    (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :id "button-save" :name "button-save"} "Speichern")]])

(defn edit [base post]
  [:div {:id "cont"}
   (image-icon)
   [:form {:action "/admin/posts/update" :method "post" :id "edit-post-form"}
    (f/hidden-field { :value (:csrf-field base)} "__anti-forgery-token")
    (f/hidden-field { :value (:id post)} "id")
    [:div (f/text-field {:maxlength 150 :size 90 :placeholder "Title" :value (:title post)} "title")]
    [:div (f/text-field {:maxlength 150 :size 70 :placeholder "tags" :value (:tags post)} "tags")]
    [:div (f/text-area {:cols 90 :rows 20} "body" (:body post))]
    [:div (f/label "published" "Published") (f/check-box {:title "Publish this"} "published" (:published post))]
    [:div (f/label "discution" "Discution") (f/check-box {:title "Active comments"} "discution" (:discution post))]
    (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :id "button-save" :name "button-save"} "Speichern")]])
