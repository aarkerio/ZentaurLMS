(ns zentaur.libs.models.odt
  (:require [clojure.tools.logging :as log])
  (:import java.net.URI
           org.odftoolkit.simple.TextDocument
           org.odftoolkit.simple.table.Cell
           org.odftoolkit.simple.table.Table
           org.odftoolkit.simple.text.list.List
           org.odftoolkit.odfdom.dom.style.OdfStyleFamily
           org.odftoolkit.odfdom.dom.style.props.OdfParagraphProperties
           org.odftoolkit.odfdom.dom.style.props.OdfTextProperties))

(defn answer-template [answer idx]
  (let [idx+ (inc idx)]
    (str idx+ ").- [  ] " (:answer answer))))

(defn build-questions [one-question idx]
  (let [idx+     (inc idx)
        qtype    (:qtype one-question)
        content  (cond
                   (= qtype 1) (into '() (map-indexed (fn [idx itm] (answer-template itm idx)) (:answers one-question)))
                   (= qtype 2) "____________________________________________________________________________________"
                   (= qtype 3) "fulfill")]
    (str idx+ ").- " (:question one-question) " \n " content "\n")))

(defn generate-odt [filename test]
  (let [questions  (map-indexed (fn [idx itm] (assoc {} :idx (inc idx) :question itm)) (:questions test))
        outputOdt  (TextDocument/newTextDocument)
        styles     (.getOrCreateDocumentStyles outputOdt)
        _          (log/info (str ">>> styles stylesstyles TYPE >>>>> " (type styles)))
        negro      (.newStyle styles "negro" (OdfStyleFamily/Paragraph))
        _          (.setStyleDisplayNameAttribute negro "negro")
        _          (.setProperty negro (OdfTextProperties/FontWeight) "bold")
        _          (.setProperty negro (OdfTextProperties/FontSize) "14pt")
        _          (.setProperty negro (OdfTextProperties/Color) "#a85a32")]
    (try
      (.addParagraph outputOdt "Date: __ /__ / ____")
      (.addParagraph outputOdt "")
      (.setStyleName (.getOdfElement (.addParagraph outputOdt (:title test))) "negro")
      (.addParagraph outputOdt "")
      (doseq [q questions]
        (let [question (:question q)
              qtype    (:qtype question)]
          (.addParagraph outputOdt (str (:idx q) ").- " (:question question)))
          (cond
            (= qtype 1) (doall (map-indexed (fn [idx answer] (.addParagraph outputOdt (str (inc idx) ").- [  ] " (:answer answer)))) (:answers question)))
            (= qtype 2) (.addParagraph outputOdt (clojure.string/join (take 80 (repeat "_"))))
            (= qtype 3) (.addParagraph outputOdt "fulfill"))
          (.addParagraph outputOdt "")))

      (.save outputOdt filename)
      (catch Exception e (str "ERROR: unable to create output file: " (.getMessage e))))))
