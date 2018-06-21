(ns zentaur.hiccup_templating.admin.uploads-view
  (:require [hiccup.form :as f]
            [hiccup.core :as c]
            [clojure.tools.logging :as log]
            [hiccup.element :only (link-to)]))

(defn formatted-file [file]
  (let [filename   (:filename file)
        created_at (:created_at file)
        tags       (:tags file)
        id         (:id file)]
  [:tr
   [:td filename]
    [:td created_at]
    [:td tags]
    [:td [:a {:href "/admin/uploads/delete/{{id}}"} "Delete"]]]))

(defn index [files csrf-field]
  (let [formatted-files (doall (for [file files]
                                 (formatted-file file)))]
    [:div nil
      [:div {:class "row"}
        [:div {:class "span12"}
          (f/form-to {:enctype "multipart/form-data" :class "form-inline my-2 my-lg-0"}
             [:post "/admin/uploads"]
             (f/hidden-field "__anti-forgery-token" csrf-field)
             (f/file-upload { :class "form-control mr-sm-2" :placeholder "file" } :userfile)
             [:br " "]
             (f/text-field  { :class "form-control mr-sm-2" :placeholder "tags" } :tags)
             (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :name "submit"} "Datei hochladen"))]]
    [:table {:class "some-classs"}
      [:thead
        [:tr
          [:th "File"]
          [:th "Tags"]
          [:th "Created"]
          [:th "Delete"]]]
      [:tbody formatted-files] ]] ))

