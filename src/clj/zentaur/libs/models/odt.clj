(ns zentaur.libs.models.odt
  (:require [clojure.tools.logging :as log])
  (:import java.net.URI
           org.odftoolkit.simple.TextDocument
           org.odftoolkit.simple.table.Cell
           org.odftoolkit.simple.table.Table
           org.odftoolkit.simple.text.list.List))

(defmacro template [t]
  `(fn [~'items]
     (for [~'item ~'items]
       ~(clojure.walk/prewalk
          (fn [x#]
            (if (and (symbol? x#) (.startsWith (name x#) "$"))
              `(~(keyword (.substring (name x#) 1)) ~'item)
              x#))
          t))))

(def questions [{:idx "20" :question "Question  the dev-front build to :optimizations none."
                :qtype 1 :hint "Convert the dev-front build to :optimizations none."
                :explanation "One explanation One explanation One explanation One explanation "
                :points 3} {:idx "23" :question "Question the dev-front build to :optimizations none."
                :qtype 2 :hint "Convert the dev-front build to optimizations none."
                :explanation "two explanation two explanation two explanation two explanation."
                :points 6}])


(defn answer-template [answer idx]
  (let [idx+ (inc idx)]
    [:paragraph (str idx+ ").- [  ] ") (:answer answer)]))

(def questions-template
  (template
    [:paragraph
     [:paragraph [:chunk {:style :bold} (str $idx ").- ")] $question]
     [:chunk {:style :italic :color [227 227 227]} "points: "] $points ".  "
     [:chunk {:style :italic :color [227 227 227]} "hint: "] $hint "\n"
     [:chunk {:style :italic :color [227 227 227]} "qtype: "] $qtype "\n"
     $content
     [:spacer]]))

(defn build-questions [one-question idx]
  (let [idx+     (inc idx)
        qtype    (:qtype one-question)
        content  (cond
                   (= qtype 1) (into [:paragraph] (map-indexed (fn [idx itm] (answer-template itm idx)) (:answers one-question)))
                   (= qtype 2) [:paragraph "____________________________________________________________________________________"]
                   (= qtype 3) [:paragraph "fulfill"])]
  (assoc one-question :content content :idx idx+)))

(defn generate-odt [filename test]
  (let [questions  (map-indexed (fn [idx itm] (build-questions itm idx) ) (:questions test))
        qtpl       (questions-template questions)
        _          (log/info (str ">>> **** qtpl  qtpl ***** >>>>> " (apply prn qtpl)))
        outputOdt  (TextDocument/newTextDocument)
        uri        (URI. "/home/manuel/Documents/lisplogo_fancy_256.png")
        items      (into-array String ["Primer Asunto" "Segundo punto" "Tercer negocio" "Too long list"])]
    (try
      (.newImage outputOdt uri)
      (.addParagraph outputOdt "Hallo tolle und grüne Welt!, taradazo Hello Simple ODF!")
      (.addParagraph outputOdt "Hello World, Hallo Welt taradazo Hello Simple ODF AFTER IMAGE!")
      (.addParagraph outputOdt "The following element is a new list.")

      ;; doto macro, first argument is passed to all the next nested forms
      (doto (.addList outputOdt)
        (.addItems items))

      ;; ".." threading macro, Expands into a member access (.) of the first member on the first
      ;; argument, followed by the next member on the result, etc.
      (.. outputOdt
         (addTable 2 2)
         (getCellByPosition 0 1)
         (setStringValue "I'm in some cell table!"))

      (.save outputOdt filename)
      (catch Exception e (str "ERROR: unable to create output file: " (.getMessage e))))))
