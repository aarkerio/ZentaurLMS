(ns zentaur.controllers.uploads-controller
  (:require [zentaur.models.uploads :as model-upload]
            [zentaur.controllers.base-controller :as basec]
            [zentaur.hiccup_templating.layout-view :as layout]
            [zentaur.hiccup_templating.admin.uploads-view :as admin-uploads-view]
            [clojure.tools.logging :as log]
            [ring.util.http-response :as response]))


;;;;;;;;;;;;;;;;;;;;;;;     ADMIN FUNCTIONS    ;;;;;;;;;;;;;;;;;;;;
;; GET /admin/uploads
(defn admin-uploads [request]
  (let [base       (basec/set-vars request)
        user-id    (-> request :identity :id)
        csrf-field (:csrf-field base)
        files      (model-upload/get-uploads user-id)]
    (layout/application (merge base {:title "Posts" :contents (admin-uploads-view/index files csrf-field) }))))

;; POST /admin/uploads
(defn upload-file [request]
  (let [user-id   (-> request :identity :id)
        params    (-> request :params)]
    (log/info (str ">>> REQUEST >>>>> " request ))
    (model-upload/upload-file params user-id)
    (-> (response/found "/admin/uploads"))))

;; GET /admin/process
(defn process [request]
  (let [base      (basec/set-vars request)
        user-id   (-> request :identity :id)
        params    (-> request :params)
        file      (model-upload/get-upload id)]
    (log/info (str ">>> REQUEST >>>>> " request ))
    (layout/application (merge base {:title "Process" :contents (admin-uploads-view/process files csrf-field) }))))

