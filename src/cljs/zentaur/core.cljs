(ns zentaur.core
  (:require [ajax.core :refer [POST DELETE]]
            [clojure.string :as s]
            [goog.dom :as gdom]
            [goog.string :as gstr]
            [goog.events :as events]
            [zentaur.uploads :as uploads]
            [zentaur.reframe.tests.core :as ctests]
            [zentaur.users :as users])
  (:import [goog.events EventType]))

(defn error-handler
  "Ajax error handler (deprecated)"
  [{:keys [status status-text]}]
  (.log js/console (str "Something bad happened: " status " " status-text)))

(defn ^:export validate-new-post []
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

(defn ^:export flash-timeout
  "Removes Ring middleware flash messages after four seconds"
  []
  (when-let [flash-msg (gdom/getElement "flash-msg")]
    (js/setTimeout #(set! (.-className %) "goaway") 4000 flash-msg)))

(defn validate-comment-values []
  (let [body (.getElementById js/document "body")]
    (if (> (count (.-value body)) 0)
      true
      (do (js/alert "Ups, du musst etwas schreiben.")
          false))))

(defn ^:export validate-comment-form
  "Called in zentaur.hiccup.posts-view"
  []
  (if (and js/document
           (.-getElementById js/document))
    (when-let [comment-form (.getElementById js/document "comment-textarea")]
      (set! (.-onsubmit comment-form) validate-comment-values))))

(defn toggle-form []
  (when-let [hform (gdom/getElement "button-show-div")]  ;; versteckte Taste. Nur im Bearbeitungsmodus
    (events/listen hform EventType.CLICK
                   (fn [e]
                     (let [divh    (gdom/getElement "hidden-form")
                           toggle  (if (= (.-className divh) "hidden-div") "visible" "hidden-div")]
                       (set! (.-className divh) toggle))))))

(defn ^:export validate-minimal-test []
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

(defn ^:export deletetest [uurlid]
  (when (js/confirm "Delete test?  (this cannot undo)")
      (let [csrf-field (.-value (gdom/getElement "__anti-forgery-token"))]
        (DELETE "/vclass/tests/delete"
            {:params  {:uurlid uurlid}
             :headers {"x-csrf-token" csrf-field}
             :handler (fn [] (set! js/window.location.href "/vclass/tests"))
             :error-handler error-handler}))))

(defn load-tests []
  (toggle-form)
  (show-new-test-form))

(defn hide-secret-field []
  (when-let [hform (gdom/getElement "open-vc")]  ;; versteckte Taste. Nur im Bearbeitungsmodus
    (events/listen hform EventType.CLICK
                   (fn [e]
                     (let [divh    (gdom/getElement "secret-div")
                           toggle  (if (= (.-className divh) "hidden-div") "visible" "hidden-div")]
                       (set! (.-className divh) toggle))))))

(defn load-vclassrooms []
  (toggle-form)
  (hide-secret-field))

(defn validate-update-file []
  (let [file (.getElementById js/document "file")]
    (if (> (count (.-value file)) 0)
      true
      (do (js/alert "Ooops, you need to seleect a file first")
          false))))

(defn set-upload-form
  "Called in zentaur.hiccup.files-view"
  []
  (if (and js/document
           (.-getElementById js/document))
    (when-let [file-form (.getElementById js/document "upload-file-form")]
      (set! (.-onsubmit file-form) validate-update-file))))

(defn load-files []
  (set-upload-form))

(defn ^:export deletevc [uurlid]
  (when (js/confirm "Delete Classroom? (this cannot undo)")
    (let [csrf-field (.-value (gdom/getElement "__anti-forgery-token"))]
      (DELETE "/vclass/delete"
          {:params  {:uurlid uurlid}
           :headers {"x-csrf-token" csrf-field}
           :handler (fn [] (set! js/window.location.href "/vclass/index"))
           :error-handler error-handler}))))

(defn ask-csrf [csrf-field]
  (when-let [csrf-value  (.-value csrf-field)]
    (POST "/uploads/token"
        {:params {:foo "bar!"}
         :headers {"x-csrf-token" csrf-value}
         :handler (fn [value]
                    (set! (.-value csrf-field) (:anti-forgery-token value)))
         :error-handler error-handler})))

(defn refresh-csrf []
  (when-let [csrf-field (gdom/getElement "__anti-forgery-token")]
    (.log js/console (str ">>> !!!! VALU   E csrf-field >>>>> " csrf-field ))
    (js/setTimeout (do (ask-csrf csrf-field)) 6000000)))

(defn ^:export init []
  (let [_           (flash-timeout)
        _           (refresh-csrf)
        current_url (.-pathname (.-location js/document))
        _           (.log js/console (str ">>> **** tatsächliche: current. Jedoch However**** >>>>> " current_url))]
    (cond
      (s/includes? current_url "admin/users")     (users/load-users)
      (s/includes? current_url "uploads/process") (uploads/mount)
      (s/includes? current_url "/posts/view/")    (validate-comment-form)
      (= current_url "/admin/posts/new")          (new-post-validation)
      (= current_url "/vclass/tests")             (load-tests)
      (= current_url "/vclass/index")             (load-vclassrooms)
      (s/includes? current_url "/vclass/show/")   (load-vclassrooms)
      (s/includes? current_url "/vclass/files/")  (load-files)
      :else "F")))

(defn copytoclipboard
  "Copy val to browser clipboard"
  [val]
  (let [elm (.createElement js/document "textarea")]
    (set! (.-value elm) val)
    (.appendChild js/document.body elm)
    (.select elm)
    (.execCommand js/document "copy")
    (.removeChild js/document.body elm)))
