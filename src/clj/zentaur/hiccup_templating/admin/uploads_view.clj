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

(defn index [files csrf-field]
  (let [formatted-files (doall (for [file files]
                                 (formatted-file file)))]
    [:div
    [:div {:class "row"}
      [:div {:class "span12"}
        (f/form-to {:enctype "multipart/form-data" :class "form-inline my-2 my-lg-0"}
           [:post "/admin/uploads"]
           (f/hidden-field "__anti-forgery-token" csrf-field)
           (f/file-upload { :class "form-control mr-sm-2" :placeholder "file" } :userfile)
           [:br " "]
           (f/text-field  { :class "form-control mr-sm-2" :placeholder "tags" } :tags)
           (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :name "submit"} "Datei hochladen"))]]
    [:div formatted-files]]
  ))
