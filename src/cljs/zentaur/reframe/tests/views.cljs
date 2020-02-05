(ns zentaur.reframe.tests.views
  (:require [clojure.string :as str]
            [goog.dom :as gdom]
            [reagent.core  :as r]
            [re-frame.core :as rf]
            [zentaur.reframe.tests.forms.blocks :as blk]))

(defn edit-question [{:keys [question id hint explanation qtype]}]
  (let [aquestion    (r/atom question)
        ahint        (r/atom hint)
        aexplanation (r/atom explanation)
        aqtype       (r/atom qtype)]
    (fn []
      [:div.edit_question
       [:div "Question: " [:br]
        [:input {:type      "text"
                 :value     @aquestion
                 :maxLength 180
                 :size      100
                 :on-change #(reset! aquestion (-> % .-target .-value))}]]
       [:div "Hint: " [:br]
        [:input {:type      "text"
                 :value     @ahint
                 :maxLength 180
                 :size      100
                 :on-change #(reset! ahint (-> % .-target .-value))}]]
       [:div "Explanation: " [:br]
        [:input {:type      "text"
                 :value     @aexplanation
                 :maxLength 180
                 :size      100
                 :on-change #(reset! aexplanation (-> % .-target .-value))}]]
       [:div.div-separator
        [:select.form-control.mr-sm-2 {:name      "qtype"
                                       :value     @aqtype
                                       :on-change #(reset! aqtype (-> % .-target .-value))
                                       :id        (str "edit-qtype-select-" id)}
         [:option {:value "1"} "Multiple"]
         [:option {:value "2"} "Open"]
         [:option {:value "3"} "Fullfill"]
         [:option {:value "4"} "Columns"]]]
       [:div [:input.btn {:type  "button"
                          :value "Save"
                          :on-click #(rf/dispatch [:update-question {:question    @aquestion
                                                                     :hint        @ahint
                                                                     :id          id
                                                                     :qtype       @aqtype
                                                                     :explanation @aexplanation}])}]]])))

(defn answer-editing-input [{:keys [answer correct id]}]
  (let [aanswer   (r/atom answer)
        acorrect  (r/atom correct)]
    (fn []
      [:div.edit_question
       [:div "Answer: " [:br]
        [:input {:type      "text"
                 :value     @aanswer
                 :maxLength 180
                 :size      40
                 :on-change #(reset! aanswer (-> % .-target .-value))}]]
       [:input {:type      "checkbox"
                :title     "richtig?"
                :checked   @acorrect
                :on-change #(swap! acorrect not)}]
       [:div [:input.btn {:type "button" :class "btn btn btn-outline-primary-green" :value "Speichern"
                          :on-click #(rf/dispatch [:update-answer {:answer @aanswer :correct @acorrect :id id}])}]]])))
;;;;;;;; FORMS ENDS


(defn display-answer [{:keys [id answer correct question_id key] :as answer-record}]
  (let [answer-class    (if-not correct "all-width-red" "all-width-green")
        answer-text     (if-not correct "answer-text-red" "answer-text-green")
        editing-answer  (r/atom false)]
    (fn []
      [:div
       [:div {:class answer-class}
        [:div.edit-icon-div
         (if @editing-answer
           [:img.img-float-right {:title    "Antwort abbrechen"
                                  :alt      "Antwort abbrechen"
                                  :src      "/img/icon_cancel.png"
                                  :on-click #(swap! editing-answer not)}]
           [:img.img-float-right {:title    "Antwort bearbeiten"
                                  :alt      "Antwort bearbeiten"
                                  :src      "/img/icon_edit.png"
                                  :on-click #(swap! editing-answer not)}])]
        (when @editing-answer
          [answer-editing-input answer-record])
        [:span {:class answer-text} (str key ".-  ("correct")")] "   " answer
        [:img.img-float-right {:title    "Antwort löschen"
                               :alt      "Antwort löschen"
                               :src      "/img/icon_delete.png"
                               :on-click #(rf/dispatch [:delete-answer {:answer-id id :question-id question_id}])}]]])))

(defn input-new-answer
  "Note: this is one-way bound to the global atom, it doesn't subscribe to it"
  [{:keys [question-id on-stop props]}]
  (let [inner       (r/atom "")
        checked     (r/atom false)]
    (fn []
      [:div.div-new-answer
       [:input (merge props
                      {:type        "text"
                       :maxLength   180
                       :size        80
                       :value       @inner
                       :on-change   #(reset! inner (-> % .-target .-value))
                       :on-key-down #(case (.-which %)
                                          27 (on-stop) ; esc
                                          nil)})]
       [:input.btn {:type "checkbox" :title "Richtig?" :aria-label "Richting?"
                    :checked @checked :on-change #(swap! checked not)}]
       [:input.btn {:type "button" :class "btn btn btn-outline-primary-green" :value "Antwort hinzufügen"
                    :on-click #(do (rf/dispatch [:create-answer {:question-id question-id
                                                                       :correct @checked
                                                                       :answer @inner}])
                                   (reset! checked false)
                                   (reset! inner ""))}]])))

(defn fulfill-question-form
  [question text-asterisks]
  (let [fulfill (:fulfill question)
        id (:id question)]
    (fn []
      [:div
       [:div.div-separator fulfill]
       [:div.div-separator
        [:textarea {:value @text-asterisks :on-change  #(reset! text-asterisks (-> % .-target .-value))
                    :placeholder "Text and asterisks" :title "Text and asterisks" :cols 120  :rows 10}]]
        [:input.btn {:type "button" :class "btn btn btn-outline-primary-green" :value "Speichern"
                     :on-click #(rf/dispatch [:update-question {:id      id
                                                                :fulfill fulfill}])}]])))

;; Polimorphysm to the kind of question
(defmulti display-question (fn [question] (:qtype question)))

;; 1: multi 2: open, 3: fullfill, 4: composite questions (columns)
(defmethod display-question 1
  [{:keys [question explanation hint key qtype id ordnen] :as q}]
  (let [counter (r/atom 0)]
    (fn [{:keys [question explanation hint qtype id ordnen] :as q}]
    [:div
     [input-new-answer {:question-id id :on-stop #(js/console.log "stop") :props {:placeholder "Neue antwort"}}]
     (for [answer (:answers q)]
       [display-answer (assoc (second answer) :key (swap! counter inc))])])))

(defmethod display-question 2
  [question]
  [:p "(Dies ist eine offene Frage)"])

(defmethod display-question 3
  [question]
  (let [text-asterisks (r/atom (:fulfill question))]
    [fulfill-question-form question text-asterisks]))

(defmethod display-question 4
  [question]
  [:p "Question columns"])

(defn question-item
  "Display any type of question"
  [{:keys [question explanation hint qtype id ordnen index] :as q}]
  (let [editing-question (r/atom false)]
    (fn []
      [:div.div-question-row
       [:div.edit-icon-div
        (if @editing-question
          [:img.img-float-right {:title    "Frage abbrechen"
                                 :alt      "Frage abbrechen"
                                 :src      "/img/icon_cancel.png"
                                 :on-click #(swap! editing-question not)}]
          [:img.img-float-right {:title    "Frage bearbeiten"
                                 :alt      "Frage bearbeiten"
                                 :src      "/img/icon_edit.png"
                                 :on-click #(swap! editing-question not)}])]  ;; editing ends
     [:div.question-elements
       [:div [:span.bold-font (str index ".- Frage: ")] question  "   ordnen:" ordnen "   question id:" id]
       [:div [:span.bold-font "Hint: "] hint]
       [:div [:span.bold-font "Erläuterung: "] explanation]]
     (when @editing-question
        [edit-question q])
       [display-question q] ;; Polimorphysm for the kind of question
     [:div.img-delete-right
       [:img {:src    "/img/icon_delete.png"
              :title  "Frage löschen"
              :alt    "Frage löschen"
              :on-click #(rf/dispatch [:delete-question id])}]]])))

(defn questions-list
  []
  (let [counter (atom 0)]
    (fn []
      [:section
       (for [question @(rf/subscribe [:questions])]
         (do
            ^{:key (swap! counter inc)} [question-item (second question)]
            ))])))

(defn test-editor-form [test ^string title ^string description ^string tags ^int subject-id]
  (.log js/console (str ">>> VALUE >>>>> " subject-id ))
    [:div {:id "test-whole-display"}
     [:div.edit-icon-div
      (if @(rf/subscribe [:toggle-testform])
        [:img.img-float-right {:title "Bearbeiten test abbrechen" :alt "Bearbeiten test abbrechen" :src "/img/icon_cancel.png"
                               :on-click #(rf/dispatch [:toggle-testform])   }]
        [:img.img-float-right {:title "Test bearbeiten" :alt "Test bearbeiten" :src "/img/icon_edit_test.png"
                               :on-click #(rf/dispatch [:toggle-testform])}])]
     [:div {:id "hidden-form" :class (if @(rf/subscribe [:toggle-testform]) "visible-div" "hidden-div")}
      [:h3.class "Bearbeit test"]
      [:div.div-separator
       [:label {:class "tiny-label"} "Title"]
       [:input {:type  "text" :value @title :placeholder "Title" :title "Title" :maxLength 100
                :on-change #(reset! title (-> % .-target .-value)) :size  100}]]
      [:div.div-separator
       [:label {:class "tiny-label"} "Description"]
       [:input {:type "text" :value @description :on-change #(reset! description (-> % .-target .-value))
                :placeholder "Erklärung" :title "Erklärung"  :maxLength 180 :size 100}]]
      [:div.div-separator
       [:label {:class "tiny-label"} "Tags"]
       [:input {:type "text" :value @tags :on-change #(reset! tags (-> % .-target .-value))
                :placeholder "Tags" :title "Tags" :maxLength 100 :size 100}]]
      [:div.div-separator
       [:select.form-control.mr-sm-2 {:name      "subject-id"
                                      :value     @subject-id
                                      :on-change #(reset! subject-id (-> % .-target .-value))}
        (for [subject @(rf/subscribe [:subjects])]
          ^{:key (:id subject)} [:option {:value (:id subject)} (:subject subject)])
        ]]
      [:div
       [:input {:class "btn btn-outline-primary-green" :type "button" :value "Speichern"
                :on-click #(rf/dispatch [:update-test {:title @title :description @description
                                                       :tags @tags :subject-id @subject-id :test-id (:id test)}])}]]]
      [:div
       [:h1 @title]
       [:div.div-simple-separator [:span {:class "bold-font"} "Tags: "] @tags [:span {:class "bold-font"} " Created:"] (:created_at test)]
       [:div.div-simple-separator [:span {:class "bold-font"}  "Description: "] @description [:span {:class "bold-font"}  "Subject: "] (:subject test)]]])

(defn test-editor-view
  []
  (let [test        (rf/subscribe [:test])
        title       (r/atom nil)
        subject-id  (r/atom nil)
        description (r/atom nil)
        tags        (r/atom nil)]
    (fn []
      (reset! title (:title @test))
      (reset! subject-id (:subject_id @test))
      (reset! description (:description @test))
      (reset! tags (:tags @test))
      [:div
       [test-editor-form @test title description tags subject-id]
       [:img {:src "/img/icon_add_question.png" :alt "Fragen hinzüfugen" :title "Fragen hinzüfugen"
                        :on-click #(rf/dispatch [:toggle-qform])}]])))

(defn create-question-form
  "Verstecken Form for a neue fragen"
  []
  (let [qform        (rf/subscribe [:qform])
        new-question (r/atom "")
        hint         (r/atom "")
        explanation  (r/atom "")
        qtype        (r/atom "1")]
    (fn []
      [:div {:id "hidden-form" :class (if @qform "visible-div" "hidden-div")}
       [:h3.class "Hinzifugen neue fragen"]
       [:div.div-separator
        [:input {:type         "text" :value @new-question
                 :placeholder  "Neue Frage"
                 :title        "Neue Frage"
                 :maxLength    180
                 :on-change    #(reset! new-question (-> % .-target .-value))
                 :size         100}]]
       [:div.div-separator
        [:input {:type         "text"
                 :value        @hint
                 :on-change    #(reset! hint (-> % .-target .-value))
                 :placeholder  "Frage Hinweis"
                 :title        "Frage Hinweis"
                 :maxLength    180
                 :size         100}]]
       [:div.div-separator
        [:input {:type         "text"
                 :value        @explanation
                 :on-change    #(reset! explanation (-> % .-target .-value))
                 :placeholder  "Frage erklärung"
                 :title        "Frage erklärung"
                 :maxLength    180
                 :size         100}]]
       [:div.div-separator
        [:select.form-control.mr-sm-2 {:name      "qtype"
                                       :value     @qtype
                                       :on-change #(reset! qtype (-> % .-target .-value))}
         [:option {:value "1"} "Multiple"]
         [:option {:value "2"} "Open"]
         [:option {:value "3"} "Fullfill"]
         ;; [:option {:value "4"} "Columns"]
         ]]
     [:div
      [:input.btn {:class "btn btn-outline-primary-green" :type "button" :value "Neue Frage speichern"
                   :on-click #(do (rf/dispatch [:create-question {:question    @new-question
                                                                  :hint        @hint
                                                                  :qtype       @qtype
                                                                  :test-id     (.-value (gdom/getElement "test-id"))
                                                                  :explanation @explanation}])
                                  (reset! new-question "")
                                  (reset! hint "")
                                  (reset! explanation ""))}]]])))

(defn todo-app
  []
    [:div {:id "page-container"}
      [test-editor-view]
      [create-question-form]
      [questions-list]
     [:div {:class "footer"}
      [:p "Ziehen Sie die Fragen per Drag & Drop in eine andere Reihenfolge."]]])
