(ns ^:test-model zentaur.models.exports
  "Business logic for the export section"
  (:require [clj-pdf.core :as pdf]
            [clojure.tools.logging :as log]
            [crypto.random :as cr]
            [zentaur.db.core :as db]
            [zentaur.models.tests :as mt]))

(defn answer-template [answer]
  [:paragraph [:chunk {:style :bold} "[ ] "] (:answer answer)])

(def questions-template
  (pdf/template
    [:paragraph
     [:paragraph (str $idx ").- " $question)]
     [:chunk {:style :italic :color [227 227 227]} "points: "] $points ".  "
     [:chunk {:style :italic :color [227 227 227]} "hint: "] $hint "\n"
     [:chunk {:style :italic :color [227 227 227]} "qtype: "] $qtype "\n"

     $content
     [:spacer]]))

(defn indexado [coll]
  (map-indexed (fn [idx itm] (assoc itm :idx (inc idx))) coll))

(defn build-questions [one-question]
  (let [qtype    (:qtype one-question)
        content  (if (= qtype 1)
                   (into [:paragraph] (map #(answer-template %) (:answers one-question)))
                   [:paragraph "____________________________________________________________________________________"])]
  (assoc one-question :content content)))

(defn to-pdf [file-name test]
  (let [counter     (atom 0)
        subject     (:subject test)
        title       (:title test)
        description (:description test)
        tags        (:tags test)
        questions   (map build-questions (:questions test))
        qtpl        (questions-template questions)]
    (pdf/pdf
     [{}
      [:image {:align :left} "resources/public/img/warning_clojure.png"]
      [:heading {:style {:size 14 :color [66 135 245] :align :left}} title]
      [:line {:gap 10 :color [66 135 245]}]
      [:chunk {:style :bold :size 10} "Description: "] description
      [:chunk {:style :bold :size 10} "Subject: "] subject
      [:chunk {:style :bold :size 10} "Tags: "] tags
      [:spacer]
      qtpl
      [:spacer]
      [:spacer]
      [:paragraph "Good luck! ;-)"]]
     file-name)))

(defn export-pdf [uurlid]
  (let [test      (mt/build-test-structure uurlid false)
        _         (log/info (str ">>> export-pdfexport-pdf  TEST >>>>> " test))
        rand7     (cr/hex 7)
        title     (clojure.string/replace (:title test) #" " "_")
        ;; final-pdf (questions-template questions)
        file-name (str "resources/public/tmp/" title  "-" rand7 ".pdf")
        _         (to-pdf file-name test)]
    file-name))

;; (defn export-odf
;;   "Export to open document format"
;;   [uurlid]
;;   (let [test (db/get-one-test {:uurlid uurlid :active true})
;;     (db/remove-test! {:test-id test-id}) ))
