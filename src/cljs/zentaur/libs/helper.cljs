(ns zentaur.libs.helper
  )

(defn my-toggle [element-str]
  (let [div-message (gdom/getElement element-str)]
    (style/showElement div-message (not (style/isElementShown div-message)))))
