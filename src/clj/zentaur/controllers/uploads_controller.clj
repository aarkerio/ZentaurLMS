(ns blog.controllers.uploads-controller
  (:require [blog.models.uploads :as moduploads]
            [blog.controllers.base-controller :as basec]
            [blog.hiccup_templating.layout-view :as layout]
            [blog.hiccup_templating.admin.uploads-view :as uploads-view]
            [clojure.tools.logging :as log]
            [ring.util.http-response :as response]))

(defn admin-uploads [request]
  (let [base     (basec/set-vars request)
        user-id  (-> request :identity :id)
        _        (log/info (str ">>> user-id >>>>> " user-id))
        files    (moduploads/get-uploads user-id)]
    (layout/application (merge base {:title "Posts" :contents (uploads-view/index files) }))))

(defn upload-file [params identity]
  (let [user-id   (:id identity)]
    ;; (log/info (str ">>> FILE >>>>> " params " root-path >>> " root-path " user-file >>> " (str rand5 "-" (:filename user-file))))
    (moduploads/save-upload! params identity)
    (-> (response/found "/admin/images"))))

