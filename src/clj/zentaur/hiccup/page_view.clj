(ns zentaur.hiccup.page-view
  (:require [hiccup.form :as f]
            [hiccup.element :refer [link-to]]))

(defn vision []
  [:div {:id "content"}
   [:h1 {:class "text-success"} "Vision"]])

(defn about []
  [:div {:id "content"}
   [:h1 {:class "text-success"} "About"]])

(defn news []
  [:div {:id "content"}
   [:h1 {:class "text-success"} "News"]])

(defn join []
  [:div {:id "content"}
   [:h1 {:class "text-success"} "Join us!"]])

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

