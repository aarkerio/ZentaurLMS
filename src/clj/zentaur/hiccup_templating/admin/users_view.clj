(ns blog.hiccup_templating.admin.users-view
  (:require [hiccup.form :as f]
            [hiccup.core :as c]
            [clojure.tools.logging :as log]
            [hiccup.element :only (link-to)]))

(defn index [content]
  [:div {:id "root-app"} "This is the Original text PAGE PROTECTED ONLY users and admin"]
  [:div "Identity :  {{ (:identity content) }}"
    [:img {:src "/img/warning_clojure.png" :alt "Lisp" :title "Lisp"}]
    [:div {:class "row"}
      [:div {:class "span12"}
         (f/form-to [:post "/login" {:class "form-inline my-2 my-lg-0"}]
                 (f/hidden-field { :value (:csrf-field content)} "__anti-forgery-token")
                 (f/password-field { :class "form-control mr-sm-2" :placeholder "password" } :password )
                 (f/text-field  { :class "form-control mr-sm-2" :placeholder "email" } :email)
                 (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :name "submit"} "Anmeldung"))]]])

(defn login [base]
  [:div
     (f/form-to [:post "/login" {:class "form-inline my-2 my-lg-0"}]
        (f/hidden-field { :value (:csrf-field base)} "__anti-forgery-token")
        (f/password-field { :class "form-control mr-sm-2" :placeholder "password" } :password )
        (f/text-field  { :class "form-control mr-sm-2" :placeholder "email" } :email)
        (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :name "submit"} "Anmeldung"))])
