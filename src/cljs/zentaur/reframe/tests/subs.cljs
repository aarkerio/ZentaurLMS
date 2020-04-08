(ns zentaur.reframe.tests.subs ^{:doc "Re-frame Subscriptions"}
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :test
 (fn [db]
   (:test db)))

(rf/reg-sub
 :questions
 (fn [db]
   (get-in db [:questions])))

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

