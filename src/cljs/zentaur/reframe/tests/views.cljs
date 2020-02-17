(ns zentaur.reframe.tests.views
  (:require [clojure.string :as str]
            [goog.dom :as gdom]
            [reagent.core  :as r]
            [re-frame.core :as rf]
            [zentaur.reframe.tests.forms.blocks :as blk]
            [zentaur.reframe.tests.libs :as zlib]))

(defn edit-question [{:keys [question id hint explanation qtype points]}]
  (let [aquestion    (r/atom question)
        ahint        (r/atom hint)
        aexplanation (r/atom explanation)
        aqtype       (r/atom qtype)
        apoints      (r/atom points)]
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
        [:select.form-control.mr-sm-2 {:name      "points"
                                       :value     @apoints
                                       :title     "Points"
                                       :on-change #(reset! apoints (-> % .-target .-value))}
         (for [pvalue (range 1 6)]
           ^{:key pvalue} [:option {:value pvalue} pvalue])]]
       [:div.div-separator
        [:select.form-control.mr-sm-2 {:name      "qtype"
                                       :value     @aqtype
                                       :title     "Type of question"
                                       :on-change #(reset! aqtype (-> % .-target .-value))}
         [:option {:value "1"} "Multiple"]
         [:option {:value "2"} "Open"]
         [:option {:value "3"} "Fullfill"]
         ;; [:option {:value "4"} "Columns"]
         ]]
       [:div [:input.btn {:type  "button" :class "btn btn btn-outline-primary-green" :value "Speichern"
                          :on-click #(rf/dispatch [:update-question {:id          id
                                                                     :question    @aquestion
                                                                     :hint        @ahint
                                                                     :points      @apoints
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
                          :on-click #(rf/dispatch [:update-answer {:answer @aanswer
                                                                   :correct @acorrect
                                                                   :answer_id id}])}]]])))

(defn display-answer [{:keys [id answer correct question_id key] :as answer-record}]
  (let [answer-class    (if-not correct "all-width-red" "all-width-green")
        answer-text     (if-not correct "answer-text-red" "answer-text-green")
        editing-answer  (r/atom false)]
    (fn []
      [:div {:class answer-class}
       [:div
        [:img.img-float-right {:title    "Frage nachbestellen"
                              :alt      "Frage nachbestellen"
                              :src      "/img/icon_blue_up.png"
                              :on-click #(rf/dispatch [:reorder-answer {:answer-id id :question-id question_id}])}]
       [:img.img-float-right {:title    "Senden Sie nach unten"
                              :alt      "Senden Sie nach unten"
                              :src      "/img/icon_blue_down.png"
                              :on-click #(rf/dispatch [:reorder-answer {:answer-id id :question-id question_id}])}]

        (if @editing-answer
          [:div
           [answer-editing-input answer-record]
           [:img.img-float-right {:title    "Antwort abbrechen"
                                  :alt      "Antwort abbrechen"
                                  :src      "/img/icon_cancel.png"
                                  :on-click #(swap! editing-answer not)}]]
          [:div
           [:div [:span {:class answer-text} (str key ".-  ("correct")")] " " answer ]
           [:img.img-float-right {:title    "Antwort bearbeiten"
                                  :alt      "Antwort bearbeiten"
                                  :src      "/img/icon_edit.png"
                                  :on-click #(swap! editing-answer not)}]])]

       [:img.img-float-right {:title    "Antwort löschen"
                              :alt      "Antwort löschen"
                              :src      "/img/icon_delete.png"
                              :on-click #(rf/dispatch [:delete-answer {:answer-id id :question-id question_id}])}]])))

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
  [question]
  (let [afulfill  (r/atom (:fulfill question))
        id        (:id question)]
    (fn []
      [:div
       [:div.div-separator (zlib/asterisks-to-spaces @afulfill)]
       [:div.div-separator
        [:textarea {:value @afulfill :on-change  #(reset! afulfill (-> % .-target .-value))
                    :placeholder "Text and asterisks" :title "Text and asterisks" :cols 120  :rows 10}]]
        [:input.btn {:type "button" :class "btn btn btn-outline-primary-green" :value "Speichern"
                     :on-click #(rf/dispatch [:update-fulfill {:id      id
                                                               :fulfill @afulfill}])}]])))
;; Polimorphysm to the kind of question
(defmulti display-question (fn [question] (:qtype question)))

;; 1: multi 2: open, 3: fullfill, 4: composite questions (columns)
(defmethod display-question 1
  [{:keys [question explanation hint key qtype id ordnen] :as q}]
  (let [counter (r/atom 0)]
    (fn [{:keys [question explanation hint qtype id ordnen] :as q}]
    [:div
     [input-new-answer {:question-id id :on-stop #(js/console.log "stop") :props {:placeholder "Neue antwort"}}]
     (when-not (nil? (:answers q))
       (for [answer (:answers q)]
         [display-answer (assoc (second answer) :key (swap! counter inc))]))])))

(defmethod display-question 2
  [question]
  [:p "(Dies ist eine offene Frage)"])

(defmethod display-question 3
  [question]
    [fulfill-question-form question])

(defmethod display-question 4
  [question]
  [:p "Question columns"])

(def question-counter (r/atom 0))

(defn question-item
  "Display any type of question"
  [{:keys [question explanation hint qtype id ordnen points] :as q}]
  (let [editing-question (r/atom false)]
    (fn []
      [:div.question-container-div
       [:div.question-items-divs
        [:img.img-float-right {:title    "Frage nachbestellen"
                               :alt      "Frage nachbestellen"
                               :src      "/img/icon_up_green.png"
                               :on-click #(rf/dispatch [:reorder-question {:question-id id :send "up"}])}]
       [:img.img-float-right {:title    "Senden Sie nach unten"
                              :alt      "Senden Sie nach unten"
                              :src      "/img/icon_down_green.png"
                              :on-click #(rf/dispatch [:reorder-question {:question-id id :send "down"}])}]

        (if @editing-question
          [:img.img-float-right {:title    "Frage abbrechen"
                                 :alt      "Frage abbrechen"
                                 :src      "/img/icon_cancel.png"
                                 :on-click #(swap! editing-question not)}]
          [:img.img-float-right {:title    "Frage bearbeiten"
                                 :alt      "Frage bearbeiten"
                                 :src      "/img/icon_edit.png"
                                 :on-click #(swap! editing-question not)}])]  ;; editing ends
     [:div.question-items-divs
      [:div [:span.bold-font (str (swap! question-counter inc) ".- Frage: ")] question  "   ordnen:" ordnen "   question id:" id]
      [:div [:span.bold-font "Hint: "] hint]
      [:div [:span.bold-font "Points: "] points]
      [:div [:span.bold-font "Erläuterung: "] explanation]]
     (when @editing-question
        [edit-question q])
       [display-question q] ;; Polimorphysm for the kind of question
     [:div.question-items-divs
       [:img {:src    "/img/icon_delete.png"
              :title  "Frage löschen"
              :alt    "Frage löschen"
              :on-click #(rf/dispatch [:delete-question id])}]]])))

(defn questions-list
  []
  (let [counter (r/atom 1)]
    (fn []
      (reset! question-counter 0)
      [:section
       (doall (for [question @(rf/subscribe [:questions])]
                ^{:key (swap! counter inc)} [question-item (second question)]))])))

(defn test-editor-form [test title description tags subject-id]
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
        (for [row-subject @(rf/subscribe [:subjects])]
          ^{:key (:id row-subject)} [:option {:value (:id row-subject)} (:subject row-subject)])
        ]]
      [:div
       [:input {:class "btn btn-outline-primary-green" :type "button" :value "Speichern"
                :on-click #(rf/dispatch [:update-test {:title @title
                                                       :description @description
                                                       :tags @tags
                                                       :subject_id @subject-id
                                                       :test_id (:id test)}])}]]]
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
        qtype        (r/atom "1")
        points       (r/atom "1")]
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
        [:select.form-control.mr-sm-2 {:name      "points"
                                       :value     @points
                                       :on-change #(reset! points (-> % .-target .-value))}
         (for [pvalue (range 1 6)]
           ^{:key pvalue} [:option {:value pvalue} pvalue])]]
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
                                                                  :points      @points
                                                                  :explanation @explanation
                                                                  :test-id     (.-value (gdom/getElement "test-id"))
                                                                  :user-id     (.-value (gdom/getElement "user-id"))}])
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
