(ns zentaur.views
  (:require [clojure.string :as str]
            [goog.dom :as gdom]
            [reagent.core  :as reagent]
            [re-frame.core :as re-frame]))

(def ^:private api-url "https://conduit.productionready.io/api")

(defn question-input [{:keys [question on-save on-stop]}]
  (let [val  (reagent/atom question)
        stop #(do (reset! val "")
                  (when on-stop (on-stop)))
        save #(let [v (-> @val str str/trim)]
                (on-save v)
                (stop))]
    (fn [props]
      (.log js/console (str ">>> PROPS >>>>> " props))
      [:input (merge (dissoc props :on-save :on-stop :question)
                     {:type        "text"
                      :value       @val
                      :auto-focus  true
                      :on-blur     save
                      :on-change   #(reset! val (-> % .-target .-value))
                      :on-key-down #(case (.-which %)
                                      13 (save)
                                      27 (stop)
                                      nil)})])))

(defn display-answer [{:keys [id answer correct question-id]} counter]
  (let [separator    (str "display-answer-div-" id)
        answer-class (if (= correct false) "all-width-red" "all-width-green")]
    [:div {:id separator :key separator}
     [:div {:class answer-class}
      [:span.bold-font (str counter ".-  ("correct")")] "   " answer
      [:img.img-float-right
                             {:title  "Delete answer"
                              :alt  "Delete answer"
                              :src    "/img/icon_delete.png"
                              :on-click #(re-frame/dispatch [:delete-answer {:answer-id id :question-id question-id}])}]]]))

(defn new-answer [question-id]
  (let [checked  (reagent/atom false)
        inner    (reagent/atom "")]
     (fn []
    [:div.div-separator {:id question-id :key question-id}
     [:input {:type         "text"
              :defaultValue ""
              :id           (str "new-answer-"question-id)
              :key          (str "new-answer-"question-id)
              :placeholder  "New Answer"
              :title        "New Answer"
              :maxLength    180
              :size         70}]
     [:input.btn {:type "checkbox" :title "richtig?"
                  :key (str "new-answer-box-"question-id) :id (str "new-answer-box-"question-id)
                  :checked @checked :on-change #(swap! checked not)}]
     [:input.btn {:type "button" :value "Antwort hinzufügen"
                  :key (str "new-answer-btn" question-id)
                  :id  (str "new-answer-btn" question-id)
                  :on-click #(re-frame/dispatch [:create-answer question-id @checked])}]
     ])))

(defn input-new-answer
  "note: this is one-way bound to the global atom, it doesn't subscribe to it"
  [{:keys [question-id on-stop props]}]
  (let [inner       (reagent/atom "")
        checked     (reagent/atom false)
        keyed-div   (str "div-sep-" question-id)]
    (fn []
      [:div.div-separator {:id keyed-div :key keyed-div}
       [:input (merge props
                      {:type        "text"
                       :value       @inner
                       :on-change   #(reset! inner (-> % .-target .-value))
                       :on-key-down #(case (.-which %)
                                          27 (on-stop) ; esc
                                          nil)})]
       [:input.btn {:type "checkbox" :title "richtig?"
                    :key (str "new-answer-box-"question-id)
                    :id (str "new-answer-box-"question-id)
                    :checked @checked :on-change #(swap! checked not)}]
       [:input.btn {:type "button" :value "Antwort hinzufügen"
                    :key (str "new-answer-btn" question-id)
                    :id  (str "new-answer-btn" question-id)
                    :on-click #(re-frame/dispatch [:create-answer {:question-id question-id
                                                                   :correct @checked
                                                                   :answer @inner}])}]])))

(defn question-item
  [{:keys [question explanation hint key qtype id ordnen] :as q}]
  (let [counter   (atom 0)]
    [:div.div-separator {:key (str "div-question-separator-" id) :id (str "div-question-separator-" id)}
     [:p {:key (str "div-question" id) :id (str "div-question" id)} [:span.bold-font (str key ".-")] "Question: " question  "   ordnen:" ordnen]
     [:p {:key (str "div-hint" id)     :id (str "div-hint" id)}     "Hint: " hint]
     [:p {:key (str "div-explan" id)   :id (str "div-explan" id)}   "Explanation: " explanation]
     (if (= qtype 1)
       (do [:div [input-new-answer {:question-id id :on-stop #(js/console.log "stopp") :props   {:placeholder "New answer"}}]
            (for [answer (:answers q)]
              [display-answer answer (swap! counter inc)])]))

     [:p [:input.btn {:type     "button"
                      :value    "Löschen fragen"
                      :key      (str "frage-btn-x" id)
                      :id       (str "frage-btn-x" id)
                      :on-click #(re-frame/dispatch [:delete-question id])}]]]))

(defn questions-list
  []
  (let [questions (re-frame/subscribe [:questions])
        counter   (atom 0)]
    [:section {:key (str "question-list-key-" counter) :id (str "question-list-key-" counter)}
     (for [question @questions]
       [question-item (second (assoc-in question [1 :key] (swap! counter inc)))])]))

(defn question-entry
  []
  (let [qform (re-frame/subscribe [:qform])]
    [:div {:id "hidden-form" :class (if @qform "visible-div" "hidden-div")}
      [:h3.class "Hinzifugen neue fragen"]
     [:div.div-separator {:id "question-title-div" :key "question-title-div"}
      [:input {:type         "text"
               :defaultValue ""
               :id           "new-question"
               :key          "new-question"
               :placeholder  "New question"
               :title        "New question"
               :maxLength    180
               :size         100}]]
     [:div.div-separator {:id "question-hint-div" :key "question-hint-div"}
      [:input {:type         "text"
               :defaultValue ""
               :id           "question-hint"
               :key          "question-hint"
               :placeholder  "Question hint"
               :title        "Question hint"
               :maxLength    180
               :size         100}]]
     [:div.div-separator {:id "question-explanation-div" :key "question-explanation-div"}
      [:input {:type         "text"
               :defaultValue ""
               :id           "explanation-description"
               :key          "explanation-description"
               :placeholder  "Question explanation"
               :title        "Question explanation"
               :maxLength    180
               :size         100}]]
     [:div.div-separator {:id "question-qtype-div" :key "question-qtype-div"}
      [:select.form-control.mr-sm-2 {:name "qtype" :id "qtype-select"}
       [:option {:value "1"} "Multiple"]
       [:option {:value "2"} "Open"]
       [:option {:value "3"} "Fullfill"]
       [:option {:value "4"} "Columns"]]]
     [:div
      [:input.btn {:type "button" :value "Save new question"
                   :on-click #(re-frame.core/dispatch [:create-question {:question    (.-value (gdom/getElement "new-question"))
                                                                         :hint        (.-value (gdom/getElement "question-hint"))
                                                                         :qtype       (.-value (gdom/getElement "qtype-select"))
                                                                         :test-id     (.-value (gdom/getElement "test-id"))
                                                                         :explanation (.-value (gdom/getElement "explanation-description"))}
                                                       :toggle-qform])}]]]))

(defn test-display []
  (let [test  (re-frame/subscribe [:test])
        qform (re-frame/subscribe [:qform])]
    [:div
     [:h1 (:title @test)]
     [:div.someclass (str "tags: " (:tags @test) "    created: " (:created_at @test)) ]
     ;; (prn "Current test: " @test)
     [:div [:input.btn {:type "button" :value "Fragen hinzüfugen"
                        :on-click #(re-frame.core/dispatch [:toggle-qform])}]]]))

(defn todo-app
  []
  (let [questions (re-frame/subscribe [:questions])]
        [:div
         [:section#todoapp
          [test-display]
          [question-entry]
          (when (seq @questions)
            [questions-list])]
         [:footer#info
          [:p "Drag and drop to reorder questions"]]]))
