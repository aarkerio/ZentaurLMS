(ns zentaur.hiccup.vclassrooms-view
  (:require [clojure.tools.logging :as log]
            [hiccup.core :as c]
            [hiccup.form :as f]
            [hiccup.element :refer [link-to]]
            [markdown.core :as md]
            [zentaur.hiccup.helpers-view :as hv]))

(defn format-row
  [{:keys [name draft historical secret public description uurlid created_at]}]
  (let [formatted-date (hv/format-date created_at)
        draft (if draft "icon_draft.png" "icon_published.png")
        alt   (if draft "Draft" "Published")]
    [:tr
     [:td  [:a {:href (str "/vclass/toggle/" uurlid)} [:img {:src (str "/img/" draft) :alt alt :title alt}]]]
     [:td  [:a {:href (str "/vclass/show/" uurlid)} name]]
     [:td  description]
     [:td  formatted-date]
     [:td  [:a {:onclick (str "zentaur.core.deletevc(" uurlid ")")} [:img {:src "/img/icon_delete.png" :alt "Delete Classroom" :title "Delete Classroom"}]]]]))

(defn- vc-new-form [csrf-field]
  [:div.hidden-div {:id "hidden-form"}
   [:form {:id "submit-vc-form" :action "/vclass/index" :method "post" :class "css-class-form"}
    (f/hidden-field {:value csrf-field} "__anti-forgery-token")
    [:div.div-separator (f/label "name" "Name") [:br] (f/text-field {:maxlength 90 :size 90 :placeholder "Name"} "name")]
    [:div.div-separator (f/label "description" "Description") [:br] (f/text-area {:cols 50 :rows 6 :placeholder "Description"} "description")]
    [:div.div-separator {:id "secret-div"} (f/label "secret" "secret") [:br] (f/text-field {:maxlength 10 :size 10 :placeholder "Secret"} "secret")]
    [:div.div-separator (f/label "open" "Open") [:br] (f/check-box {:title "Open" :value "1" :id "open-vc"} "public")]
    [:div.div-separator (f/label "published" "Published") [:br] (f/check-box {:title "Publish this" :value "1"} "draft")]
    [:div.div-separator (f/label "discution" "Historical") [:br] (f/check-box {:title "Archive this classroom" :value "1"} "historical")]
      (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :id "button-save" :name "button-save"} "Speichern")]])

(defn index [vclassrooms csrf-field]
  (let [form                  (vc-new-form csrf-field)
        formatted-vclassrooms (doall (for [vc vclassrooms]
                                 (format-row vc)))]
    [:div {:id "cont"}
     [:h1 "Classrooms"]
     [:div [:img {:src "/img/icon_add.png" :alt "Quizz test hinzüfugen" :title "Quizz test hinzüfugen" :id "button-show-div"}]]
     [:div form]
     [:div {:id "content"}

     [:table {:class "some-table-class"}
         [:thead
           [:tr
            [:th "Published/Draft"]
            [:th "Name"]
            [:th "Description"]
            [:th "Created"]
            [:th "Löschen"]]]
          [:tbody formatted-vclassrooms]]]
      [:nav {:class "blog-pagination"}
        [:a {:class "btn btn-outline-primary" :href "#"} "Older"]
        [:a {:class "btn btn-outline-secondary disabled" :href "#"} "Newer"]]]))

(defn show [vclassroom base]
  (let [formatted-vc (format-row vclassroom)]
    [:div {:id "cont"}
     [:div {:id "content"} formatted-vc]]))
