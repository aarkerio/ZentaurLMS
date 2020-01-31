(ns ^{:doc "Static pages"} zentaur.controllers.company-controller
  (:require [clojure.tools.logging :as log]
            [ring.util.http-response :as response]
            [zentaur.controllers.base-controller :as basec]
            [zentaur.hiccup.layouts.application-layout :as layout]
            [zentaur.hiccup.page-view :as page-view]))

(defn get-admin [request]
  (let [base     (basec/set-vars request)]
    (layout/application "admin/index.html")))

;; Polimorphysm for static pages
(defmulti page-behavior :page)

(defmethod page-behavior "about"
  [page]
  {:title "About us" :contents (page-view/about)})

(defmethod page-behavior "news"
  [page]
  {:title "News" :contents (page-view/news)})

(defmethod page-behavior "vision"
  [page]
  {:title "Vision" :contents (page-view/vision)})

(defmethod page-behavior "join"
  [page]
  {:title "Join us!" :contents (page-view/join)})

(defn load-page [request]
  (let [base     (basec/set-vars request)
        page     (-> request :path-params :page)
        contents (page-behavior {:page page})]
    (basec/parser
     (layout/application
      (merge base contents)))))

