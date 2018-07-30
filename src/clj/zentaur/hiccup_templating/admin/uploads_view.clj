(ns zentaur.hiccup_templating.admin.uploads-view
  (:require [hiccup.form :as f]
            [hiccup.core :as c]
            [clojure.tools.logging :as log]
            [hiccup.element :only (link-to)]))

(defn extract? [id content]
  (if (clojure.string/blank? content)
    (c/html [:a {:href (str "/admin/uploads/extract/" id)} "Extract"])
    (str "Done")))

(defn formatted-file [file]
  (let [filename   (:filename file)
        created_at (:created_at file)
        tags       (:tags file)
        content    (:content file)
        done       (:done file)
        id         (:id file)]
  [:tr
    [:td filename]
    [:td tags]
    [:td done]
    [:td created_at]
    [:td [:a {:href (str "/admin/uploads/download/" id)} "Download"]]
    [:td (extract? id content)]
    [:td [:a {:href (str "/admin/uploads/process/" id)}  "Process"]]
    [:td [:a {:href (str "/admin/uploads/archive/" id)}  "Archive"]]]))

(defn index [files csrf-field]
  (let [formatted-files (doall (for [file files]
                                 (formatted-file file)))]
    [:div nil
      [:h1 nil "Tests"]
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
          [:th "Done"]
          [:th "Uploaded Date"]
          [:th "Download"]
          [:th "Extract"]
          [:th "Process"]
          [:th "Archive"]]]
      [:tbody formatted-files] ]]))

(defn process [file csrf-field]
    [:div nil
      [:h1 nil "Import"]
      [:div {:class "row"}
       "asddasdasdasd"]
      [:div {:class "someclass"}
        (f/submit-button {:class "btn" :id "save-button"}     "Save")
        (f/submit-button {:class "btn" :id "test-button"}     "Test")
        (f/submit-button {:class "btn" :id "multiple-button"} "Multiple Option")
        (f/submit-button {:class "btn" :id "download-button"} "Download")
        (f/submit-button {:class "btn" :id "export-button"}   "Export")]
      [:div {:class "someclass"}
       (f/text-area {:class "btn" :rows "20" :cols "100" :id "export-button"} "json-field" file)]
      [:div {:class "someclass"}
         file]
        ])

