(ns zentaur.hiccup.admin.tests-view
  (:require [hiccup.form :as f]
            [hiccup.core :as c]
            [clojure.tools.logging :as log]
            [hiccup.element :refer [link-to]]
            [hiccup.page :refer [include-css include-js]]
            [zentaur.hiccup.helpers-view :as hv]))

(defn formatted-test [{:keys [title created_at tags published id]}]
  (let [formatted-date (hv/format-date created_at true)]
  [:tr
   [:td [:a {:class "btn btn-outline-primary-green" :href (str "/admin/tests/edit/" id)}  "Edit"]]
   [:td title]
   [:td tags]
   [:td formatted-date]
   [:td [:a {:class "btn btn-outline-primary-green" :href  (str "/admin/tests/exporttest/" id)} "Export"]]
   [:td [:button {:class "btn btn-outline-primary-green" :onClick (str "zentaur.core.deletetest("id")")} "Löschen"]]]))

(defn- form-new [csrf-field]
  [:div.hidden-div {:id "hidden-form"}
    [:form {:id "submit-test-form" :action "/admin/tests" :method "post" :class "css-class-form"}
      (f/hidden-field {:value csrf-field} "__anti-forgery-token")
      [:div (f/text-field {:maxlength 150 :size 90 :placeholder "Title"} "title")]
      [:div (f/text-field {:maxlength 150 :size 70 :placeholder "Tags"} "tags")]
      (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :id "button-save" :name "button-save"} "Speichern")]])

(defn index [tests base]
  (let [csrf-field      (:csrf-field base)
        formatted-tests (for [test tests]
                          (formatted-test test))]
    [:div {:id "cont"}
     [:h1 "Dein genialer Quiz Test"]
     [:div [:img {:src "/img/icon_add.png" :alt "Quizz test hinzüfugen" :title "Quizz test hinzüfugen" :id "button-show-div"}]]
     (form-new csrf-field)
     [:div {:id "content"}
       [:table {:class "some-table-class"}
         [:thead
           [:tr
            [:th "Bearbeiten"]
            [:th "Titel"]
            [:th "Stichworte"]
            [:th "Erstellt"]
            [:th "Export"]
            [:th "Löschen"]]]
          [:tbody formatted-tests]]]
      [:nav {:class "blog-pagination"}
        [:a {:class "btn btn-outline-primary-green" :href "#"} "Older"]
        [:a {:class "btn btn-outline-primary-green disabled" :href "#"} "Newer"]]]))

(defn edit [base test-id]
  [:div
   [:h1 "Bearbeiten Quizz Test"]
   [:div (f/form-to [:id "hidden-form"]
                    (f/hidden-field {:value (:csrf-field base)} "__anti-forgery-token")
                    (f/hidden-field {:value test-id} "test-id"))]
   [:div {:id "test-root-app"}]])
