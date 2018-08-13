(ns zentaur.core
  (:require [zentaur.users :as users]
         ;;   [zentaur.posts :as posts]
         ;;   [zentaur.uploads :as uploads]
            [goog.dom :as gdom]
            [goog.events :as events])
  (:import [goog.events EventType]))

(defn load-users []
  (events/listen (gdom/getElement "icon-add") EventType.CLICK
       (fn [] (.log js/console (str ">>> VALUE >>>>>  #####   >>>>>   events/listen  in users ns")))))

(defn load-posts []
  (events/listen (gdom/getElement "icon-add") EventType.CLICK
       (fn [] (.log js/console (str ">>> VALUE >>>>>  #####   >>>>>   events/listen  in users ns")))))

(defn ^:export init []
  (let [current_url (.-pathname (.-location js/document))
        _           (.log js/console (str ">>> CURRENT >>>>> " current_url))]
    (case current_url
        "/admin/users"   (load-users)
   ;;     "/admin/posts"   (posts/mount)
   ;;     "/admin/uploads" (uploads/mount)
        "default")))

