(ns zentaur.tests.core
  (:require
            [cljs.core.async :refer [<! chan close!]]
            [cljs-http.client :as http]
            [cljs.loader :as loader]
            [goog.dom :as gdom]
            [goog.events :as events]
            [reagent.core :as r]
            [re-frame.core :as reframe] ;; [dispatch dispatch-sync]]
            [secretary.core :as secretary]
            [zentaur.events]    ;; These two are only required to make the compiler
            [zentaur.subs]      ;; my subscriptions
            [zentaur.views])
  (:require-macros [cljs.core.async.macros :as m :refer [go]]
                   [secretary.core :refer [defroute]])
  (:import [goog History]
           [goog.history EventType]))

;; Put an initial value into app-db.
;; The event handler for `:initialise-db` can be found in `events.cljs`
;; Using the sync version of dispatch means that value is in
;; place before we go onto the next step.
(reframe/dispatch-sync [:initialise-db])

;; -- Routes and History ------------------------------------------------------
;; Although we use the secretary library below, that's mostly a historical
;; accident. You might also consider using:
;;   - https://github.com/DomKM/silk
;;   - https://github.com/juxt/bidi
;; We don't have a strong opinion.
;;
(defroute "/" [] (reframe/dispatch [:set-showing :all]))
(defroute "/:filter" [filter] (reframe/dispatch [:set-showing (keyword filter)]))

(def history
  (doto (History.)
    (events/listen EventType.NAVIGATE
                   (fn [event] (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -- Entry Point -------------------------------------------------------------
;; Within ../../resources/public/index.html you'll see this code
;;    window.onload = function () {
;;      todomvc.core.main();
;;    }
;; So this is the entry function that kicks off the app once the HTML is loaded.
;;

(defn ^:export main
  []
  ;; Render the UI into the HTML's <div id="app" /> element
  ;; The view function `todomvc.views/todo-app` is the
  ;; root view for the entire UI.
  (r/render [zentaur.views/todo-app]
            (.getElementById js/document "test-root-app")))

(main)

;; (defonce app-state (r/atom {:seconds-elapsed 0}))
;; (defonce csrf      (r/atom "not-valid-csrf"))
;; (defonce questions (r/atom (sorted-map)))
;; (defonce answer-1  (r/atom {}))
;; (defonce counter   (r/atom 0))
;; (defonce todos     (r/atom 0))
;; (defonce test-id   (r/atom 0))
;; (defonce title     (r/atom "Test title"))
;; (defonce jtest     (r/atom {:title "Test title"}))

;; (defn set-timeout! [ratom]
;;   (js/setTimeout #(swap! ratom update :seconds-elapsed inc) 7000))

;; (defn toggle [id div] (swap! todos update-in [id :done] not))
;; (defn save [id title] (swap! todos assoc-in [id :title] title))
;; (defn delete [id] (swap! todos dissoc id))

;; (defn add-question [text]
;;   (let [id (swap! todos inc)]
;;     (swap! questions assoc id {:id id :title text :done false})))

;; (defn gather-json []
;;     (let [test-id    (.-value (gdom/getElement "test-id"))
;;           csrf-field (.-value (gdom/getElement "__anti-forgery-token"))]
;;       {:test-id test-id :format :json :keywords? true :response-format :json :csrf-token csrf-field}))

;; (defn initial-load []
;;   (let [params (gather-json)]
;;     (go (let [response (<! (http/post "/admin/tests/load"
;;                                       {:json-params params :headers {"x-csrf-token" (:csrf-token params)}}))]
;;           (reset! jtest (js->clj (.parse js/JSON (:body response)) :keywordize-keys true))
;;           (.log js/console (str ">>> VALUE @JTEST GO >>>>> "  @jtest))
;;           (reset! title (:title @jtest))))))

;; (defn save-all []
;;   (let [params     (gather-json)
;;         all-params (assoc params :questions @questions)]
;;     (go (let [response (<! (http/post "/admin/tests/save"
;;                                       {:json-params all-params :headers {"x-csrf-token" (:csrf-token params)}}))]
;;           (def foo (js->clj (.parse js/JSON (:body response)) :keywordize-keys true))
;;           (.log js/console (str ">>> VALUE @JTEST GO >>>>> "  foo))))))

;; (defn text-input [{:keys [title on-save on-stop value]}]
;;   (let [val   (r/atom title)
;;         stop #(do (reset! val "")
;;                   (if on-stop (on-stop)))
;;         save #(let [v (-> @val str clojure.string/trim)]
;;                 (if-not (empty? v) (on-save v))
;;                 (stop))]
;;     (fn [{:keys [id class placeholder]}]
;;       [:input {:type "text"
;;                :value value
;;                :id id
;;                :class class
;;                :placeholder placeholder
;;                :on-blur save
;;                :on-change #(reset! val (-> % .-target .-value))
;;                :on-key-down #(case (.-which %)
;;                                13 (save)
;;                                27 (stop)
;;                                nil)}])))

;; (defonce init (do
;;                 (initial-load)
;;                 ))

;; (defn answer-item [answer]
;;   (let [answer-string (:answer answer)
;;         id            (:id answer)
;;         correct       (:correct answer)
;;         _   (.log js/console (str ">>> VALUE  ANSWER >>>>> " answer ))]
;;     [:div {:class "answer-view" :id (str "answer-" id) :key (str "answer-" id)}
;;      [:input {:type         "text"
;;               :defaultValue answer-string
;;               :id           (str "answer-" id)
;;               :key          (str "answer-" id)
;;               :maxLength    90
;;               :size         80
;;               :class        "question-class"}]

;;      [:input {:type "checkbox" :id (str "answer-cb-" id) :defaultChecked correct :title "Mark as a correct answer"}]]))

;; (defn question-item [question]
;;   (let [question-str (:question question)
;;         qtype        (:qtype question)
;;         id           (:id question)
;;         number       (swap! counter inc)
;;         _            (.log js/console (str ">>> VALUE  QUESTION >>>>> " question))
;;         _ (r/atom (:answers question))]
;;     [:div {:class "question-view" :id (str "question-" id) :key (str "question-" id)}
;;      (str number " .- ")
;;      [:input {:type         "text"
;;                :defaultValue question-str
;;                :id           (str "question-" id)
;;                :key          (str "question-" id)
;;                :maxLength    120
;;                :size         100
;;                :class        "question-input"}]
;;      (when (= qtype 1)
;;        [:a {:key (str "add-answer-link" id) :id (str "add-answer-link" id) :href "#"} "Add a new answer"])
;;      (when (= qtype 1)
;;        (for [answer (:answers question)]
;;          (answer-item answer)))]))

;; (defn new-question []
;;   [:div {:id "new-question" :key "instructions-component" :class "div-separator"}
;;    [:input {:type        "text"
;;             :id          "new-question"
;;             :key         "new-question"
;;             :placeholder "New question"
;;             :title       "New question"
;;             :maxLength   150
;;             :size        100}]
;;    [:br]
;;    [:a {:id "add-question" :key "add-question" :href "#"} "Save question"]])

;; (defn todo-app []
;;   (let [new-questions (:questions @jtest)
;;         _             (reset! questions new-questions)
;;         title         (:title @jtest)
;;         description   (:description @jtest)
;;         instructions  (:instructions @jtest)
;;         _             (reset! counter 0)]
;;         [:div
;;          [:section#todoapp
;;           [:header#header
;;            [:div {:id "title-component" :key "title-component" :class "div-separator"}
;;             [:input {:type        "text"
;;                      :defaultValue title
;;                      :id          "input-title"
;;                      :key         "input-title"
;;                      :placeholder "Test name"
;;                      :title       "Test name"
;;                      :maxLength    150
;;                      :size         100}]]
;;            [:div {:id "description-component" :key "description-component" :class "div-separator"}
;;             [:input {:type        "text"
;;                      :defaultValue description
;;                      :id          "input-description"
;;                      :key         "input-description"
;;                      :placeholder "Test description"
;;                      :title        "Test description"
;;                      :maxLength    150
;;                      :size         100}]]
;;            [:div {:id "instructions-component" :key "instructions-component" :class "div-separator"}
;;             [:input {:type        "text"
;;                      :defaultValue instructions
;;                      :id          "input-instructions"
;;                      :key         "input-instructions"
;;                      :placeholder "Test instructions"
;;                      :title       "Test instructions"
;;                      :maxLength   150
;;                      :size        100}]]
;;            [:br]
;;            [:a {:id "add-question" :key "add-question" :href "#"} "Add a new question"]]
;;            [new-question]
;;           [:div#questions
;;            (for [question @questions]
;;              (question-item question))]]]))

;; (defn ^:export run []
;;   (r/render [todo-app]
;;             (gdom/getElement "test-root-app")))

;; (when-let [root-div (gdom/getElement "test-root-app")]
;;   (run))

(loader/set-loaded! :tests)
