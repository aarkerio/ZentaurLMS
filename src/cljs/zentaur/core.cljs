(ns zentaur.core
  (:require [ajax.core :refer [GET POST DELETE]]
            [cljs.loader :as loader]
            [clojure.string :as s]
            [goog.dom :as gdom]
            [goog.string :as gstr]
            [goog.events :as events]
            [goog.style :as style]
            [zentaur.users :as users]
            [zentaur.reframe.tests.core :as ctests])
  (:import [goog.events EventType]))

(enable-console-print!)

;;    Ajax handlers
(defn handler [response]
  (.log js/console (str response)))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

;; Users
(defn- load-users []
  (events/listen (gdom/getElement "icon-add") EventType.CLICK
                 (fn [e]
                   (let [divh      (gdom/getElement "divhide")
                         divclass  (.-className divh)
                         toggle   (if (= divclass "hidden-div") "visible" "hidden-div")]
                     (do
                       (.log js/console (str ">>> VALUE >>>>>  " e))
                       (set! (.-className divh) toggle))))))
;;;;;  PROCESS
(def qstart "{ \n \"status\": \"1\", \n \"hint\" : \"\", \n  \"explanation\": \"\", \n  \"question\": \"\", \n")

(def qstart_1 " \"qtype\" : \"1\", \n  \"answers\": [
                       { \"answer\": \"One\", \"correct\": \"false\" }, \n
                       { \"answer\": \"Two\", \"correct\": \"false\" }  \n ] } ")

(def qstart_2 " \"qtype\" : \"5\", \n \"answers\": [
               { \"first_column\": \"México\",  \"second_column\": \"\",   \"name_column\": \"A\", \"correct_column\": \"B\" }, \n
               { \"first_column\": \"Argentina\",  \"second_column\": \"\", \"name_column\": \"B\", \"correct_column\": \"A\" }, \n ] } ")

(def qstart_3 " \"qtype\" : \"3\" \n } ")

(defn build-string [zahlenwert]
 (let [question qstart]
  (cond
    (= zahlenwert 1) (str question qstart_1)
    (= zahlenwert 2) (str question qstart_2)
    (= zahlenwert 3) (str question qstart_3))))

(defn insert-text [zahlenwert]
  (let [textbox   (gdom/getElement "json-field")
        text-str  (.-value textbox)   ;; goog.dom.getTextContent  getTextContent
        question  (build-string zahlenwert)
        _         (.log js/console (str ">>> QUESTION >>>>> " (.stringify js/JSON question)))
        startPos  (.-selectionStart textbox)
        endPos    (.-selectionEnd textbox)
        beforeStr (subs text-str 0 startPos)
        afterStr  (subs text-str endPos (count text-str))
        finalStr  (str beforeStr  question  afterStr)]

    (set! (.-innerHTML textbox) finalStr)))

(defn set-message [response]
  (.log js/console (str ">>> Set msg :::  #####  >>>>> " response))
  (let [div-message (gdom/getElement "display-message")
        msg         (:msg response)]
    (set! (.-innerHTML div-message) msg)
    (style/showElement div-message true)))

(defn export-json []
  (let [json       (.-value (gdom/getElement "json-field"))
        id         (.-value (gdom/getElement "upload-id"))
        csrf-field (.-value (gdom/getElement "__anti-forgery-token"))]
    (POST "/admin/uploads/export"
        {:params {:body  json
                  :id    id}
         :headers {"x-csrf-token" csrf-field}
         :handler set-message
         :error-handler error-handler})))

(defn save-json []
  (let [json       (.-value (gdom/getElement "json-field"))
        id         (.-value (gdom/getElement "upload-id"))
        csrf-field (.-value (gdom/getElement "__anti-forgery-token"))]
    (POST "/admin/uploads/save"
        {:params {:body json
                  :id id}
         :headers {"x-csrf-token" csrf-field}
         :handler set-message
         :error-handler error-handler})))

;;;;    PROCESS LOADERS BLOCK
(defn- load-process []
  (events/listen (gdom/getElement "insert-question") EventType.CHANGE
                 (fn [e]
                   (let [value (.-value (gdom/getElement "insert-question"))]
                     (insert-text (js/parseInt value)))))
  (events/listen (gdom/getElement "export-button") EventType.CLICK export-json)
  (events/listen (gdom/getElement "save-button") EventType.CLICK save-json))

(defn- load-posts []
  (events/listen (gdom/getElement "icon-add") EventType.CLICK
       (fn [] (.log js/console (str ">>> VALUE >>>>>  #####   >>>>>   events/listen  in users ns")))))

(defn- remove-flash []
  (.log js/console (str ">>> REMOVING FLASH MESSAGE !!!!>>>>> "))
  (when-let [flash-msg (gdom/getElement "flash-msg")]
    (.log js/console (str ">>> flash-msg VALUE >>>>> " flash-msg ))
    (js/setTimeout (.-remove flash-msg) 9000)))

(defn- flash-timeout []
  (if-let [flash-msg (gdom/getElement "flash-msg")]
    (js/setTimeout (remove-flash) 90000)))

(defn validate-minimal-test []
  (let [title  (.getElementById js/document "title")
        tags   (.getElementById js/document "tags")]
    (if (and (> (count (.-value title)) 0)
             (> (count (.-value tags)) 0))
      true
      (do (js/alert "Please, complete the form!")
          false))))

(defn init-form []
  ;; verify that js/document exists and that it has a getElementById
  ;; property
  (if (and js/document
           (.-getElementById js/document))
    ;; get loginForm by element id and set its onsubmit property to
    ;; our validate-form function
    (let [test-form (.getElementById js/document "submit-test-form")]
      (set! (.-onsubmit test-form) validate-minimal-test))))

(defn- load-tests []
  (when-let [hform (gdom/getElement "button-show-div")]  ;; versteckte Taste. Nur im Bearbeitungsmodus
    (events/listen hform EventType.CLICK
                   (fn [e]
                     (let [divh    (gdom/getElement "hidden-form")
                           toggle  (if (= (.-className divh) "hidden-div") "visible" "hidden-div")]
                       (set! (.-className divh) toggle))))))

(defn delete-test [test-id]
  (let [csrf-field (.-value (gdom/getElement "__anti-forgery-token"))]
    (DELETE "/admin/tests/deletetest"
        {:params  {:test-id test-id}
         :headers {"x-csrf-token" csrf-field}
         :handler (fn [] (set! js/window.location.href "/admin/tests"))
         :error-handler error-handler})))

(defn ^:export deletetest [test-id]
  (when (js/confirm "Delete test?")
    (delete-test test-id)))

(defn ask-csrf [csrf-field]
  (when-let [csrf-value  (.-value csrf-field)]
    (POST "/uploads/token"
        {:params {:foo "bar!"}
         :headers {"x-csrf-token" csrf-value}
         :handler (fn [value]
                    (set! (.-value csrf-field) (:anti-forgery-token value)))
         :error-handler error-handler})))

(defn refresh-csrf []
  (when-let [csrf-field (.-value (gdom/getElement "__anti-forgery-token"))]
    (js/setTimeout (do (ask-csrf csrf-field)) 6000000)))

(defn ^:export init []
  (flash-timeout)
  (refresh-csrf)
  (let [current_url (.-pathname (.-location js/document))
        _           (.log js/console (str ">>> **** tatsächliche: current. Jedoch However**** >>>>> " current_url))]
    (cond
      (s/includes? current_url "admin/users")     (load-users)
      (s/includes? current_url "uploads/process") (load-process)
      (s/includes? current_url "admin/posts")     (load-posts)
      (= current_url "/admin/tests")              (do (init-form)(load-tests))
      :else "F")))
