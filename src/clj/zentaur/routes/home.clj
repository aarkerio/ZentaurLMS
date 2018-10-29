(ns zentaur.routes.home
  (:require
    [zentaur.controllers.company-controller :as cont-company]
    [zentaur.controllers.posts-controller   :as cont-posts]
    [zentaur.controllers.tests-controller   :as cont-tests]
    [zentaur.controllers.uploads-controller :as cont-uploads]
    [zentaur.controllers.users-controller   :as cont-users]
    [compojure.core :refer [defroutes context GET POST PUT DELETE PATCH]]))

(defroutes home-routes
  (GET    "/"                           request   (cont-posts/get-posts request))
  (POST   "/admin/posts"                request   (cont-posts/save-post (:params request)))
  (POST   "/post/savecomment"           request   (cont-posts/save-comment request))
  (GET    "/post/:id"                   request   (cont-posts/single-post request))
  (DELETE "/admin/posts/:id"            request   (cont-posts/delete-post request))
  (GET    "/admin/posts/published/:id/:published" request (cont-posts/toggle-published (:params request)))
  (GET    "/admin/posts"                request   (cont-posts/admin-posts request))
  (GET    "/admin/posts/new"            request   (cont-posts/admin-new request))
  (GET    "/tests"                      request   (cont-tests/get-tests request))
  (GET    "/admin/tests"                request   (cont-tests/admin-index request))
  (GET    "/admin/tests/edit/:id"       request   (cont-tests/admin-edit request))
  (POST   "/admin/tests"                request   (cont-tests/create-test request))
  (POST   "/admin/tests/load"           request   (cont-tests/load-json request))
  (POST   "/admin/tests/createquestion" request   (cont-tests/create-question request))
  (POST   "/admin/tests/updatequestion" request   (cont-tests/update-question request))
  (POST   "/admin/tests/updateanswer"   request   (cont-tests/update-answer request))
  (POST   "/admin/tests/deletequestion" request   (cont-tests/delete-question request))
  (POST   "/admin/tests/createanswer"   request   (cont-tests/create-answer request))
  (GET    "/admin/uploads"              request   (cont-uploads/admin-uploads request))
  (POST   "/admin/uploads"              request   (cont-uploads/upload-file request))
  (GET    "/admin/uploads/process/:id"  request   (cont-uploads/process request))
  (POST   "/admin/uploads/export"       request   (cont-uploads/export-test request))
  (POST   "/admin/uploads/save"         request   (cont-uploads/save-body (:params request)))
  (GET    "/admin/uploads/archive/:id"  request   (cont-uploads/archive request))
  (GET    "/admin/uploads/download/:id" request   (cont-uploads/download (:params request)))
  (GET    "/admin/uploads/extract/:id"  request   (cont-uploads/extract  (:params request)))
  (GET    "/admin/users"                request   (cont-users/admin-users request))
  (POST   "/admin/users"                request   (cont-users/create-user request))
  (GET    "/page/:page"                 request   (cont-company/load-page (:params request)))
  (GET    "/login"                      request   (cont-users/login-page request))
  (POST   "/login"                      request   (cont-users/post-login request))
  (GET    "/logout"                     request   (cont-users/clear-session! request)))

