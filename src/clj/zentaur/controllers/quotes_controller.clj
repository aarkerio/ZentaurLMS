(ns ^{:doc "Quotes controller"} zentaur.controllers.quotes-controller
  (:require [clojure.tools.logging :as log]
            [ring.util.http-response :as response]
            [zentaur.controllers.base-controller :as basec]
            [zentaur.hiccup.admin.quotes-view :as admin-quotes-view]
            [zentaur.hiccup.layouts.application-layout :as layout]
            [zentaur.models.quotes :as model-quotes]))

;; ADMIN SECTION

(defn admin-listing
  "GET  /admin/quotes"
  [request]
  (let [base (basec/set-vars request)]
    (basec/parser
     (layout/application (merge base {:title "Quotes" :contents (admin-quotes-view/admin-listing)})))))
