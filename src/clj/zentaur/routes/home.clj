(ns blog.routes.home
  (:require
       [blog.controllers.posts-controller   :as cont-posts]
       [blog.controllers.users-controller   :as cont-users]
       [blog.controllers.uploads-controller  :as cont-uploads]
       [blog.controllers.company-controller :as cont-company]
       [compojure.core :refer [defroutes context GET POST PUT DELETE PATCH]]))

(defroutes base-routes
  (GET    "/"               request    (cont-posts/get-posts  request))
  (POST   "/admin/posts"    request    (cont-posts/save-post! request))
  (POST   "/post/savecomment" request  (cont-posts/save-comment request))
  (GET    "/post/:id"       request    (cont-posts/single-post request))
  (DELETE "/posts/:id"      request    (cont-posts/delete-post request))
  (GET    "/admin/posts"    request    (cont-posts/admin-posts request))
  (GET    "/login"          request    (cont-users/login-page request))
  (POST   "/login"          request    (cont-users/post-login request))
  (GET    "/logout"         request    (cont-users/clear-session! request))
  (GET    "/admin/users"    request    (cont-users/admin-users request))
  (GET    "/admin/uploads"  request    (cont-uploads/admin-uploads request))
  (POST   "/admin/uploads"  request    (cont-uploads/upload-file request))
  (GET    "/about"          request    (cont-company/about-page request)))

