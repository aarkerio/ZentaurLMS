(ns zentaur.hiccup.files-view
  (:require [clojure.tools.logging :as log]
            [hiccup.core :as c]
            [hiccup.form :as f]
            [zentaur.hiccup.helpers-view :as hv]))

(defn format-file
  [file uname type]
  (let [uurlid   (:uurlid file)
        archived (:archived file)
        url      (str "/files/" uname "/" (:file file))]
    [:div {:style "width:100%;"} [:a {:href url } (:file file)] "  "
     [:img {:src "/img/icon_clipboard.png" :alt "Archive file" :title "Archive file" :onclick (str "zentaur.core.copytoclipboard('"url"')")}]
     (hv/format-date (:created_at file))
     [:a {:href (str "/vclass/files/archive/" type "/" uurlid "/" archived)} [:img {:src "/img/icon_archive.png" :alt "Archive file" :title "Archive file"}]]]))

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
     [:div {:id "content-files"} formatted-files]
      [:nav {:class "blog-pagination"}
        [:a {:class "btn btn-outline-primary-green" :href "#"} "Older"]
        [:a {:class "btn btn-outline-primary-green disabled" :href "#"} "Newer"]]]))


