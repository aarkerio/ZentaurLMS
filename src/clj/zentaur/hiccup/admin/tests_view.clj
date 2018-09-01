(ns zentaur.hiccup.admin.tests-view
  (:require [hiccup.form :as f]
            [hiccup.core :as c]
            [clojure.tools.logging :as log]
            [hiccup.element :only (link-to)]
            [hiccup.page :refer [include-css include-js]]))

(defn formatted-test [{:keys [title created_at tags published id]}]
  [:tr
    [:td [:a {:href (str "/admin/tests/edit/" id)}  "Edit"]]
    [:td title]
    [:td tags]
    [:td created_at]
    [:td [:a {:href (str "/admin/tests/delete/" id)}  "Delete"]]])

(defn index [tests]
  (let [formatted-tests (for [test tests]
                          (formatted-test test))]
    [:div {:id "cont"}
      [:div {:id "button-neuer"} [:a {:class "btn btn-outline-primary" :href "/admin/tests/new"} "Neuer Quiztest"]]
      [:div {:id "content"}
        [:table {:class "some-table-class"}
          [:thead
            [:tr
              [:th "Edit"]
              [:th "Title"]
              [:th "Tags"]
              [:th "Created"]
              [:th "Delete"]]]
          [:tbody formatted-tests]]]
      [:nav {:class "blog-pagination"}
        [:a {:class "btn btn-outline-primary" :href "#"} "Older"]
        [:a {:class "btn btn-outline-secondary disabled" :href "#"} "Newer"]]]))

(defn new [base]
  [:div
    [:div {:id "cont"}
    (f/form-to [:test "/admin/tests"]
      (f/hidden-field { :value (:csrf-field base)} "__anti-forgery-token")
        [:div (f/text-field {:maxlength 150 :size 90 :placeholder "Title"} "title")]
        [:div (f/text-field {:maxlength 150 :size 70 :placeholder "tags"} "tags")]
        [:div [:img {:src "/img/icon_add.png" :alt "Add question" :title "Add question"}]]
        [:div (f/label "active" "Active") (f/check-box {:title "Active comments" :value "1"} "active")]
        (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :id "button-save" :name "button-save"} "Speichern")) ]
   [:div {:id "app"}]])

