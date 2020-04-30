(ns zentaur.hiccup.helpers-view
  (:require [clojure.tools.logging :as log]
            [clojure.string :as cs]
            [java-time :as jt]
            [hiccup.form :as f]
            [hiccup.element :refer [link-to]])
  (:import [java.net InetAddress]))

(def back-button [:a {:onclick "javascript:history.go(-1);"} [:img {:src "/img/icon_back.png":title "<< Go back" :alt "<< Go back"}]])

(defn format-date
  "Format a Java instant"
  [date]
  (let [formatter (java.text.SimpleDateFormat. "MM/dd/yyyy")]
    (.format formatter date)))

(defn index []
  [:div {:id "content"}
   [:h1 {:class "text-success"} "Hello Hiccup"]])

(defn top-links []
  [:div {:class "div_inline_list"}
   [:ul {:class "inline_list"}
    [:li [:a {:href "/page/news"} "Nachrichten"]]
    [:li.separator "|" ]
    [:li [:a {:href "/page/vision"} "Unsere Vision"]]
    [:li.separator "|" ]
    [:li [:a {:href "/page/join"} "Begleiten"]]
    [:li.separator "|" ]
    [:li [:a {:href "/page/about"} "Über uns" ]]]])

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

(defn pagination [model-data]
  [:div {:class "pagination"}
   [:nav {:class "blog-pagination"}
    [:a {:class "btn btn-outline-primary-green" :href "#"} "Older"]
    [:a {:class "btn btn-outline-primary-green disabled" :href "#"} "Newer"]]])

(defn not-found []
  [:div {:class "well"}
   [:h1 {:class "info-worning"} "Page Not Found"]
   [:p "There's no requested page. "]
   (link-to {:class "btn btn-primary"} "/" "Take me to Home")])

;; (= (paginate (range 101) 10 5)
;;    {:prev-seq (4 3 2 1), :next-seq (6 7 8 9 10 11), :pages 11, :page 5, :next 6, :prev 4}

(defn get-localhost
  "Returns the hostname of the local host."
  ^InetAddress []
  (let [lh (InetAddress/getLocalHost)]
    ;; (.getHostName lh)
    (.getCanonicalHostName lh)))

