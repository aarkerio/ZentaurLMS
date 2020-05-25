(ns zentaur.reframe.comments.comments_events
  (:require [goog.dom :as gdom]
            [goog.string :as gstring]
            [re-frame.core :as rf]
            [re-graph.core :as re-graph]))

(rf/reg-event-db
 :load-comments-response
  []
  (fn [db [_ {:keys [data errors]}]]
    (let [pre-comments (:load_comments data)
          comments     (:comments pre-comments)]
      (assoc db :comments comments))))

(rf/reg-event-fx
  :load-comments
  (fn [cfx [_ updates]]
    (let [post-id (.-value (gdom/getElement "post-id"))
          query   (gstring/format "{load_comments(post_id: %i) {comments {comment username created_at}}}"
                                  post-id)]
      (rf/dispatch [::re-graph/query query {} [:load-comments-response]]))))

(rf/reg-event-db
 :process-save-blog-comment
 (fn [db [_ response]]
   (let [comment     (:create_comment (second (first response)))]
         (update-in db [:comments] conj comment))))

(rf/reg-event-fx
  :save-blog-comment
  (fn [cfx _]
    (let [updates (second _)
          {:keys [post-id comment user-id]} updates
          mutation    (gstring/format "mutation { create_comment( post_id: %i, comment: \"%s\", user_id: %i)
                                      { username comment created_at }}"
                                      post-id comment user-id)]
      (rf/dispatch [::re-graph/mutate mutation {} [:process-save-blog-comment]]))))
