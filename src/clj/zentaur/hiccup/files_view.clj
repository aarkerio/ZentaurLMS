(ns zentaur.hiccup.files-view
  (:require [clojure.tools.logging :as log]
            [hiccup.core :as c]
            [hiccup.form :as f]
            [zentaur.hiccup.helpers-view :as hv]))

(defn format-file
  [file uname type]
  (let [uurlid   (:uurlid file)
        archived (:archived file)
        url      (str "/files/" uname "/" (:file file))
        up-date  (hv/format-date (:created_at file))]
    [:tr
     [:td [:a {:href url } (:file file)]]
     [:td [:img {:src "/img/icon_clipboard.png" :alt "Copy to clipboard" :title "Copy to clipboard" :onclick (str "zentaur.core.copytoclipboard('"url"')")}]]
     [:td up-date]
     [:td  [:a {:href (str "/vclass/files/archive/" type "/" uurlid "/" archived)} [:img {:src "/img/icon_archive.png" :alt "Archive file" :title "Archive file"}]]]
     [:td [:a {:onclick (str "zentaur.core.deletefile(" uurlid ")")} [:img {:src "/img/icon_delete.png" :alt "Delete file" :title "Delete file"}]]]]))

(defn index [files base type]
  (let [uname           (-> base :identity :uname)
        formatted-files (for [file files]
                          (format-file file uname type))]
    [:div {:id "cont"}
     (f/form-to {:enctype "multipart/form-data" :class "form-inline my-2 my-lg-0" :id "upload-file-form"}
                [:post "/vclass/files"]
                (f/hidden-field {:value (:csrf-field base)} "__anti-forgery-token")
                (f/hidden-field {:value type} "type")
                [:div.div-separator (f/file-upload {:placeholder "Upload file"} "file")]
                [:div (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :id "button-save" :name "button-save"} "Speichern")])
     [:table {:class "some-table-class"}
      [:thead
       [:tr
        [:th "File"]
        [:th "Uploaded"]
        [:th "Copy to clipboard"]
        [:th "Sent to archived files"]
        [:th "Löschen"]]]
      [:tbody formatted-files]]
      [:nav {:class "blog-pagination"}
        [:a {:class "btn btn-outline-primary-green" :href "#"} "Older"]
        [:a {:class "btn btn-outline-primary-green disabled" :href "#"} "Newer"]]]))


