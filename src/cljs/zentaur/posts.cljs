(ns zentaur.posts
  (:require [zentaur.libs.sanitize :as s]
            [zentaur.uploads :as uploads]
            [ajax.core :refer [GET POST DELETE]]))

(defn- handler [response]
  (let [parsed       (str "t   /  read r response")
        _            (.log js/console (str ">>> PARSED >>>>> " (type parsed) ">>>>" parsed))
        comment      (get parsed "comment")
        created_at   (get parsed "created_at")
        last_name    (get parsed "last_name")
        _            (.log js/console (str ">>> COMMENT >>>>> " comment))
        comments_div (.getElementById js/document "comments")]
    (.append comments_div (s/escape-html (str created_at "<br />" last_name "<br />" comment)))
    (.log js/console (str "Handler response: " response))))

(defn- error-handler [{:keys [status stext]}]
  (.log js/console (str "something bad happened: " status " " stext)))

(defn- send-ajax [comment post_id csrf-token]
  (POST "/post/savecomment"
      {:params {:comment comment
                :post_id post_id}
       :headers {"x-csrf-token" csrf-token}
       :handler handler
       :error-handler error-handler}))

(defn listener-msg
  ([elem] (listener-msg elem "msgtextarea" "click" "send-ajax"))
  ([elem container event function]
     (.addEventListener (.getElementById js/document elem) event
                        (fn [evt]
                          (.log js/console (str "evt: >>>>> " evt))
                          (let [atxt       (-> evt (.-currentTarget) (.-innerHTML))
                                csrf-token (.getElementById js/document "__anti-forgery-token")
                                csrf-value (-> csrf-token (.-value))
                                post_id    (-> (.getElementById js/document "post_id") (.-value))
                                comment    (-> (.getElementById js/document container) (.-value))]
                            (.log js/console (str "  comment : >>>>> " comment))
                            (.preventDefault evt)
                            (set! (.-value (.getElementById js/document container)) "")
                            (send-ajax comment post_id csrf-value))))))

(defn set-country
  ([] (set-country "us" 98))
  ([country code] (println country code)))

(defn add-listener-ccc [elem event function]
  (.log js/console (str elem ">>>>>"))
  (.addEventListener (.getElementById js/document elem) event
    (fn [evt]
     (let [atxt (-> evt (.-currentTarget) (.-innerHTML))
           msg  (str "You clicked:  " atxt)]
       (.alert js/window msg)
       (.preventDefault evt)))))

(defn ^:uploads upload-functions []
  (uploads/add-insert-json))

(defn mount []
  (.log js/console (str ">>> VALUE >>>>> mount POSTS !!!")))

(defn send-delete-post [post-id]
  (let [csrf-field (.-value (.getElementById js/document "__anti-forgery-token"))]
    (DELETE "/admin/posts/deletepost"
        {:params  {:post-id post-id}
         :headers {"x-csrf-token" csrf-field}
         :handler (fn [] (set! js/window.location.href "/admin/posts"))
         :error-handler error-handler})))

(defn ^:export deletepost [post-id]
  (when (js/confirm "Delete post?")
    (send-delete-post post-id)))

(defn load-posts
  "Called in zentaur.hiccup.posts-edit"
  []
  (.log js/console (str ">>> VALUE  load-posts BEFORE>>>>> "  ))
  (when-let [open-image (.getElementById js/document "open_images")]
    (set! (.-onclick open-image) (fn []
                                   (.log js/console (str ">>> VALUE in function magic happens >>>>> "  ))
                                   (.open js/window "/vclass/files/img", "popUpWindow",
                                          "height=400,width=300,left=100,top=100,resizable=yes,scrollbars=yes,toolbar=yes,menubar=no,location=no,directories=no,status=no")
                                   ))))

