(ns zentaur.routes.home
  (:require
    [zentaur.controllers.company-controller :as cont-company]
    [zentaur.controllers.posts-controller   :as cont-posts]
    [zentaur.controllers.tests-controller   :as cont-tests]
    [zentaur.controllers.uploads-controller :as cont-uploads]
    [zentaur.controllers.users-controller   :as cont-users]
    [compojure.core :refer [defroutes context GET POST PUT DELETE PATCH]]))

(defroutes base-routes
  (GET    "/"                          request   (cont-posts/get-posts  request))
  (POST   "/admin/posts"               request   (cont-posts/save-post! request))
  (POST   "/post/savecomment"          request   (cont-posts/save-comment request))
  (GET    "/post/:id"                  request   (cont-posts/single-post request))
  (GET    "/tests/"                    request   (cont-tests/get-tests request))
  (DELETE "/posts/:id"                 request   (cont-posts/delete-post request))
  (GET    "/admin/posts"               request   (cont-posts/admin-posts request))
  (GET    "/login"                     request   (cont-users/login-page request))
  (POST   "/login"                     request   (cont-users/post-login request))
  (GET    "/logout"                    request   (cont-users/clear-session! request))
  (GET    "/admin/users"               request   (cont-users/admin-users request))
  (GET    "/admin/uploads"             request   (cont-uploads/admin-uploads request))
  (POST   "/admin/uploads"             request   (cont-uploads/upload-file request))
  (GET    "/admin/uploads/process/:id" request   (cont-uploads/process request))
  (GET    "/admin/uploads/archive/:id" request   (cont-uploads/archive request))
  (GET    "/admin/tests"               request   (cont-tests/admin-tests request))
  (GET    "/about"                     request   (cont-company/about-page request)))

