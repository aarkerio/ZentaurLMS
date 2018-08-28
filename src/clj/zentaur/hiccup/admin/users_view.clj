(ns zentaur.hiccup.admin.users-view
  (:require [hiccup.form :as f]
            [hiccup.core :as c]
            [clojure.tools.logging :as log]
            [hiccup.element :only (link-to)]))

(defn formatted-user [{:keys [fname lname uname email active id created_at]}]
    [:tr
     [:td [:a {:href (str "/admin/users/edit/" id)} "Edit"]]
     [:td fname]
     [:td lname]
     [:td uname]
     [:td email]
     [:td created_at]
     [:td [:a {:href (str "/admin/users/active/" id "/" active)}  "Active"]]
     [:td [:a {:href (str "/admin/users/delete/" id)}  "Delete"]]])

(defn roles-options [roles]
  (for [x roles]
    (let [id   (:id x)
          name (:name x)]
      [:option {:value id} name])))

(defn index [base users roles]
  (let [formatted-users (for [user users]
                          (formatted-user user))
       options          (roles-options roles)]
    [:div {:class "content"}
      [:h1 {:id "root-app"} "Users and admins"]
      [:div {:class "fooclass"} [:a {:href "#image"} [:img {:id "icon-add" :src "/img/icon_add.png" :alt "Add" :title "Add"}]]]
      [:div {:class "row"}
        [:div {:class "hidden-div" :id "divhide"}
          (f/form-to [:post "/admin/users" {:class "form-inline my-2 my-lg-0"}]
            (f/hidden-field { :value (:csrf-field base)} "__anti-forgery-token")
            (f/text-field  { :class "form-control mr-sm-2" :placeholder "First name" } :fname)
            (f/text-field  { :class "form-control mr-sm-2" :placeholder "Last name" } :lname)
            (f/text-field  { :class "form-control mr-sm-2" :placeholder "Username" } :uname)
            (f/text-field  { :class "form-control mr-sm-2" :placeholder "email" } :email)
            (f/text-field  { :class "form-control mr-sm-2" :placeholder "prepassword" } :prepassword)
            [:div (c/html [:select {:class "form-control mr-sm-2" :name "role_id"}
                          options])]
            [:div (f/label "Admin" "Admin") (f/check-box {:title "Admin user" :value "1"} "preadmin")]
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
