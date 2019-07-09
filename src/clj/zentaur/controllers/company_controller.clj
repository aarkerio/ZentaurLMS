(ns ^{:doc "Static pages"} zentaur.controllers.company-controller
  (:require [zentaur.controllers.base-controller :as basec]
            [clojure.tools.logging :as log]
            [zentaur.hiccup.layout-view :as layout]
            [zentaur.hiccup.page-view :as page-view]
            [ring.util.http-response :as response]))

(defn get-admin [request]
  (let [base     (basec/set-vars request)]
    (layout/application "admin/index.html")))

;; Polimorphysm for static pages
(defmulti page-behavior (fn [page] (:page page)))

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
