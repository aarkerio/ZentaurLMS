(ns zentaur.hiccup.helpers-view
  (:require [clojure.tools.logging :as log]
            [java-time :as jt]
            [hiccup.form :as f]
            [hiccup.element :refer [link-to]]))

(defn format-date
  "Format a Java instant"
  [date]
  (let [formatter         (jt/format "dd-MM-yyyy HH:mm")
        instant-with-zone (.atZone date (jt/zone-id))]
    (jt/format formatter instant-with-zone)))

(defn index []
  [:div {:id "content"}
   [:h1 {:class "text-success"} "Hello Hiccup"]])

(defn nav-links []
  [[:li {:class "nav-item"} [:a {:class "nav-link" :href "/page/news"} "Nachrichten"]]
   [:li {:class "nav-item"} [:a {:class "nav-link" :href "/page/vision"} "Unsere Vision"]]
   [:li {:class "nav-item"} [:a {:class "nav-link" :href "/page/join"} "Begleiten"]]
   [:li {:class "nav-item"} [:a {:class "nav-link" :href "/page/about"} "Über uns" ]]])

(defn display-flash [msg]
  [:div {:class "alert notice alert-success" :id "flash-msg"}
    [:a.close {:data-dismiss "alert"} "x"]
    [:div#flash_notice msg]])

(defn labeled-radio [label]
  [:label (f/radio-button {:ng-model "user.gender"} "user.gender" false f/label)
   (str label "    ")])

(defn subscribe []
  [:div {:class "well"}
   [:form {:novalidate "" :role "form"}
    [:div {:class "form-group"}
     (f/label {:class "control-label"} "email" "Email")
     (f/email-field {:class "form-control" :placeholder "Email" :ng-model "user.email"} "user.email")]
    [:div {:class "form-group"}
     (f/label {:class "control-label"} "password" "Password")
     (f/password-field {:class "form-control" :placeholder "Password" :ng-model "user.password"} "user.password")]
    [:div {:class "form-group"}
     (f/label {:class "control-label"} "gender" "Gender")
     (reduce conj [:div {:class "btn-group"}] (map labeled-radio ["male" "female" "other"]))]
    [:div {:class "form-group"}
     [:label
      (f/check-box {:ng-model "user.remember"} "user.remember-me") " Remember me"]]]
   [:pre "form = {{ user | json }}"]])

(defn pagination []
  [:div {:ng-controller "PaginationCtrl" :class "well"}
   [:pre "[Browser] Current page: {{currentPage}}. [Server] {{partial}}"]
   [:pagination {:total-items "totalItems" :page "currentPage" :on-select-page "displayPartial(page)"}]])

(defn page [id]
  (str "Got id: " id))

(defn not-found []
  [:div {:class "well"}
   [:h1 {:class "info-worning"} "Page Not Found"]
   [:p "There's no requested page. "]
   (link-to {:class "btn btn-primary"} "/" "Take me to Home")])

(defn http-status [data]
  [:div {:class "container-fluid"}
    [:div {:class "row-fluid"}
      [:div {:class "col-lg-12"}
        [:div {:class "centering text-center"}
          [:div {:class "text-center"}
            [:h1 [:span {:class "text-danger"} (str "Error:" (:status data))]]
              [:hr]
              [:h2 {:class "without-margin" } (:title data)]
              [:h4 {:class "text-danger" }   (:message data)]]]]]])
