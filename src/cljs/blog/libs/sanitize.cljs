(ns blog.libs.sanitize
  )

;; (def policy (html-policy :allow-elements ["a"]
;;                          :allow-attributes ["href" :on-elements ["a"]]
;;                          :allow-standard-url-protocols
;;                          :require-rel-nofollow-on-links))

(defn escape-html
  "change special character into html character entitites"
  [text]
  (-> (str text)
      (clojure.string/replace
       #"&(?!(amp;|lt;|gt;|quot;|#x27;|#x2F;))" "&amp;")
      ;(.replace "&" "&amp;")
      (.replace "<" "&lt;")
      (.replace ">" "&gt;")
      (.replace "\"" "&quot;")
      (.replace "'" "&#x27;")
      (.replace "/" "&#x2F;")))
