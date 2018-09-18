(ns zentaur.tests.core
  (:require [ajax.core :refer [GET POST]]
            [cljs.core.async :refer [<! chan close!]]
            [cljs-http.client :as http]
            [cljs.loader :as loader]
            [goog.dom :as gdom]
            [reagent.core :as r])
  (:require-macros
            [cljs.core.async.macros :as m :refer [go]]))

(.log js/console "I am un tests module!!!  ")

(defonce todos      (r/atom (sorted-map)))
(defonce questions  (r/atom (sorted-map)))
(defonce counter    (r/atom 0))
(defonce jtest      (r/atom {}))

(defn add-todo [text]
  (let [id (swap! counter inc)]
    (swap! todos assoc id {:id id :title text :done false})))

(defn add-question [text]
  (let [id (swap! counter inc)]
    (swap! questions assoc id {:id id :title text :done false})))

(defn toggle [id] (swap! todos update-in [id :done] not))
(defn save [id title] (swap! todos assoc-in [id :title] title))
(defn delete [id] (swap! todos dissoc id))

(defn mmap [m f a] (->> m (f a) (into (empty m))))
(defn complete-all [v] (swap! todos mmap map #(assoc-in % [1 :done] v)))
(defn clear-done [] (swap! todos mmap remove #(get-in % [1 :done])))

(defn gather-json []
    (let [test-id    (.-value (gdom/getElement "test-id"))
          csrf-field (.-value (gdom/getElement "__anti-forgery-token"))]
      {:test-id test-id :format :json :keywords? true :response-format :json :csrf-token csrf-field}))

(defn initial-load []
  (let [params (gather-json)]
    (go (let [response (<! (http/post "/admin/tests/load"
                                      {:json-params params :headers {"x-csrf-token" (:csrf-token params)}}))]
          (reset! jtest (js->clj (.parse js/JSON (:body response)) :keywordize-keys true))))))

(defn text-input [{:keys [title on-save on-stop value]}]
  (let [val   (r/atom title)
        stop #(do (reset! val "")
                  (if on-stop (on-stop)))
        save #(let [v (-> @val str clojure.string/trim)]
                (if-not (empty? v) (on-save v))
                (stop))]
    (fn [{:keys [id class placeholder]}]
      [:input {:type "text" :value value
               :id id :class class :placeholder placeholder
               :on-blur save
               :on-change #(reset! val (-> % .-target .-value))
               :on-key-down #(case (.-which %)
                               13 (save)
                               27 (stop)
                               nil)}])))

(defn title-component []
  (let [title (:title @jtest)]
    [:div
      [text-input {:id "input-title"
                   :placeholder "Test name"
                   :defaultValue title}]]))

(defonce init (do
                (initial-load)
                (add-todo "Allow any arguments to component functions")
                (complete-all true)))

(defn todo-stats [{:keys [filt active done]}]
  (let [props-for (fn [name]
                    {:class (if (= name @filt) "selected")
                     :on-click #(reset! filt name)})]
    [:div
     [:span#todo-count
      [:strong active] " " (case active 1 "item" "items") " left"]
     [:ul#filters
      [:li [:a (props-for :all) "All"]]
      [:li [:a (props-for :active) "Active"]]
      [:li [:a (props-for :done) "Completed"]]]
     (when (pos? done)
       [:button#clear-completed {:on-click clear-done}
        "Clear completed " done])]))

(defn answer-item [answer]
  (let [answer-string (:answer answer)
        id            (:id answer)
        _   (.log js/console (str ">>> VALUE  ANSWER IDDDDDDD >>>>> " id ))]
    [:div.view
     [:input {:type "text"
              :value answer-string
              :id    (str "answer-" id)
              :class "question-class"}]]))

(defn question-item [question]
  (let [question-str (:question question)
        qtype        (:qtype question)
        id           (:id question)
        _            (.log js/console (str ">>> VALUE  QUESTION IDDDDDDD >>>>> " id ))]
    [:div {:class "question-view" :id (str "question-" id)}
      [:input {:type  "text"
               :defaultValue question-str
               :id    (str "question-" id)
               :maxLength 90
               :size 80
               :class "question-input"}]
     ;; (when (= qtype 1)
     ;;   (do [:a {:id "add-answer"} "Add new answer"]
     ;;       (for [(:answers question) answer]
     ;;         (answer-item answer))
     ;;       ))
     ]))

(defn todo-app []
      (let [questions (:questions @jtest)]
        [:div
          [:section#todoapp
            [:header#header
              (title-component)
              [:br]
              [:a {:id "add-question" :href "#"} "Add new question"]]
            [:div#main
               (for [question questions]
                 (question-item question))]]]))

(defn question-component []
  [:div
    [:p "I am a component!"]
    [:div.someclass
      [text-input {:id "new-question"
                   :placeholder "Add a question"}]]
   [:h3 "single-select list"]
   [:div.list-group {:field :single-select :id :pick-one}
    [:div.list-group-item {:key :multiple} "Multiple option"]
    [:div.list-group-item {:key :single} "Single"]
    [:div.list-group-item {:key :columns} "Columns"]]])

(defn question-parent []
  [:div
   [:p "I include simple-component."]
   [question-component]])

(defn ^:export run []
  (r/render [todo-app]
            (gdom/getElement "test-root-app")))

(when-let [root-div (gdom/getElement "test-root-app")]
  (run))

(loader/set-loaded! :tests)
