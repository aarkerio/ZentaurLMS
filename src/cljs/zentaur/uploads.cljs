(ns zentaur.uploads
  (:require [goog.dom :as gdom]
            [goog.events :as events])
  (:import [goog.events EventType]))

(enable-console-print!)

(defn mount []
  (.log js/console (str ">>> VALUE >>>>> mount uploads !!!")))

(defn add-insert-json []
  (when-let [button (.getElementById js/document "insert-button")]
    (.log js/console (str button ">>>>>"))
    (.addEventListener (.getElementById js/document button) "event"
      (fn [evt]
       (let [atxt (-> evt (.-currentTarget) (.-innerHTML))
           msg  (str "You clicked:  " atxt)]
         (.alert js/window msg)
         (.preventDefault evt))))))

(def qstart "{ \n \"status\": \"1\", \n \"hint\" : \"\", \n  \"explanation\": \"\", \n  \"question\": \"\", \n")

(def qstart_1 " \"qtype\" : \"1\", \n  \"answers\": [  \n
                       { \"answer\": \"One\", \"correct\": \"false\" }, \n
                       { \"answer\": \"Two\", \"correct\": \"false\" }  \n ] } ")

(def qstart_2 " \"qtype\" : \"5\", \n \"answers\": [  \n
               { \"first_column\": \"México\",  \"second_column\": \"\",   \"name_column\": \"A\", \"correct_column\": \"B\" }, \n
               { \"first_column\": \"Argentina\",  \"second_column\": \"\", \"name_column\": \"B\", \"correct_column\": \"A\" }, \n ] } ")

(def qstart_3 " \"qtype\" : \"3\" \n } ")

(defn build-string [some-integer]
 (let [question qstart]
  (cond
    (= some-integer 1) (str question qstart_1)
    (= some-integer 2) (str question qstart_2)
    (= some-integer 3) (str question qstart_3))))

(defn insert-text [zahlenwert]
  (let [textbox   (js/jQuery "#json_field")
        text-str  (.val textbox)
        question  (build-string zahlenwert)
        _         (.log js/console (str ">>> text-str >>>>> " (.stringify js/JSON text-str)))
        startPos  (.selectionStart textbox)
        endPos    (.selectionStart textbox)
        beforeStr (.substr textbox 0, startPos)
        afterStr  (.substr textbox endPos (count textbox))
        finalStr  (str beforeStr  question  afterStr)]

    (.val textbox finalStr)))


;; (defn set-message [response]
;;   (.log js/console (str ">>> Set msg :::  #####  >>>>> " response))
;;   (let [div-message (gdom/getElement "display-message")
;;         msg         (:msg response)]
;;     (set! (.-innerHTML div-message) msg)
;;     (style/showElement div-message true)))

;; (defn export-json []
;;   (let [json       (.-value (gdom/getElement "json-field"))
;;         id         (.-value (gdom/getElement "upload-id"))
;;         csrf-field (.-value (gdom/getElement "__anti-forgery-token"))]
;;     (POST "/admin/uploads/export"
;;         {:params {:body  json
;;                   :id    id}
;;          :headers {"x-csrf-token" csrf-field}
;;          :handler set-message
;;          :error-handler error-handler})))

;; (defn save-json []
;;   (let [json       (.-value (gdom/getElement "json-field"))
;;         id         (.-value (gdom/getElement "upload-id"))
;;         csrf-field (.-value (gdom/getElement "__anti-forgery-token"))]
;;     (POST "/admin/uploads/save"
;;         {:params {:body json
;;                   :id id}
;;          :headers {"x-csrf-token" csrf-field}
;;          :handler set-message
;;          :error-handler error-handler})))

;; ;;;;    PROCESS LOADERS BLOCK
;; (defn load-process []
;;   (events/listen (gdom/getElement "insert-question") EventType.CHANGE
;;                  (fn [e]
;;                    (let [value (.-value (gdom/getElement "insert-question"))]
;;                      (insert-text (js/parseInt value)))))
;;   (events/listen (gdom/getElement "export-button") EventType.CLICK export-json)
;;   (events/listen (gdom/getElement "save-button") EventType.CLICK save-json))
