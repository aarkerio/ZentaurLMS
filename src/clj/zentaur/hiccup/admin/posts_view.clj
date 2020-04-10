(ns zentaur.hiccup.admin.posts-view
  (:require [clojure.tools.logging :as log]
            [hiccup.form :as f]
            [hiccup.core :as c]
            [zentaur.hiccup.helpers-view :as hv]))

(defn formatted-post [{:keys [title created_at tags discussion published id]}]
  (let [icon (if published "icon_published.png" "icon_draft.png")
        alt  (if published "Published" "Draft")]
  [:tr
    [:td [:a {:href (str "/admin/posts/edit/" id)} [:img {:src "/img/icon_edit_test.png" :alt "Bearbeiten"  :title "Bearbeiten"}]]]
    [:td title]
    [:td tags]
    [:td [:a {:href (str "/admin/posts/published/" id "/" published)}  [:img {:src (str "/img/" icon) :alt alt :title alt}]]]
    [:td created_at]
    [:td [:a {:onclick (str "zentaur.posts.deletepost("id")")} [:img {:src "/img/icon_delete.png" :alt "Delete post" :title "Delete post"}]]]]))

(defn index
  "Admin posts index"
  [posts page]
  (let [total           (:total (first posts))
        formatted-posts (doall (for [post posts]
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
          ; :records        : Total number of records
          ; :per-page       : Items shown per page
          ; :max-pages      : Maximum number of pagination links appear
          ; :current        : Current page number
          ; :biased         : :left or :right, if the number of pages shown is even,
          ;                   current page should either sit in left half of right half
          ; :link-tpl       : template to use for individual links
          ; :list-tpl       : tempate to use for entire list
          (hv/html-paginator {:records total :per-page 5 :max-pages 5 :current page :biased :left :location "/admin/posts/list"})
     ]))

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
    [:div (f/label "discussion" "Discussion") (f/check-box {:title "Active comments" :value "1"} "discussion")]
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
    [:div (f/label "discussion" "Discussion") (f/check-box {:title "Active comments"} "discussion" (:discussion post))]
    (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :id "button-save" :name "button-save"} "Speichern")]])
