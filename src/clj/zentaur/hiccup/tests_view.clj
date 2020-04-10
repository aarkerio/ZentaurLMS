(ns zentaur.hiccup.tests-view
  (:require [hiccup.form :as f]
            [hiccup.core :as c]
            [clojure.tools.logging :as log]
            [hiccup.element :refer [link-to]]
            [hiccup.page :refer [include-css include-js]]
            [zentaur.hiccup.helpers-view :as hv]))

(defn formatted-test [{:keys [title created_at tags published id subject uurlid level]}]
  (let [formatted-date (hv/format-date created_at)]
  [:tr
   [:td [:a {:href (str "/vclass/tests/edit/" uurlid)} [:img {:src "/img/icon_edit_test.png" :alt "Bearbeiten"  :title "Bearbeiten"}]]]
   [:td title]
   [:td tags]
   [:td subject]
   [:td level]
   [:td formatted-date]
   [:td [:a {:href (str "/vclass/tests/exportpdf/" uurlid)} [:img {:src "/img/icon_export_pdf.png" :alt "Export PDF" :title "Export PDF"}]]]
   [:td [:a {:href (str "/vclass/tests/exportodt/" uurlid)} [:img {:src "/img/icon_export_odt.png" :alt "Export ODT" :title "Export ODT"}]]]
   [:td [:a {:href (str "/vclass/tests/apply/" uurlid)} [:img {:src "/img/icon_apply.png" :alt "Bewerben Sie sich für die Klasse" :title "Bewerben Sie sich für die Klasse"}]]]
   [:td [:a {:onclick (str "zentaur.core.deletetest('" uurlid "')")} [:img {:src "/img/icon_delete.png" :alt "Delete test" :title "Delete test"}]]]]))

(defn- ^:private test-new-form [csrf-field subjects levels langs]
  [:div.hidden-div {:id "hidden-form"}
   [:form {:id "submit-test-form" :action "/vclass/tests" :method "post" :class "css-class-form"}
    (f/hidden-field {:value csrf-field} "__anti-forgery-token")
    [:label {:for "title"} "Title:"]
    [:div.div-separator (f/text-field {:maxlength 150 :size 90 :placeholder "Title"} "title")]
    [:label {:for "tags"} "Tags:"]
    [:div.div-separator (f/text-field {:maxlength 150 :size 70 :placeholder "Tags"} "tags")]
    [:label {:for "subject_id"} "Subject:"]
    [:div.div-separator
     [:select.form-control.mr-sm-2 {:name "subject_id"}
      (for [subject subjects]
        [:option {:value (:id subject)} (:subject subject)])]]
    [:label {:for "level_id"} "Level:"]
    [:div.div-separator
     [:select.form-control.mr-sm-2 {:name "level_id"}
      (for [level levels]
        [:option {:value (:id level)} (:level level)])]]
    [:label {:for "lang_id"} "Lang:"]
    [:div.div-separator
     [:select.form-control.mr-sm-2 {:name "lang_id"}
      (for [lang langs]
        [:option {:value (:id lang)} (:lang lang)])]]
    (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :id "button-save" :name "button-save"} "Speichern")]])

(defn index [tests base subjects levels langs]
  (let [csrf-field      (:csrf-field base)
        formatted-tests (for [test tests]
                          (formatted-test test))]
    [:div {:id "cont"}
     [:h1 "Dein genialer Quiz Test"]
     [:div [:img {:src "/img/icon_add.png" :alt "Quizz test hinzüfugen" :title "Quizz test hinzüfugen" :id "button-show-div"}]]
     (test-new-form csrf-field subjects levels langs)
     [:div {:id "content"}
       [:table {:class "some-table-class"}
         [:thead
           [:tr
            [:th "Bearbeiten"]
            [:th "Titel"]
            [:th "Stichworte"]
            [:th "Fach"]
            [:th "Stufe"]
            [:th "Erstellt"]
            [:th "Export PDF"]
            [:th "Export ODF"]
            [:th "Apply to Classroom"]
            [:th "Löschen"]]]
          [:tbody formatted-tests]]]
      (hv/pagination "tests")]))

(defn edit [base uurlid]
  (let [csrf-field (:csrf-field base)
        user-id    (-> base :identity :id)]
    [:div
     [:h1 "Bearbeiten Quizz Test"]
     [:div (f/form-to [:id "hidden-form"]
                      (f/hidden-field {:value csrf-field} "__anti-forgery-token")
                      (f/hidden-field {:value uurlid} "uurlid")
                      (f/hidden-field {:value user-id} "user-id"))]
     [:div {:id "test-root-app"}]]))

(defn search [base]
  (let [csrf-field (:csrf-field base)]
    [:div
     [:h1 "Browse and select Questions"]
     [:div {:id "search-root-app"}]]))
