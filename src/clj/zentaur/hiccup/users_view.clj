(ns zentaur.hiccup.users-view
  (:require [hiccup.form :as f]
            [hiccup.core :as c]
            [clojure.tools.logging :as log]))

(defn login [base]
  [:div
   (f/form-to [:post "/login" {:class "form-inline my-2 my-lg-0"}]
              (f/hidden-field { :value (:csrf-field base)} "__anti-forgery-token")
              (f/text-field  { :class "form-control mr-sm-2" :placeholder "email" } :email)
              (f/password-field { :class "form-control mr-sm-2" :placeholder "password" } :password )
              (f/submit-button {:class "btn btn-outline-success my-2 my-sm-0" :name "submit"} "Anmeldung"))])
