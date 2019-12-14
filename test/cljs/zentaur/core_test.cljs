(ns zentaur.core-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures run-tests]]
            [zentaur.core :as rc]
            [zentaur.reframe.tests.libs :as lib]))

(enable-console-print!)

;; (use-fixtures :once
;;   {:before (fn [] (into [] '(1 2)))
;;    :after  (fn [] ...)})

(deftest test-home
  (is (= true true)))

(def questions [{:qid 38, :full-question {:explanation "So I'm just beginning 've been trying to make ", :ordnen 1, :reviewed_fact false, :question "So I'm  re-  simple link ", :points 1, :hint "So I'm just beginning to learn  and I've been trying to ", :qtype 1, :updated_at nil, :reviewed_cr false, :active true, :id 38, :answers [], :user_id 1, :created_at "13/12/2019", :reviewed_lang false}}
                {:qid 39, :full-question {:explanation "This panel event handler.", :ordnen 2, :reviewed_fact false, :question "This panel allows you to", :points 1, :hint "This  your event handler.", :qtype 1, :updated_at nil, :reviewed_cr false, :active true, :id 39, :answers [], :user_id 1, :created_at "13/12/2019", :reviewed_lang false}}
                {:qid 42, :full-question {:explanation "So I'm just beginning 've been trying ", :ordnen 1, :reviewed_fact false, :question "So I'm  resimple link ", :points 1, :hint "So I'm just beginning to learn  and I've  ", :qtype 1, :updated_at nil, :reviewed_cr false, :active true, :id 42, :answers [], :user_id 1, :created_at "13/12/2019", :reviewed_lang false}} ])

(deftest test-index-by-qid
  (let [index (libs/index-by-qid questions question-id)]
    (is (= index 1))))

(run-tests)

;; (run-tests (cljs-test-display.core/init! "app-testing")
;;            'example.core-test
;;            'example.other-test)
