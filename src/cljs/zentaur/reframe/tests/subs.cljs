(ns zentaur.reframe.tests.subs ^{:doc "Zentaur Re-frame Subscriptions"}
  (:require [re-frame.core :as rf]
            [zentaur.reframe.libs.commons :as cms]))

(rf/reg-sub
 :test
 (fn [db]
   (:test db)))

;; In the bussines logic the questions and nestes answers doesn't matter
;; but in the view they must be ordered using the ":ordnen" keyword
(rf/reg-sub
 :questions
 (fn [db]
   (let [questions (cms/vector-to-ordered-idxmap (get-in db [:questions]))]
     (map #(update % :answers cms/vector-to-ordered-idxmap) questions))))

(rf/reg-sub
 :subjects
 (fn [db]
   (get-in db [:subjects])))

(rf/reg-sub
 :levels
 (fn [db]
   (get-in db [:levels])))

(rf/reg-sub
 :langs
 (fn [db]
   (get-in db [:langs])))

(rf/reg-sub
 :qform
 (fn [db]
   (get-in db [:qform])))

(rf/reg-sub
 :toggle-testform
 (fn [db]
   (get-in db [:testform])))

(rf/reg-sub
  :question-count
  (fn [_]
    (rf/subscribe [:questions]))
  (fn [questions]
    (count questions)))

(rf/reg-sub
  :test-uurlid
  (fn [_]
    (rf/subscribe [:test]))
  (fn [test]
    (:uurlid test)))

(rf/reg-sub
  :test-user-id
  (fn [_]
    (rf/subscribe [:test]))
  (fn [test]
    (:user_id test)))

(rf/reg-sub
 :comments
 (fn [db]
   (get-in db [:comments])))

(rf/reg-sub
 :search-terms
 (fn [db]
   (get-in db [:search-terms])))

;; Quotes section
(rf/reg-sub
 :quotes
 (fn [db]
   (cms/order-map (get-in db [:quotes]))))
