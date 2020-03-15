(ns zentaur.libs.models.odt
  (:require [clojure.tools.logging :as log])
  (:import java.net.URI
           org.odftoolkit.simple.TextDocument
           org.odftoolkit.simple.table.Cell
           org.odftoolkit.simple.table.Table
           org.odftoolkit.simple.text.list.List))

(defn answer-template [answer idx]
  (let [idx+ (inc idx)]
    (str idx+ ").- [  ] " (:answer answer) "\n")))

(defn build-questions [one-question idx]
  (let [idx+     (inc idx)
        qtype    (:qtype one-question)
        content  (cond
                   (= qtype 1) (into '() (map-indexed (fn [idx itm] (answer-template itm idx)) (:answers one-question)))
                   (= qtype 2) "____________________________________________________________________________________\n"
                   (= qtype 3) "fulfill \n")]
    (str idx+ ").- " (:question one-question) " \n " content "\n")))

(defn add-paragraph [outputOdt questions]
  (map (fn [q] (.addParagraph outputOdt q)) questions))

(defn generate-odt [filename test]
  (let [questions  (map-indexed (fn [idx itm] (build-questions itm idx) ) (:questions test))
        outputOdt  (TextDocument/newTextDocument)
        uri        (URI. "resources/public/img/quiz-logo.png")
        _          (log/info (str ">>>  questionsquestionsquestions 33333 >>>>> " (type questions)))]
    (try
      (.newImage outputOdt uri)
      (add-paragraph outputOdt questions)

      (.addParagraph outputOdt "Good luck :-)")

      (.save outputOdt filename)
      (catch Exception e (str "ERROR: unable to create output file: " (.getMessage e))))))
