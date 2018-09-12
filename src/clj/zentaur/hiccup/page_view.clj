(ns zentaur.hiccup.page-view
  (:require [hiccup.form :as f]
            [hiccup.element :refer [link-to]]))

(defn vision []
  [:div {:id "content"}
   [:h1 {:class "text-success"} "Vision"]
   [:p {:class "text-paragraph"} "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."]
   [:p {:class "text-paragraph"} [:img {:src "/img/pfrr.jpeg" :title "PPFR" :alt "PPFR"}]]])

(defn about []
  [:div {:id "content"}
   [:h1 {:class "text-success"} "About"]
   [:p {:class "text-paragraph"} "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."]
   [:p {:class "text-paragraph"} [:img {:src "/img/pfrr.jpeg" :title "PPFR" :alt "PPFR"}]]])

(defn news []
  [:div {:id "content"}
   [:h1 {:class "text-success"} "News"]
   [:p {:class "text-paragraph"} "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."]
   [:p {:class "text-paragraph"} [:img {:src "/img/pfrr.jpeg" :title "PPFR" :alt "PPFR"}]]])

(defn join []
  [:div {:id "content"}
   [:h1 {:class "text-success"} "Join us!"]
   [:p {:class "text-paragraph"} "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."]
   [:p {:class "text-paragraph"} [:img {:src "/img/pfrr.jpeg" :title "PPFR" :alt "PPFR"}]]])

(defn nav-links []
  [[:li {:class "nav-item"} [:a {:class "nav-link" :href "/page/news"} "Nachrichten"]]
   [:li {:class "nav-item"} [:a {:class "nav-link" :href "/page/vision"} "Unsere Vision"]]
   [:li {:class "nav-item"} [:a {:class "nav-link" :href "/page/quienessomos"} "Wer sind wir?"]]
   [:li {:class "nav-item"} [:a {:class "nav-link" :href "/page/unete"} "Begleiten"]]
   [:li {:class "nav-item"} [:a {:class "nav-link" :href "/page/about"} "Über uns" ]]])

(defn success-flash [msg]
  [:div.alert.notice.alert-success
    [:a.close {:data-dismiss "alert"} "x"]
    [:div#flash_notice msg]])

(defn error-flash [msg]
  [:div.alert.notice.alert-error
    [:a.close {:data-dismiss "alert"} "x"]
    [:div#flash_notice msg]])

(defn hello []
  [:div {:class "well"}
   [:h1 {:class "text-info"} "Hello Hiccup and AngularJS"]
   [:div {:class "row"}
    [:div {:class "col-lg-2"}
     (f/label "name" "Name:")]
    [:div {:class "col-lg-4"}
     (f/text-field {:class "form-control" :ng-model "yourName" :placeholder "Enter a name here"} "your-name")]]
   [:hr]
   [:h1 {:class "text-success"} "Hello {{yourName}}!"]])

