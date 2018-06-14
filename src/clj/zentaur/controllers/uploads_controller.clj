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
(defn upload-file [params identity]
  (let [user-id   (:id identity)]
    ;; (log/info (str ">>> FILE >>>>> " params " root-path >>> " root-path " user-file >>> " (str rand5 "-" (:filename user-file))))
    (model-upload/save-upload! params identity)
    (-> (response/found "/admin/uploads"))))

