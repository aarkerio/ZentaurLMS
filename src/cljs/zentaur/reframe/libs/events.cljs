(ns zentaur.reframe.libs.events
  (:require [clojure.string :as str]
            [goog.string :as gstring]
            [re-frame.core :as rf]
            [zentaur.reframe.libs.db :as zdb]))

(rf/reg-event-fx
 :initialise-db
 (fn [{:keys [db]} _]
   {:db zdb/default-db}))
