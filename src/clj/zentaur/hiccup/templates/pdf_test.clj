(ns zentaur.hiccup.templates.pdf-test
  (:use clj-pdf.core))

(def stylesheet
  {:foo {:color [255 0 0]
         :family :helvetica}
   :bar {:color [0 0 255]
         :family :helvetica}
   :baz {:align :right}})

(defn create-pdf
  "Creates PDF file"
  [test]
(pdf
  [{:stylesheet stylesheet}
   [:list {:roman true}
          [:chunk {:style :bold} "a bold item"]
          "another item"
          "yet another item"]
   [:phrase "some text"]
   [:phrase "some more text"]
   [:paragraph "yet more text"]]
  "doc.pdf")
  )
