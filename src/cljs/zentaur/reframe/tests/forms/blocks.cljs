(ns zentaur.reframe.tests.forms.blocks
  (:require [goog.dom :as gdom]
            [reagent.core  :as reagent]
            [re-frame.core :as re-frame]))

(defn edit-test-form
  "Verstecken Form for test bearbeiten"
  []
  (let [qform        (re-frame/subscribe [:toggle-testform])
        new-question (reagent/atom "")
        hint         (reagent/atom "")
        explanation  (reagent/atom "")
        qtype        (reagent/atom "1")]
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
         [:option {:value "4"} "Columns"]]]
     [:div
      [:input.btn {:type "button" :value "Neue Frage speichern"
                   :on-click #(do (re-frame.core/dispatch [:create-question {:question    @new-question
                                                                             :hint        @hint
                                                                             :qtype       @qtype
                                                                             :test-id     (.-value (gdom/getElement "test-id"))
                                                                             :user-id     (.-value (gdom/getElement "user-id"))
                                                                             :explanation @explanation}])
                                  (reset! new-question "")
                                  (reset! hint "")
                                  (reset! explanation ""))}]]])))


