(ns zentaur.views
  (:require [clojure.string :as str]
            [goog.dom :as gdom]
            [reagent.core  :as reagent]
            [re-frame.core :as reframe]))

(def api-url "https://conduit.productionready.io/api")

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
(defn question-item
  []
  (let [editing (reagent/atom false)]
    (fn [{:keys [id done title]}]
      [:li {:class (str (when done "completed")
                        (when @editing "editing"))}
       [:div.view
        [:label
         {:on-double-click #(reset! editing true)}
         title]
        [:button.destroy
         {:on-click #(reframe/dispatch [:delete-question id])}]]
       (when @editing
         [question-input
          {:class "edit"
           :title title
           :on-save #(if (seq %)
                       (reframe/dispatch [:save-question id %])
                       (reframe/dispatch [:delete-question id]))
           :on-stop #(reset! editing false)}])])))

(defn questions-list
  []
  (let [questions @(reframe/subscribe [:questions])
        all-complete? @(reframe/subscribe [:all-complete?])]
    [:section#main
     [:label
      {:for "toggle-all"}
      "Mark all as complete"]
     [:ul#questions-list
      (for [question questions]
        ^{:key (:id question)} [question-item question])]]))

(defn question-entry
  []
  (let [test (reframe/subscribe [:test])]
    [:div.hidden-div {:id "hidden-form"}
      [:h3.class "Hinzifugen neue fragen"]
     [:div.div-separator {:id "question-title-div" :key "question-title-div"}
      [question-input
       {:id          "new-question"
        :placeholder "Neue frage"
        :defaultValue ""
        :size        100
        :maxLength   180}]]

     [:div.div-separator {:id "question-hint-div" :key "question-hint-div"}
      [:input {:type         "text"
               :defaultValue ""
               :id           "question-hint"
               :key          "question-hint"
               :placeholder  "Question hint"
               :title        "Question hint"
               :maxLength    180
               :size         100}]]
     [:div.div-separator {:id "question-description-div" :key "question-description-div"}
      [:input {:type         "text"
               :defaultValue ""
               :id           "question-description"
               :key          "question-description"
               :placeholder  "Question description"
               :title        "Question description"
               :maxLength    180
               :size         100}]]
     [:div.div-separator {:id "question-qtype-div" :key "question-qtype-div"}
      [:select.form-control.mr-sm-2 {:name "qtype" :id "qtype-select"}
       [:option {:value "1"} "Multiple"]
       [:option {:value "2"} "Columns"]
       [:option {:value "2"} "Single"]]]
     [:div
      [:input.btn {:type "button" :value "Save new question"
                   :on-click #(re-frame.core/dispatch [:save-question {:question    (.-value (gdom/getElement "new-question"))
                                                                       :hint        (.-value (gdom/getElement "question-hint"))
                                                                       :qtype       (.-value (gdom/getElement "qtype-select"))
                                                                       :test-id     (:id @test)
                                                                       :description (.-value (gdom/getElement "question-description"))}])}]]]))

(defn test-display []
  (let [test (reframe/subscribe [:test])]
    [:div
     [:h1 (:title @test)]
     [:div.someclass (str "tags: " (:tags @test) "    created: " (:created_at @test)) ]
     (str "Current test: " @test)
     [:div [:img {:src "/img/icon_add.png" :alt "Fragen hinzüfugen" :title "Fragen hinzüfugen" :id "button-show-div"}]]]))

;; ##### My shit  ENDS

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
