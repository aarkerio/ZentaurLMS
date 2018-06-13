(ns zentaur.hiccup_templating.admin.uploads-view
  (:require [hiccup.form :as f]
            [hiccup.core :as c]
            [clojure.tools.logging :as log]
            [hiccup.element :only (link-to)]))

(defn formatted-file [file]
    [:div {:style "margin:3px; padding:4px; border: dotted gray 1px;"}
        [:div (:filename file)]
        [:div (:created_at file)]
        [:div (:tags file)]
        [:div [:a {:href "/admin/uploads/delete/{{(:id file)}}"} "Delete"]]])

(defn index [files content]
  (let [formatted-files (doall (for [file files]
                                 (formatted-file file)))]
  [:div {:class "row"}
    [:div {:class "span12"}
       (f/form-to [:post "/admin/uploads" {:enctype "multipart/form-data" :class "form-inline my-2 my-lg-0"}]
           (f/hidden-field { :value (:csrf-field content)} "__anti-forgery-token")
           (f/file-upload { :class "form-control mr-sm-2" :placeholder "file" } :userfile )
           (f/text-field  { :class "form-control mr-sm-2" :placeholder "email" } :email)
           (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :name "submit"} "Anmeldung"))]]))
