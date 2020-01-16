(ns zentaur.routes.home
  (:require [buddy.auth.accessrules :refer [wrap-access-rules]]
            [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.flash :refer [wrap-flash]]
            [ring-ttl-session.core :refer [ttl-memory-store]]
            [zentaur.controllers.api.vclassroom     :as cont-api]
            [zentaur.controllers.company-controller :as cont-company]
            [zentaur.controllers.export-controller  :as cont-export]
            [zentaur.controllers.posts-controller   :as cont-posts]
            [zentaur.controllers.tests-controller   :as cont-tests]
            [zentaur.controllers.uploads-controller :as cont-uploads]
            [zentaur.controllers.users-controller   :as cont-users]
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
   ["/"                  {:get  cont-posts/get-posts}]
   ["/tests"                   {:get  cont-tests/admin-index}]
   ["/tests/edit/:id"          {:get  cont-tests/admin-edit}]
   ["/tests/exporttestpdf/:id" {:get  cont-export/export-test-pdf}]
   ["/tests/exporttestodf/:id" {:get  cont-export/export-test-odf}]
   ["/uploads"                 {:get  cont-uploads/admin-uploads :post cont-uploads/upload-file}]
   ["/uploads/process/:id"     {:get  cont-uploads/process}]
   ["/uploads/export"          {:post cont-uploads/export-test}]
   ["/uploads/save"            {:post cont-uploads/save-body}]
   ["/uploads/archive/:id"     {:get  cont-uploads/archive}]
   ["/uploads/download/:id"    {:get  cont-uploads/download}]
   ["/uploads/extract/:id"     {:get  cont-uploads/extract}]])

(def api-routes
  ["/api"
   ["/load-test"         {:post cont-api/load-test}]
   ["/createquestion"    {:post cont-api/create-question}]
   ["/createanswer"      {:post cont-api/create-answer}]
   ["/updatequestion"    {:post cont-api/update-question}]
   ["/updateanswer"      {:post cont-api/update-answer}]
   ["/updatetest"        {:post cont-api/update-test}]
   ["/deletetest"        {:delete cont-api/delete-test}]
   ["/deletequestion"    {:delete cont-api/delete-question}]
   ["/deleteanswer"      {:delete cont-api/delete-answer}]])

(def admin-routes
  ["/admin"
   ["/posts"                   {:get  cont-posts/admin-posts :post cont-posts/save-post}]
   ["/posts/delete/:id"        {:delete cont-posts/delete-post}]
   ["/posts/published/:id/:published" {:get cont-posts/toggle-published}]
   ["/posts/new"               {:get  cont-posts/admin-new}]
   ["/users"                   {:get  cont-users/admin-users :post cont-users/create-user}]])

(defn home-routes []
  [""
   {:middleware [middleware/wrap-auth
                 (wrap-access-rules {:rules middleware/rules :on-error middleware/on-error})
                 ;;(wrap-authentication (session-backend))
                 ;;(wrap-defaults (assoc-in site-defaults [:session :store] (ttl-memory-store (* 60 30))))
                 middleware/wrap-csrf
                 middleware/wrap-formats
                 wrap-flash
                 ]}
   (merge site-routes vclass-routes api-routes admin-routes)])

