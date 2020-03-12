(ns zentaur.hiccup.tests-view
  (:require [hiccup.form :as f]
            [hiccup.core :as c]
            [clojure.tools.logging :as log]
            [hiccup.element :refer [link-to]]
            [hiccup.page :refer [include-css include-js]]
            [zentaur.hiccup.helpers-view :as hv]))

(defn formatted-test [{:keys [title created_at tags published id subject uurlid]}]
  (let [formatted-date (hv/format-date created_at)]
  [:tr
   [:td [:a {:href (str "/vclass/tests/edit/" uurlid)} [:img {:src "/img/icon_edit_test.png" :alt "Bearbeiten"  :title "Bearbeiten"}]]]
   [:td title]
   [:td tags]
   [:td subject]
   [:td formatted-date]
   [:td [:a {:href (str "/vclass/tests/exportpdf/" uurlid)} [:img {:src "/img/icon_export_pdf.png" :alt "Export PDF" :title "Export PDF"}]]]
   [:td [:a {:href (str "/vclass/tests/exportodt/" uurlid)} [:img {:src "/img/icon_export_odt.png" :alt "Export ODT" :title "Export ODT"}]]]
   [:td [:a {:onclick (str "zentaur.core.deletetest('" uurlid "')")} [:img {:src "/img/icon_delete.png" :alt "Delete test" :title "Delete test"}]]]]))

(defn- test-new-form [subjects csrf-field]
  [:div.hidden-div {:id "hidden-form"}
   [:form {:id "submit-test-form" :action "/vclass/tests" :method "post" :class "css-class-form"}
    (f/hidden-field {:value csrf-field} "__anti-forgery-token")
    [:div.div-separator (f/text-field {:maxlength 150 :size 90 :placeholder "Title"} "title")]
    [:div.div-separator (f/text-field {:maxlength 150 :size 70 :placeholder "Tags"} "tags")]
    [:div.div-separator
     [:select.form-control.mr-sm-2 {:name "subject_id" :value 1}
      (for [subject subjects]
        [:option {:value (:id subject)} (:subject subject)])
      ]]
      (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :id "button-save" :name "button-save"} "Speichern")]])

(defn index [tests base subjects]
  (let [csrf-field      (:csrf-field base)
        formatted-tests (for [test tests]
                          (formatted-test test))]
    [:div {:id "cont"}
     [:h1 "Dein genialer Quiz Test"]
     [:div [:img {:src "/img/icon_add.png" :alt "Quizz test hinzüfugen" :title "Quizz test hinzüfugen" :id "button-show-div"}]]
     (test-new-form subjects csrf-field)
     [:div {:id "content"}
       [:table {:class "some-table-class"}
         [:thead
           [:tr
            [:th "Bearbeiten"]
            [:th "Titel"]
            [:th "Stichworte"]
            [:th "Fach"]
            [:th "Erstellt"]
            [:th "Export PDF"]
            [:th "Export ODF"]
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
