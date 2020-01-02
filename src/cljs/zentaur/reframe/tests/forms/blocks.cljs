(ns zentaur.reframe.tests.forms.blocks
  (:require [goog.dom :as gdom]
            [reagent.core  :as reagent]
            [re-frame.core :as re-frame]))

(defn edit-test-form
  "Verstecken Form for test bearbeiten"
  []
  (let [test-form     (re-frame/subscribe [:toggle-testform])
        title         (atom "")
        description   (atom "")
        tags          (atom "")]
    (fn []
      [:div {:id "hidden-form" :class (if @test-form "visible-div" "hidden-div")}
       [:h3.class "Bearbeit test"]
       [:div.div-separator
        [:input {:type         "text" :value @title
                 :placeholder  "Title"
                 :title        "Title"
                 :maxLength    180
                 :on-change    #(reset! title (-> % .-target .-value))
                 :size         100}]]
       [:div.div-separator
        [:input {:type         "text"
                 :value        @description
                 :on-change    #(reset! description (-> % .-target .-value))
                 :placeholder  "Erklärung"
                 :title        "Erklärung"
                 :maxLength    180
                 :size         100}]]
       [:div.div-separator
        [:input {:type         "text"
                 :value        @tags
                 :on-change    #(reset! tags (-> % .-target .-value))
                 :placeholder  "Tags"
                 :title        "Tags"
                 :maxLength    180
                 :size         100}]]
     [:div
      [:input {:class "btn btn-outline-primary-green" :type "button" :value "Speichern"
               :on-click (re-frame.core/dispatch [:update-test {:title        @title
                                                                :description  @description
                                                                :tags         @tags
                                                                :test-id      (.-value (gdom/getElement "test-id"))
                                                                :user-id      (.-value (gdom/getElement "user-id"))}])} ]]])))


