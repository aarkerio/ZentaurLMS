(ns zentaur.uploads)

(enable-console-print!)

(.log js/console "I am in upload.cljs  !")

(defn add-insert-json []
  (when-let [button (.getElementById js/document "insert-button")]
    (.log js/console (str button ">>>>>"))
    (.addEventListener (.getElementById js/document button) "event"
      (fn [evt]
       (let [atxt (-> evt (.-currentTarget) (.-innerHTML))
           msg  (str "You clicked:  " atxt)]
         (.alert js/window msg)
         (.preventDefault evt))))))

