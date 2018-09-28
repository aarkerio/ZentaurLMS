(ns zentaur.views
  (:require [reagent.core  :as reagent]
            [re-frame.core :as reframe]
            [clojure.string :as str]))

(defn todo-input [{:keys [title on-save on-stop]}]
  (let [val  (reagent/atom title)
        stop #(do (reset! val "")
                  (when on-stop (on-stop)))
        save #(let [v (-> @val str str/trim)]
                (on-save v)
                (stop))]
    (fn [props]
      [:input (merge (dissoc props :on-save :on-stop :title)
                     {:type        "text"
                      :value       @val
                      :auto-focus  true
                      :on-blur     save
                      :on-change   #(reset! val (-> % .-target .-value))
                      :on-key-down #(case (.-which %)
                                      13 (save)
                                      27 (stop)
                                      nil)})])))

(defn todo-item
  []
  (let [editing (reagent/atom false)]
    (fn [{:keys [id done title]}]
      [:li {:class (str (when done "completed ")
                        (when @editing "editing"))}
        [:div.view
          [:input.toggle
            {:type "checkbox"
             :checked done
             :on-change #(reframe/dispatch [:toggle-done id])}]
          [:label
            {:on-double-click #(reset! editing true)}
            title]
          [:button.destroy
            {:on-click #(reframe/dispatch [:delete-todo id])}]]
        (when @editing
          [todo-input
            {:class "edit"
             :title title
             :on-save #(if (seq %)
                          (reframe/dispatch [:save id %])
                          (reframe/dispatch [:delete-todo id]))
             :on-stop #(reset! editing false)}])])))

(defn task-list
  []
  (let [visible-todos @(reframe/subscribe [:visible-todos])
        all-complete? @(reframe/subscribe [:all-complete?])]
      [:section#main
        [:input#toggle-all
          {:type "checkbox"
           :checked all-complete?
           :on-change #(reframe/dispatch [:complete-all-toggle])}]
        [:label
          {:for "toggle-all"}
          "Mark all as complete"]
        [:ul#todo-list
          (for [todo visible-todos]
            ^{:key (:id todo)} [todo-item todo])]]))

(defn footer-controls
  []
  (let [[active done] @(reframe/subscribe [:footer-counts])
        showing       @(reframe/subscribe [:showing])
        a-fn          (fn [filter-kw txt]
                        [:a {:class (when (= filter-kw showing) "selected")
                             :href (str "#/" (name filter-kw))} txt])]
    [:footer#footer
     [:span#todo-count
      [:strong active] " " (case active 1 "item" "items") " left"]
     [:ul#filters
      [:li (a-fn :all    "All")]
      [:li (a-fn :active "Active")]
      [:li (a-fn :done   "Completed")]]
     (when (pos? done)
       [:button#clear-completed {:on-click #(reframe/dispatch [:clear-completed])}
        "Clear completed"])]))

(defn task-entry
  []
  [:header#header
    [:h1 "Die dumme und stinkende Liste zu tun"]
    [todo-input
      {:id "new-todo"
       :placeholder "What needs to be done?"
       :on-save #(when (seq %)
                   (reframe/dispatch [:add-todo %]))}]])

;; ##### My shit  STARTS
(defn counter-control
  [value on-change]
  [:div.myclass
   [:input {:type "button"
            :value value
            :on-click (fn [event]
                        (reframe/dispatch [:count-update on-change]))}]])

(defn counter-display []
  (let [count (reframe/subscribe [:count])]
    [:div (str "Current count: " @count)]))    ;; vielleicht später:  @(subscribe [:count])    subscribe and dereference in one step

(defn test-display []
  (let [test (reframe/subscribe [:test])]
    [:div (str "Current test: " @test)]))      ;; vielleicht später:  @(subscribe [:count])    subscribe and dereference in one step

(defn counter []
  [:div
   [counter-display]
   [counter-control "+" inc]
   [counter-control "-" dec]]
   [test-display])
;; ##### My shit  ENDS

(defn todo-app
  []
  [:div
   [:section#todoapp
    [counter]
    [task-entry]
    (when (seq @(reframe/subscribe [:todos]))
      [task-list])
    [footer-controls]]
   [:footer#info
    [:p "Double-click to edit a todo"]]])
