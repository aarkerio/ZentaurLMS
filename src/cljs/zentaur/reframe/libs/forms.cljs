(ns zentaur.reframe.libs.forms
  (:require [cljs.spec.alpha :as s]
            [re-frame.core :as rf]))

(def root-db-path [::forms])
(def value-db-path (conj root-db-path ::value))

(rf/reg-sub
  ::values
  (fn [db]
    (get-in db value-db-path)))

(rf/reg-sub
  ::field-value
  :<- [::values]
  (fn [forms-data [_ form-id field-path :as glo]]
    (.log js/console (str ">>> GLO >>>>> " glo  ))
    (get-in forms-data (vec (cons form-id field-path)))))

(rf/reg-event-db
  ::set-field-value
  (fn [db [_ form-id field-path new-value]]
    (assoc-in db (vec (concat value-db-path (cons form-id field-path))) new-value)))


