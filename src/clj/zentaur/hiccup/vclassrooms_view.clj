(ns zentaur.hiccup.vclassrooms-view
  (:require [clojure.tools.logging :as log]
            [hiccup.core :as c]
            [hiccup.form :as f]
            [hiccup.element :refer [link-to]]
            [markdown.core :as md]
            [zentaur.hiccup.helpers-view :as hv]
            [zentaur.libs.helpers :as h]))

(defn format-row
  [{:keys [name draft historical secret public description uurlid created_at]}]
  (let [formatted-date (hv/format-date created_at)
        icon           (if draft "icon_draft.png" "icon_published.png")
        alt            (if draft "Draft" "Published")]
    [:tr
     [:td  [:a {:href (str "/vclass/toggle/" uurlid "/" draft)} [:img {:src (str "/img/" icon) :alt alt :title alt}]]]
     [:td  [:a {:href (str "/vclass/show/" uurlid)} name]]
     [:td  description]
     [:td  formatted-date]
     [:td  [:a {:onclick (str "zentaur.core.deletevc(" uurlid ")")} [:img {:src "/img/icon_delete.png" :alt "Delete Classroom" :title "Delete Classroom"}]]]]))

(defn foo
  ([s] (foo s 10))
  ([s base] (Integer/parseInt s base)))

(defn- vc-new-form
  ([csrf-field] (vc-new-form csrf-field {}))
  ([csrf-field vclassroom]
   (let [ctx (if (empty? vclassroom) (assoc vclassroom :message "Save"   :action "/vclass/index" :name "" :description "" :secret "" :public false :draft false :historical false)
                                     (assoc vclassroom :message "Update" :action "/vclass/show"))]
     [:div.hidden-div {:id "hidden-form"}
      [:form {:id "submit-vc-form" :action (:action ctx) :method "post" :class "css-class-form"}
       (f/hidden-field {:value csrf-field} "__anti-forgery-token")
       [:div.div-separator (f/label "name" "Name") [:br] (f/text-field {:maxlength 90 :size 90 :placeholder "Name" :value (:name ctx)} "name")]
       [:div.div-separator (f/label "description" "Description") [:br] (f/text-area {:cols 50 :rows 6 :placeholder "Description"} "description" (:description ctx))]
       [:div.div-separator {:id "secret-div"} (f/label "secret" "secret") [:br] (f/text-field {:maxlength 10 :size 10 :placeholder "Secret" :value (:secret ctx)} "secret")]
       [:div.div-separator (f/label "open" "Open") [:br] (f/check-box {:title "Open" :id "open-vc" :checked (:public ctx) } "public")]
       [:div.div-separator (f/label "draft" "Draft") [:br] (f/check-box {:title "Publish this" :checked (:draft ctx) } "draft")]
       [:div.div-separator (f/label "historical" "Historical") [:br] (f/check-box {:title "Archive this classroom" :checked (:historical ctx) } "historical")]
       (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :id "button-save" :name "button-save"} (:message ctx))]])))

(defn index [vclassrooms csrf-field]
  (let [form                  (vc-new-form csrf-field)
        formatted-vclassrooms (doall (for [vc vclassrooms]
                                 (format-row vc)))]
    [:div {:id "cont"}
     [:h1 "Classrooms"]
     [:div [:img {:src "/img/icon_add.png" :alt "New Classroom" :title "New Classroom" :id "button-show-div"}]]
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

(defn show [vclassroom csrf-field]
  (let [form (vc-new-form csrf-field vclassroom)
        {:keys [id name draft historical secret public uurlid description created_at]} vclassroom
        status (if draft "Non published" "Published")]
    [:div {:id "cont"}
     [:div {:id "content"}
      [:div [:h1 name]]
      [:div [:img {:src "/img/icon_edit.png" :alt "Edit Classroom" :title "Edit Classroom" :id "button-show-div"}]]
      [:div  (str "<b>Status:</b> " status)]
      [:div  (str "<b>Public:</b> " public)]
      [:div  (str "<b>Secret:</b> " secret)]
      [:div form]
      [:div (hv/format-date created_at)]
      [:div  description]
      [:div  "Students:"]]]))
