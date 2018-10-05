(ns zentaur.views
  (:require [clojure.string :as str]
            [goog.dom :as gdom]
            [reagent.core  :as reagent]
            [re-frame.core :as reframe]))

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
(defn input-answer [answer]
  [:div.div-separator {:id "question-hint-div" :key "question-hint-div"}
      [:input {:type         "text"
               :defaultValue ""
               :id           "answer-hint"
               :key          "answer-hint"
               :placeholder  "Answer"
               :title        "Answer"
               :maxLength    180
               :size         100}]])

(defn new-answer [question-id]
  (let [checked  (reagent/atom false)
        dom-id   (str "new-answer-div" question-id)]
     (fn []
    [:div.div-separator {:id dom-id :key dom-id}
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
                  :on-click #(reframe/dispatch [:create-answer question-id @checked])}]])))

(defn question-item
  [{:keys [id question explanation hint key qtype] :as q}]
  [:div.div-separator {:key (str "divl" id) :id (str "divl" id)}
   [:p {:key (str "divl1" id) :id (str "divl1" id)} [:span.bold-font (str key ".-")] "Question: " question]
   [:p {:key (str "divl2" id) :id (str "divl2" id)} "Hint: " hint]
   [:p {:key (str "divl3" id) :id (str "divl3" id)} "Explanation: " explanation]
   (if (= qtype 1)
     [new-answer]
     (for [answer (:answers q)]
       [input-answer id]))
   [:input.btn {:type "button" :value "Löschen fragen"
                :key (str "btn" id)
                :id (str "btn" id)
                :on-click #(reframe/dispatch [:delete-question id])}]])

(defn questions-list
  []
  (let [questions (reframe/subscribe [:questions])
        counter   (atom 0)]
    [:section#list-section {:key (str "question-list-key" counter) :id (str "question-list-key" counter)}
      (for [question @questions]
        [question-item (assoc question :key (swap! counter inc))])]))

(defn question-entry
  []
  (let [qform (reframe/subscribe [:qform])]
    (.log js/console (str ">>> DISPLAY    >>>>> " @qform))
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
  (let [test  (reframe/subscribe [:test])
        qform (reframe/subscribe [:qform])]
    [:div
     [:h1 (:title @test)]
     [:div.someclass (str "tags: " (:tags @test) "    created: " (:created_at @test)) ]
     (prn "Current test: " @test)
     [:div [:input.btn {:type "button" :value "Fragen hinzüfugen"
                        :on-click #(re-frame.core/dispatch [:toggle-qform])}]]]))

(defn todo-app
  []
  [:div
   [:section#todoapp
    [test-display]
    [question-entry]
    (when (seq @(reframe/subscribe [:questions]))
      [questions-list])]
   [:footer#info
    [:p "Drag and drop to reorder questions"]]])
