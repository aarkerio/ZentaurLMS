(ns zentaur.core
  (:require [ajax.core :refer [GET POST DELETE]]
            [cljs.loader :as loader]
            [clojure.string :as s]
            [goog.dom :as gdom]
            [goog.string :as gstr]
            [goog.events :as events]
            [goog.style :as style]
            [zentaur.posts :as posts]
            [zentaur.uploads :as uploads]
            [zentaur.users :as users]
            [zentaur.reframe.tests.core :as ctests])
  (:import [goog.events EventType]))

;;  Ajax handlers
(defn handler [response]
  (.log js/console (str response)))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

(defn validate-new-post []
  (let [title  (.getElementById js/document "title")
        tags   (.getElementById js/document "body")]
    (if (and (> (count (.-value title)) 0)
             (> (count (.-value tags)) 0))
      true
      (do (js/alert "Please, complete the form!")
          false))))

(defn new-post-validation []
  (if (and js/document
           (.-getElementById js/document))
    (when-let [test-form (.getElementById js/document "new-post-form")]
      (set! (.-onsubmit test-form) validate-new-post))))

(defn- remove-flash []
  (when-let [flash-msg (.-value (gdom/getElement "flash-msg"))]
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

(defn show-new-test-form
  "Called from zentaur.hiccup.admin.tests-view"
  []
  (if (and js/document
           (.-getElementById js/document))
    (when-let [test-form (.getElementById js/document "submit-test-form")]
      (set! (.-onsubmit test-form) validate-minimal-test))))

(defn validate-comment-values []
  (let [body  (.getElementById js/document "body")]
    (if (> (count (.-value body)) 0)
      true
      (do (js/alert "Ups, du musst etwas schreiben.")
          false))))

(defn validate-comment-form
  "Called in zentaur.hiccup.posts-view"
  []
  (if (and js/document
           (.-getElementById js/document))
    (when-let [comment-form (.getElementById js/document "comment-textarea")]
      (set! (.-onsubmit comment-form) validate-comment-values))))

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
  (new-post-validation)
  (show-new-test-form)
  (let [current_url (.-pathname (.-location js/document))
        _           (.log js/console (str ">>> **** tatsächliche: current. Jedoch However**** >>>>> " current_url))]
    (cond
      (s/includes? current_url "admin/users")     (users/load-users)
      (s/includes? current_url "uploads/process") (uploads/load-process)
      (s/includes? current_url "admin/posts")     (posts/load-posts)
      (s/includes? current_url "/posts/view/")    (validate-comment-form)
      (= current_url "/admin/posts/new")          (.log js/console (str ">>> test-formtest(new-post-validation)"))
      (= current_url "/vclass/tests")             (load-tests)
      :else "F")))
