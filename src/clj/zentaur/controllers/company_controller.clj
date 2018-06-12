(ns blog.controllers.company-controller
  (:require [blog.controllers.base-controller :as basec]
            [clojure.tools.logging :as log]
            [blog.hiccup_templating.layout-view :as layout]
            [ring.util.http-response :as response]))

;; Controller for static pages
(defn get-admin [request]
  (let [base     (basec/set-vars request)]
    (layout/application "admin/index.html")))

(defn about-page [request]
  (let [base     (basec/set-vars request)]
     (layout/application {:contents base})))
