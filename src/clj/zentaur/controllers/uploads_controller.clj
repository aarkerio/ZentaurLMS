(ns zentaur.controllers.uploads-controller
  (:require [zentaur.models.uploads :as moduploads]
            [zentaur.controllers.base-controller :as basec]
            [zentaur.hiccup_templating.layout-view :as layout]
            [zentaur.hiccup_templating.admin.uploads-view :as uploads-view]
            [clojure.tools.logging :as log]
            [ring.util.http-response :as response]))

;; GET /admin/uploads
(defn admin-uploads [request]
  (let [base     (basec/set-vars request)
        user-id  (-> request :identity :id)
        _        (log/info (str ">>> user-id >>>>> " user-id))
        files    (moduploads/get-uploads user-id)]
    (layout/application (merge base {:title "Posts" :contents (uploads-view/index files) }))))

;; POST /admin/uploads
(defn upload-file [params identity]
  (let [user-id   (:id identity)]
    ;; (log/info (str ">>> FILE >>>>> " params " root-path >>> " root-path " user-file >>> " (str rand5 "-" (:filename user-file))))
    (moduploads/save-upload! params identity)
    (-> (response/found "/admin/uploads"))))

