(ns zentaur.libs.models.odt
  (:require [clojure.tools.logging :as log])
  (:import java.net.URI
           org.odftoolkit.simple.TextDocument
           org.odftoolkit.simple.table.Cell
           org.odftoolkit.simple.table.Table
           org.odftoolkit.simple.text.list.List
           org.odftoolkit.odfdom.dom.style.OdfStyleFamily
           org.odftoolkit.odfdom.dom.style.props.OdfParagraphProperties
           org.odftoolkit.odfdom.dom.style.props.OdfTextProperties
           org.odftoolkit.odfdom.incubator.doc.text.OdfTextHeading))

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
        _          (.setProperty negro (OdfTextProperties/Color) "#a85a32")
        _          (.setProperty negro (OdfParagraphProperties/TextAlign) "center")
        nodoFormateado (.getContentRoot outputOdt)   ;; Obtenemos el inicio de nuestro parrafo
        textoAFormatear (.getContentDom outputOdt)   ;; Creamos el objeto que almacenará nuestro Contenido.
        uri        (URI. "resources/public/img/quiz-logo.png")
        _          (log/info (str ">>> style *************** style style NEGRO  kkkk TYPE >>>>> " (type negro) ">>>>>>> ALL " negro))]
    (try
      (.setStyleName (.appendChild nodoFormateado (new OdfTextHeading textoAFormatear "negro" "PRUEBA ESTILOS")) "negro")
      (.appendChild nodoFormateado (.addStyledContent (new OdfTextHeading textoAFormatear "negro" "dsfdsfdsf") "negro" "PRUEBA GGGGGGGGGG Wagner Tanhauser ESTILOS"))

      (.setStyleName (.getOdfElement (.addParagraph outputOdt "México 68 ------- asdasdasdasd KKKKKK")) "negro")
      (.addParagraph outputOdt "")
      (.setStyleName (.addParagraph outputOdt "asdasdasdasd") "negro")

      (.addParagraph outputOdt (.newImage outputOdt uri))

      (.addParagraph outputOdt (:title test))

      (doseq [q questions]
        (let [question (:question q)
              qtype    (:qtype question)]
          (log/info (str ">>>  questionquestionquestion ****  >>>>> " question  " qtype >>>>> " qtype))
          (.addParagraph outputOdt (str (:idx q) ").- " (:question question)))
          (cond
            (= qtype 1) (doall (map-indexed (fn [idx answer] (.addParagraph outputOdt (str (inc idx) ").- [  ] " (:answer answer)))) (:answers question)))
            (= qtype 2) (.addParagraph outputOdt (clojure.string/join (take 80 (repeat "_"))))
            (= qtype 3) (.addParagraph outputOdt "fulfill"))
          (.addParagraph outputOdt "")))

      (.save outputOdt filename)
      (catch Exception e (str "ERROR: unable to create output file: " (.getMessage e))))))
