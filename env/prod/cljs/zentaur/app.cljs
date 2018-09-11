(ns zentaur.app
  (:require [zentaur.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
