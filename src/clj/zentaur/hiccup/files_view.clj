(ns zentaur.hiccup.files-view
  (:require [clojure.tools.logging :as log]
            [hiccup.core :as c]
            [hiccup.form :as f]
            [zentaur.hiccup.helpers-view :as hv]))

(defn format-file
  [file]
  (let [id (:id file)]
    [:div {:class "blog-post"} (:file file) " " (hv/format-date (:created_at file)) " "
     [:a {:onclick (str "zentaur.files.deletefile("id")")} [:img {:src "/img/icon_delete.png" :alt "Delete file" :title "Delete file"}]]]))

(defn format-comment [comment]
    [:div {:class "user_comments"}
        [:div {:style "font-size:8pt;"} (:created_at comment)]
        [:div {:style "font-size:8pt;font-weight:bold;"} (str (:last_name comment) " wrote: ")]
        [:div {:class "font"} (:comment comment)]])

(defn index [files csrf-field]
  (let [formatted-files (doall (for [file files]
                                 (format-file file)))]
    [:div {:id "cont"}
     (f/form-to {:enctype "multipart/form-data" :class "form-inline my-2 my-lg-0"}
                [:post "/vclass/files"]
      (f/hidden-field {:value csrf-field} "__anti-forgery-token")
      [:div.div-separator (f/file-upload {:placeholder "Upload image"} "upload-image")]
      (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :id "button-save" :name "button-save"} "Speichern"))
      [:div {:id "content"} formatted-files]
      [:nav {:class "blog-pagination"}
        [:a {:class "btn btn-outline-primary-green" :href "#"} "Older"]
        [:a {:class "btn btn-outline-primary-green disabled" :href "#"} "Newer"]]]))

(defn comment-form [base id]
  (when-let [email (-> base :identity :email)]
            [:form {:id "submit-comment-form" :action "/vclass/posts/comments" :method "post" :class "css-class-form"}
                (f/hidden-field {:value (:csrf-field base)} "__anti-forgery-token")
                (f/hidden-field {:value id} "post_id")
                [:div (f/text-area {:cols 90 :rows 5} "comment-textarea")]
                (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :id "button-save" :name "button-save"} "Speichern")]))

