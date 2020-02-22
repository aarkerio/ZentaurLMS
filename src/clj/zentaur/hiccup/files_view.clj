(ns zentaur.hiccup.files-view
  (:require [clojure.tools.logging :as log]
            [hiccup.core :as c]
            [hiccup.form :as f]
            [zentaur.hiccup.helpers-view :as hv]))

(defn format-file
  [file uname]
  (let [uurlid   (:uurlid file)
        archived (:archived file)
        url      (str "/files/" uname "/" (:file file))
        up-date  (hv/format-date (:created_at file))]
    [:tr
     [:td [:a {:href url :target "_blank"} (:file file)]]
     [:td [:img {:src "/img/icon_clipboard.png" :alt "In die Zwischenablage kopieren" :title "In die Zwischenablage kopieren" :onclick (str "zentaur.core.copytoclipboard('"url"')")}]]
     [:td up-date]
     [:td  [:a {:href (str "/vclass/files/share/" uurlid)} [:img {:src "/img/icon_share.png" :alt "Share file" :title "Share file"}]]]
     [:td  [:a {:href (str "/vclass/files/archive/" uurlid "/" archived)} [:img {:src "/img/icon_archive.png" :alt "Archive file" :title "Archive file"}]]]
     [:td [:a {:onclick (str "zentaur.core.deletefile(" uurlid ")")} [:img {:src "/img/icon_delete.png" :alt "Delete file" :title "Delete file"}]]]]))

(defn index [files base archived]
  (let [uname           (-> base :identity :uname)
        toggle-archived (if (= archived false) "true" "false")
        title           (if archived "Return to files" "See your archived files")
        icon            (if archived "icon_archived.png" "icon_archived.png")
        formatted-files (for [file files]
                          (format-file file uname))]
    [:div {:id "cont"}
     [:div.right_separator [:a {:href (str "/vclass/files/" toggle-archived)} [:img {:src (str "/img/" icon) :alt title :title title}]]]
     [:div.div-separator
      (f/form-to {:enctype "multipart/form-data" :class "form-inline my-2 my-lg-0" :id "upload-file-form"}
                 [:post "/vclass/files"]
                 (f/hidden-field {:value (:csrf-field base)} "__anti-forgery-token")
                 [:div.div-separator (f/file-upload {:placeholder "Upload file"} "file")]
                 [:div (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :id "button-save" :name "button-save"} "Speichern")])]
     [:table {:class "some-table-class"}
      [:thead
       [:tr
        [:th "Datein"]
        [:th "Copy to clipboard"]
        [:th "Uploaded"]
        [:th "Share"]
        [:th "Sent file to the archive"]
        [:th "Löschen"]]]
      [:tbody formatted-files]]
      (hv/pagination "files")]))

(defn share-file [file uname vclassrooms base]
  (let [uurlid   (:uurlid file)
        archived (:archived file)
        url      (str "/files/" uname "/" (:file file))
        up-date  (hv/format-date (:created_at file))
        fvc      (reduce #(conj %1 [(:name %2) (:id %2)]) [] vclassrooms)
        _ (log/info (str ">>> PARAM  FFFVVVCCCCCCC >>>>> " fvc))
        ]
    [:div
     [:div.div-separator [:a {:href (str "/vclass/files/" archived)} "<< Go back to your files"]]
     [:div (str "<b>Created</b>: " up-date)]
     [:div [:a {:href url :target "_blank"} (:file file)]]
     [:div [:img {:src "/img/icon_clipboard.png" :alt "In die Zwischenablage kopieren" :title "In die Zwischenablage kopieren" :onclick (str "zentaur.core.copytoclipboard('"url"')")}]]
     [:div.div-separator
      (f/form-to {:enctype "multipart/form-data" :class "form-inline my-2 my-lg-0" :id "upload-file-form"}
                 [:post "/vclass/share"]
                 (f/hidden-field {:value (:csrf-field base)} "__anti-forgery-token")
                 ;; [:div.div-separator (f/text-field {:id "email" :placeholder "email"} "email")]
                 [:div.div-separator (f/drop-down {:class "form-class"} "vclassroom_id" fvc)]
       [:div (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :id "button-save" :name "button-save"} "Teilen")])]]))
