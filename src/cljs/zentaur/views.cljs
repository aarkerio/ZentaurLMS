(ns zentaur.views
  (:require [clojure.string :as str]
            [goog.dom :as gdom]
            [reagent.core  :as reagent]
            [re-frame.core :as re-frame]))

(def ^:private api-url "https://conduit.site.net/api")

(defn question-input [{:keys [question on-save on-stop]}]
  (let [val  (reagent/atom question)
        stop #(do (reset! val "")
                  (when on-stop (on-stop)))
        save #(let [v (-> @val str str/trim)]
                (on-save v)
                (stop))]
    (fn [props]
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
        answer-class (if (= correct false) "all-width-red" "all-width-green")
        answer-text  (if (= correct false) "answer-text-red" "answer-text-green")]
    [:div {:id separator :key separator}
     [:div {:class answer-class}
      [:span {:class answer-text} (str counter ".-  ("correct")")] "   " answer
      [:img.img-float-right
                             {:title  "Delete answer"
                              :alt  "Delete answer"
                              :src    "/img/icon_delete.png"
                              :on-click #(re-frame/dispatch [:delete-answer {:answer-id id :question-id question-id}])}]]]))

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
                    :on-click #(do (re-frame/dispatch [:create-answer {:question-id question-id
                                                                       :correct @checked
                                                                       :answer @inner}])
                                   (reset! checked false)
                                   (reset! inner ""))}]])))
;; Polimorphysm for kind of question
(defmulti display-question (fn [question] (:qtype question)))

;; 1: multi 2: open, 3: fullfill, 4: composite questions (columns)
(defmethod display-question 1
  [{:keys [question explanation hint key qtype id ordnen] :as q}]
  (let [counter (atom 0)]
    [:div [input-new-answer {:question-id id :on-stop #(js/console.log "stopp") :props   {:placeholder "New answer"}}]
     (for [answer (:answers q)]
       [display-answer answer (swap! counter inc)])]))

(defmethod display-question 2
  [question]
  [:p "This is an open question"])

(defmethod display-question 3
  [question]
  [:p "Question fullfill"])

(defmethod display-question 4
  [question]
  [:p "Question columns"])

(defn simple-input [{:keys [question id hint explanation qtype]}]
  (let [aquestion    (reagent/atom question)
        ahint        (reagent/atom hint)
        aexplanation (reagent/atom explanation)
        aqtype       (reagent/atom qtype)]
    (fn []
      [:div.edit_question
       [:div "Question: " [:br]
        [:input {:type      "text"
                 :value     @aquestion
                 :key       (str "edit-question-id-" id)
                 :id        (str "edit-question-id-" id)
                 :maxLength 180
                 :size      100
                 :on-change #(reset! aquestion (-> % .-target .-value))}]]
       [:div "Hint: " [:br]
        [:input {:type      "text"
                 :value     @ahint
                 :key       (str "edit-hint-id-" id)
                 :id        (str "edit-hint-id-" id)
                 :maxLength 180
                 :size      100
                 :on-change #(reset! ahint (-> % .-target .-value))}]]
       [:div "Explanation: " [:br]
        [:input {:type      "text"
                 :value     @aexplanation
                 :key       (str "edit-hint-id-" id)
                 :id        (str "edit-hint-id-" id)
                 :maxLength 180
                 :size      100
                 :on-change #(reset! aexplanation (-> % .-target .-value))}]]
       [:div.div-separator {:id "question-qtype-div" :key "question-qtype-div"}
        [:select.form-control.mr-sm-2 {:name      "qtype"
                                       :value     @aqtype
                                       :on-change #(reset! aqtype (-> % .-target .-value))
                                       :id        "qtype-select"}
         [:option {:value "1"} "Multiple"]
         [:option {:value "2"} "Open"]
         [:option {:value "3"} "Fullfill"]
         [:option {:value "4"} "Columns"]]]
       [:div [:input.btn {:type "button" :value "Save"
                          :on-click #(re-frame.core/dispatch [:update-question {:question    @aquestion
                                                                                :hint        @ahint
                                                                                :id          id
                                                                                :qtype       @aqtype
                                                                                :explanation @aexplanation}])}]]])))

(defn question-item
  [{:keys [question explanation hint key qtype id ordnen] :as q}]
  (let [counter (reagent/atom 0)
        editing (reagent/atom false)]
    (fn []
      [:div.div-separator {:key (str "div-question-separator-" id) :id (str "div-question-separator-" id)}
       [:div.edit-icon-div
        (if @editing
          [:img.img-float-right {:title    "Cancel question"
                                 :alt      "Cancel question"
                                 :key      (str "cancel-question-img-" id)
                                 :id       (str "cancel-question-img-" id)
                                 :src      "/img/icon_cancel.png"
                                 :on-click #(swap! editing not)}]
          [:img.img-float-right {:title    "Edit question"
                                 :alt      "Edit question"
                                 :key      (str "edit-question-img-" id)
                                 :id       (str "edit-question-img-" id)
                                 :src      "/img/icon_edit.png"
                                 :on-click #(swap! editing not)}])]
     [:p {:key (str "div-question" id) :id (str "div-question" id)} [:span.bold-font (str key ".-")] "Question: " question  "   ordnen:" ordnen "   id:" id]
     [:p {:key (str "div-hint" id)     :id (str "div-hint" id)}     [:span.bold-font "Hint: "] hint]
     [:p {:key (str "div-explan" id)   :id (str "div-explan" id)}   [:span.bold-font "Explanation: "] explanation]
     (when @editing
       [simple-input q])
     (display-question q)
     [:div.img-delete-right
      [:img {:src    "/img/icon_delete.png"
             :title  "Delete question"
             :alt    "Delete question"
             :key    (str "frage-btn-x" id)
             :id     (str "frage-btn-x" id)
             :on-click #(re-frame/dispatch [:delete-question id])}]]] ) ))

(defn questions-list
  []
  (let [questions (re-frame/subscribe [:questions])
        counter   (atom 0)]
    [:section {:key (str "question-list-key-" counter) :id (str "question-list-key-" counter)}
     (for [question @questions]
       [question-item (second (assoc-in question [1 :key] (swap! counter inc)))])]))

(defn question-entry
  []
  (let [qform        (re-frame/subscribe [:qform])
        new-question (reagent/atom "")
        hint         (reagent/atom "")
        explanation  (reagent/atom "")
        qtype        (reagent/atom "1")]
    (fn []
      [:div {:id "hidden-form" :class (if @qform "visible-div" "hidden-div")}
       [:h3.class "Hinzifugen neue fragen"]
       [:div.div-separator {:id "question-title-div" :key "question-title-div"}
        [:input {:type         "text"
                 :value         @new-question
                 :id           "new-question"
                 :key          "new-question"
                 :placeholder  "New question"
                 :title        "New question"
                 :maxLength    180
                 :on-change    #(reset! new-question (-> % .-target .-value))
                 :size         100}]]
       [:div.div-separator {:id "question-hint-div" :key "question-hint-div"}
        [:input {:type         "text"
                 :value        @hint
                 :on-change    #(reset! hint (-> % .-target .-value))
                 :id           "question-hint"
                 :key          "question-hint"
                 :placeholder  "Question hint"
                 :title        "Question hint"
                 :maxLength    180
                 :size         100}]]
       [:div.div-separator {:id "question-explanation-div" :key "question-explanation-div"}
        [:input {:type         "text"
                 :value        @explanation
                 :id           "explanation-description"
                 :key          "explanation-description"
                 :on-change    #(reset! explanation (-> % .-target .-value))
                 :placeholder  "Question explanation"
                 :title        "Question explanation"
                 :maxLength    180
                 :size         100}]]
       [:div.div-separator {:id "question-qtype-div" :key "question-qtype-div"}
        [:select.form-control.mr-sm-2 {:name      "qtype"
                                       :value     @qtype
                                       :on-change #(reset! qtype (-> % .-target .-value))
                                       :id        "qtype-select"}
         [:option {:value "1"} "Multiple"]
         [:option {:value "2"} "Open"]
         [:option {:value "3"} "Fullfill"]
         [:option {:value "4"} "Columns"]]]
     [:div
      [:input.btn {:type "button" :value "Save new question"
                   :on-click #(do (re-frame.core/dispatch [:create-question {:question    @new-question
                                                                             :hint        @hint
                                                                             :qtype       @qtype
                                                                             :test-id     (.-value (gdom/getElement "test-id"))
                                                                             :explanation @explanation}
                                                           :toggle-qform])
                                  (reset! new-question "")
                                  (reset! hint "")
                                  (reset! explanation ""))}]]])))

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
