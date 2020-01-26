(ns ^:test-model zentaur.models.exports
  "Business logic for the export section"
  (:require [clj-pdf.core :as pdf]
            [clojure.tools.logging :as log]
            [zentaur.db.core :as db]
            [zentaur.libs.helpers :as h]))

(def questions-template
  (pdf/template
    [:paragraph
     [:heading $question]
     [:chunk {:style :bold} "questions: "] $occupation "\n"
     [:chunk {:style :bold} "answers: "] $place "\n"
     [:chunk {:style :bold} "country: "] $country
     [:spacer]]))

(def answers-template
  (pdf/template
    [:paragraph
     [:heading $question]
     [:chunk {:style :bold} "questions: "] $occupation "\n"
     [:chunk {:style :bold} "answers: "] $place "\n"
     [:chunk {:style :bold} "country: "] $country
     [:spacer]]))

(defn to-pdf [file-name test]
  (let [subject (:subject test)
        title   (:title test)]
    (pdf/pdf
     [{:title title }
      [:list {:roman true}
       [:chunk {:style :bold} "Subject: " ]
       "another item "  subject
       "yet another item"]
      [:phrase "some text lo que sea PEDAZO DE PENDEJO"]
      [:phrase "some more text África {ñóñá}"]
      [:paragraph "yet more text"]]
     file-name)))

(defn export-pdf [test-id user-id]
  (let [test      (db/get-one-test {:id test-id :user-id user-id})
        _         (log/info (str ">>>  TEST >>>>> " test))
        rand7     (crypto.random/hex 7)
        title     (clojure.string/replace (:title test) #" " "_")
        ;; final-pdf (questions-template questions)
        file-name (str "resources/public/tmp/" title  "-" rand7 ".pdf")
        _         (to-pdf file-name test)]
    file-name))

;; (defn export-odf
;;   "Export to open document format"
;;   [test-id]
;;   (let [test-id (inc test-id)]
;;     (db/remove-test! {:test-id test-id})))
