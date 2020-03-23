(ns zentaur.routes.home
  (:require [zentaur.controllers.company-controller :as cont-company]
            [zentaur.controllers.export-controller  :as cont-export]
            [zentaur.controllers.files-controller   :as cont-files]
            [zentaur.controllers.posts-controller   :as cont-posts]
            [zentaur.controllers.tests-controller   :as cont-tests]
            [zentaur.controllers.uploads-controller :as cont-uploads]
            [zentaur.controllers.users-controller   :as cont-users]
            [zentaur.controllers.vclass-controller  :as cont-vclass]
            [zentaur.middleware :as middleware]))

(def site-routes
  [["/"                  {:get  cont-posts/get-posts}]
   ["/posts/savecomment" {:post cont-posts/save-comment}]
   ["/posts/view/:id"    {:get  cont-posts/single-post}]
   ["/uploads/token"     {:post cont-uploads/token}]
   ["/page/:page"        {:get  cont-company/load-page}]
   ["/login"             {:get  cont-users/login-page :post cont-users/post-login}]
   ["/notauthorized"     {:get  cont-posts/get-posts}]
   ["/logout"            {:get  cont-users/clear-session!}]])

(def vclass-routes
  ["/vclass"
   ["/"                        {:get  cont-posts/get-posts}]
   ["/index"                   {:get  cont-vclass/index :post cont-vclass/create-vclass}]
   ["/show/:uurlid"            {:get  cont-vclass/show}]
   ["/show"                    {:post cont-vclass/update-vc}]
   ["/toggle/:uurlid/:draft"   {:get  cont-vclass/toggle-published}]
   ["/delete/:uurlid"          {:delete cont-vclass/delete-vclass}]
   ["/tests"                   {:get  cont-tests/index :post cont-tests/create-test}]
   ["/tests/edit/:uurlid"      {:get  cont-tests/edit}]
   ["/tests/exportpdf/:uurlid" {:get  cont-export/export-test-pdf}]
   ["/tests/exportodt/:uurlid" {:get  cont-export/export-test-odt}]
   ["/tests/delete"            {:delete cont-tests/delete-test}]
   ["/files/:archived"         {:get  cont-files/index}]
   ["/files/popup/:archived"   {:get  cont-files/popup}]
   ["/files/archive/:uurlid/:archived" {:get  cont-files/archive}]
   ["/files/share/:uurlid"     {:get  cont-files/share}]
   ["/files"                   {:post cont-files/upload}]
   ["/files/download/:uurlid"  {:post cont-files/download}]
   ["/uploads"                 {:get  cont-uploads/index :post cont-uploads/upload-file}]
   ["/uploads/process/:id"     {:get  cont-uploads/process}]
   ["/uploads/export"          {:post cont-uploads/export-test}]
   ["/uploads/save"            {:post cont-uploads/save-body}]
   ["/uploads/archive/:id"     {:get  cont-uploads/archive}]
   ["/uploads/download/:id"    {:get  cont-uploads/download}]
   ["/uploads/extract/:id"     {:get  cont-uploads/extract}]])

(def admin-routes
  ["/admin"
   ["/posts"                   {:get cont-posts/admin-posts :post cont-posts/save-post}]
   ["/posts/delete/:id"        {:delete cont-posts/delete-post}]
   ["/posts/edit/:id"          {:get cont-posts/show-post}]
   ["/posts/update"            {:post cont-posts/update-post}]
   ["/posts/published/:id/:published" {:get cont-posts/toggle-published}]
   ["/posts/new"               {:get cont-posts/admin-new}]
   ["/users/:archived"         {:get cont-users/admin-users}]
   ["/users"                   {:post cont-users/create-user}]])

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   (merge site-routes vclass-routes admin-routes)])
