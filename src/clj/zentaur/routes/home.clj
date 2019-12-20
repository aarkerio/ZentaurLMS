(ns zentaur.routes.home
  (:require [zentaur.controllers.company-controller :as cont-company]
            [zentaur.controllers.posts-controller   :as cont-posts]
            [zentaur.controllers.tests-controller   :as cont-tests]
            [zentaur.controllers.uploads-controller :as cont-uploads]
            [zentaur.controllers.users-controller   :as cont-users]
            [zentaur.middleware :as middleware]))

(def user-routes
  [["/"                  {:get  cont-posts/get-posts}]
   ["/posts/savecomment" {:post cont-posts/save-comment}]
   ["/posts/view/:id"    {:get  cont-posts/single-post}]
   ["/uploads/token"     {:post cont-uploads/token}]
   ["/page/:page"        {:get  cont-company/load-page}]
   ["/login"             {:get  cont-users/login-page :post cont-users/post-login}]
   ["/notauthorized"     {:get  cont-posts/get-posts}]
   ["/logout"            {:get  cont-users/clear-session!}]])

(def admin-routes
  ["/admin"
   ["/posts"                {:get  cont-posts/admin-posts :post cont-posts/save-post}]
   ["/posts/delete/:id"     {:delete cont-posts/delete-post}]
   ["/posts/published/:id/:published" {:get cont-posts/toggle-published}]
   ["/posts/new"            {:get  cont-posts/admin-new}]
   ["/tests"                {:get  cont-tests/admin-index :post cont-tests/create-test}]
   ["/tests/edit/:id"       {:get  cont-tests/admin-edit}]
   ["/tests/load"           {:post cont-tests/load-json}]
   ["/tests/createquestion" {:post cont-tests/create-question}]
   ["/tests/updatequestion" {:post cont-tests/update-question}]
   ["/tests/updateanswer"   {:post cont-tests/update-answer}]
   ["/tests/deletetest"     {:delete cont-tests/delete-test}]
   ["/tests/deletequestion" {:delete cont-tests/delete-question}]
   ["/tests/deleteanswer"   {:delete cont-tests/delete-answer}]
   ["/tests/createanswer"   {:post cont-tests/create-answer}]
   ["/uploads"              {:get  cont-uploads/admin-uploads :post cont-uploads/upload-file}]
   ["/uploads/process/:id"  {:get  cont-uploads/process}]
   ["/uploads/export"       {:post cont-uploads/export-test}]
   ["/uploads/save"         {:post cont-uploads/save-body}]
   ["/uploads/archive/:id"  {:get  cont-uploads/archive}]
   ["/uploads/download/:id" {:get  cont-uploads/download}]
   ["/uploads/extract/:id"  {:get  cont-uploads/extract}]
   ["/users"                {:get  cont-users/admin-users :post cont-users/create-user}]])

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   (merge user-routes admin-routes)])

