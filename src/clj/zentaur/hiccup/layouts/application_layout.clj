(ns zentaur.hiccup.layouts.application-layout
  (:require [hiccup.form :as f]
            [hiccup.page :refer [html5 include-css include-js]]
            [zentaur.hiccup.helpers-view :as helpers]))

(defn- ^{:private true} html-head
  "Html helper"
  [title]
  [:head
   [:title (str ":: Zentaur :: Easy Quizz Tests for you " title )]
   [:meta {:http-equiv "Content-Type" :content "text/html;charset=utf-8"}]
   [:link {:rel "shortcut icon" :href "/img/favicon.ico"}]
   (include-css "/css/bootstrap.min.css")
   (include-css "/css/styles.css")])

(defn- ^{:private true} login-form
  "Html helper"
  [csrf-field]
  [:div {:class "form-box"}
    [:div {:class "login-form"}
    [:form {:method "post" :action "/login" }
      [:div {:class "login-form-group"}
        (f/hidden-field { :value csrf-field } "__anti-forgery-token")
        (f/email-field {:class "field-form" :maxlength 50 :size 20 :placeholder "Email"} "email")]
      [:div {:class "login-form-group"}
        (f/password-field {:class "field-form" :maxlength 50 :size 10 :placeholder "Password"} "password")]
      [:div {:class "login-form-group"}
        (f/submit-button  {:class "btn btn-sm btn-outline-success" :name "submit"} "Anmeldung")]]]])

(defn application [content]
  (let [nav-links (helpers/nav-links)
        email     (-> content :identity :email)
        top-links (if-not (nil? email)
                    (conj nav-links [:li {:class "nav-item"} [:a {:href "/vclass/tests" :class "nav-link"} "Quiztest"]]
                                    [:li {:class "nav-item"} [:a {:href "/vclass/index" :class "nav-link"} "vClassrooms"]]
                                    [:li {:class "nav-item"} [:a {:href "/vclass/uploads" :class "nav-link"} "Dateien"]]
                                    [:li {:class "nav-item"} (str "Hallo " email "!")]
                                    [:li {:class "nav-item"} [:a {:href "/admin/posts" :class "nav-link"} "Blogeinträge"]]
                                    [:li {:class "nav-item"} [:a {:href "/admin/users" :class "nav-link"} "Benutzer"]]
                                    [:li {:class "nav-item"} [:a {:href "/logout" :class "nav-link"} "Logout"]])
                    nav-links)]

    (html5 (html-head (:title content))
           [:body
            (when-not (-> content :identity :email)
              (login-form (:csrf-field content)))
            (when-let [flash (:flash content)]
              (helpers/display-flash flash))
            [:div {:class "blog-header"}
             [:div {:class "blog-title" :id "blogtitle"} "Zentaur"]
             [:div {:class "blog-description"} "Tausende von Fragen bereit zu verwenden."]]
            [:nav {:class "navbar navbar-expand-lg navbar-light bg-light"}   ;; Navigation bar starts
             [:a {:class "navbar-brand" :href "/"} "Home"]
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
            (include-js "/cljs-out/dev-main.js")
            [:div {:id "root-app"} ""]])))
