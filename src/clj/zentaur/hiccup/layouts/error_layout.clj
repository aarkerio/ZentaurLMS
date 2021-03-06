(ns zentaur.hiccup.layouts.error-layout
  (:require [hiccup.page :refer [html5 include-css include-js]]))

(defn application [{:keys [data]}]
  (let [status   (:status data)
        title    (:title data)
        message  (:message data)]
  (html5 [:head
          [:title title]
          [:meta {:http-equiv "Content-Type" :content "text/html;charset=utf-8"}]
          [:link {:rel "shortcut icon" :href "/img/favicon.ico"}]
          (include-css "/css/styles.css")]
         [:body {:style "padding:50px;margin:50px;"}
          [:div {:class "blog-header"}
           [:div {:class "blog-title" :id "blogtitle"} (str "Zentaur :: Error " title)]
           [:div {:class "container"}  (str "status: "status)]
           [:div {:class "container"}  message]
           [:div {:class "container"}  [:a {:href "/"} "<<<< Go to Home"]]
           [:div {:class "blog-footer" :id "footer"}
            [:p "Chipotle Software &copy; 2018-2020. MIT License."]]]])))
