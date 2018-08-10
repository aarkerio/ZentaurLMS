(ns zentaur.hiccup_templating.admin.users-view
  (:require [hiccup.form :as f]
            [hiccup.core :as c]
            [clojure.tools.logging :as log]
            [hiccup.element :only (link-to)]))

(defn formatted-user [user]
  (let [fname      (:fname user)
        lname      (:lname user)
        uname      (:uname user)
        email      (:email user)
        active     (:active user)
        id         (:id user)
        created_at (:created_at user)]
    [:tr
     [:td [:a {:href (str "/admin/users/edit/" id)} "Edit"]]
     [:td fname]
     [:td lname]
     [:td uname]
     [:td email]
     [:td created_at]
     [:td [:a {:href (str "/admin/users/active/" id "/" active)}  "Active"]]
     [:td [:a {:href (str "/admin/users/delete/" id)}  "Delete"]]]))

(defn index [base users]
  (let [formatted-users (for [user users]
                          (formatted-user user))]
  [:h1 {:id "root-app"} "Users and admins"]
  [:div "Identity :  {{ (:identity content) }}"
    [:div {:class "row"}
      [:div {:class "span12" :id ""}
         (f/form-to [:post "/admin/users/create" {:class "form-inline my-2 my-lg-0"}]
            (f/hidden-field { :value (:csrf-field base)} "__anti-forgery-token")
            (f/text-field { :class "form-control mr-sm-2" :placeholder "First name" } :fname)
            (f/text-field { :class "form-control mr-sm-2" :placeholder "Last name" } :lname)
            (f/text-field { :class "form-control mr-sm-2" :placeholder "Username" } :uname)
            (f/text-field  { :class "form-control mr-sm-2" :placeholder "email" } :email)
            (f/text-field  { :class "form-control mr-sm-2" :placeholder "email" } :prepassword)
            (f/text-field  { :class "form-control mr-sm-2" :placeholder "email" } :prepassword2)
            (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :name "submit"} "Einrechen"))]]
      [:table {:class "some-classs"}
        [:thead
          [:tr
            [:th "Edit"]
            [:th "Name"]
            [:th "Last"]
            [:th "User name"]
            [:th "email"]
            [:th "Created"]
            [:th "Active"]
            [:th "Delete"]]]
        [:tbody formatted-users]]]))

(defn login [base]
  [:div
     (f/form-to [:post "/login" {:class "form-inline my-2 my-lg-0"}]
        (f/hidden-field { :value (:csrf-field base)} "__anti-forgery-token")
        (f/password-field { :class "form-control mr-sm-2" :placeholder "password" } :password )
        (f/text-field  { :class "form-control mr-sm-2" :placeholder "email" } :email)
        (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :name "submit"} "Anmeldung"))])
