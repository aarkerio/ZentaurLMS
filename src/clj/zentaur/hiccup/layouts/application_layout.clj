(ns zentaur.hiccup.layouts.application-layout
  (:require [hiccup.form :as f]
            [hiccup.page :refer [html5 include-css include-js]]
            [zentaur.hiccup.helpers-view :as helpers]))

(defn- ^:private html-head
  "Html helper"
  [title]
  [:head
   [:title (str ":: Zentaur :: Easy Quizz Tests for you " title )]
   [:meta {:http-equiv "Content-Type" :content "text/html;charset=utf-8"}]
   [:link {:rel "shortcut icon" :href "/img/favicon.ico"}]
   (include-css "/css/bootstrap.min.css")
   (include-css "/css/styles.css")])

(defn- ^:private login-form
  "Html helper"
  [csrf-field]
    [:div.login-form
    [:form {:method "post" :action "/login" }
      [:div {:class "login-form-group"}
        (f/hidden-field { :value csrf-field } "__anti-forgery-token")
        (f/email-field {:class "field-form" :maxlength 50 :size 20 :placeholder "Email" :title "Email"} "email")]
      [:div {:class "login-form-group"}
        (f/password-field {:class "field-form" :maxlength 50 :size 10 :placeholder "Password" :title "Password"} "password")]
      [:div {:class "login-form-group"}
        (f/submit-button  {:class "btn btn-default" :name "submit"} "Anmeldung")]]])

(defn application [content]
  (let [nav-links []
        navclass  {:class "nav-item"}
        email     (-> content :identity :email)
        top-links (if-not (nil? email)
                    (conj nav-links [:li navclass [:a {:href "/vclass/tests" :class "nav-link"} "My tests"]]
                                    [:li navclass [:a {:href "/vclass/index" :class "nav-link"} "My Classrooms"]]
                                    [:li navclass [:a {:href "/vclass/files/false" :class "nav-link"} "My Files"]]
                                    [:li navclass [:a {:href "/vclass/search" :class "nav-link"} "Search for questions"]]
                                    [:li navclass (str "Hallo " email "!")]
                                    [:li navclass [:a {:href "/admin/posts/listing/1" :class "nav-link"} "Blogeinträge"]]
                                    [:li navclass [:a {:href "/vclass/uploads" :class "nav-link"} "Test Factory"]]
                                    [:li navclass [:a {:href "/admin/users/true" :class "nav-link"} "Benutzer"]]
                                    [:li navclass [:a {:href "/logout" :class "nav-link"} "Logout"]])
                    nav-links)]

    (html5 (html-head (:title content))
           [:body
            (when-not (-> content :identity :email)
              (login-form (:csrf-field content)))
            (when-let [flash (:flash content)]
              (helpers/display-flash flash))
            [:div {:class "blog-header"}
             [:div {:class "blog-title" :id "blogtitle"} "Zentaur"]
             [:div {:class "blog-description"} "Tausende von Fragen bereit zu verwenden."]
             (helpers/top-links)]
            [:nav {:class "navbar navbar-expand-lg navbar-light bg-light"}   ;; Navigation bar starts
             [:a {:class "navbar-brand" :href "/"} "Home"]
             [:a {:class "navbar-brand" :href "/posts/listing/1"} "Blog"]
             [:button {:class "navbar-toggler navbar-toggler-right" :type "button" :data-toggle "collapse" :data-target "#navbarSupportedContent"
                       :aria-controls "navbarSupportedContent" :aria-expanded "false" :aria-label "Toggle navigation"}
              [:span {:class "navbar-toggler-icon"}]]
             [:div {:class"collapse navbar-collapse" :id "navbarSupportedContent"}
              [:ul {:class"navbar-nav mr-auto"} (for [link top-links] link) ]]]
            [:div {:class "top-banner"}]
            [:div {:class "container"}  (:contents content)]
            [:div {:class "blog-footer" :id "footer"}
             [:a {:href "https://clojure.org" :target "_blank"} [:img {:src "/img/warning_clojure.png" :alt "Clojure" :title "Clojure"}]]
             [:p "Chipotle Software &copy; 2018-2020. MIT License."]
             [:p [:a {:href "#"} "Back to top"]]]
            (include-js "/js/dev.js")
            [:div {:id "root-app"} ""]])))
