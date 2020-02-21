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

(defmulti paginate
  "Paginate the incoming collection/length"
  (fn [coll? _ _] (sequential? coll?)))

(defmethod paginate true [coll count-per-page page]
  (paginate (count coll) count-per-page page))

(defmethod paginate :default [length count-per-page page]
  (let [pages (+ (int (/ length count-per-page))
                 (if (zero? (mod length count-per-page))
                   0
                   1))
        page (if (and (string? page)(not= page ""))
               (Integer/parseInt page))
        page (cond
               (nil? page) 1 (or (neg? page) (zero? page)) 1
               (> page pages) pages
               :else page)
        next (+ page 1)
        prev (- page 1)]
    (let [prev (if (or (neg? prev) (zero? prev)) nil prev)]
      {:pages pages
       :page page
       :next-seq (range (inc page) (inc pages))
       :prev-seq (reverse (range 1 (if (nil? prev) 1
                                       (inc prev))))
       :next (if (> next pages) nil next)
       :prev prev})))

;; USE :

;; (= (paginate (range 101) 10 5)
;;    {:prev-seq (4 3 2 1), :next-seq (6 7 8 9 10 11), :pages 11, :page 5, :next 6, :prev 4}
