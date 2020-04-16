(ns zentaur.libs.models.search
  (:require [clojure.tools.logging :as log]
            [zentaur.db.core :as db]
            [zentaur.libs.models.shared :as sh]))


(defn search_pgsql
     "Full text searching in PostgreSQL is based on the match operator @@, which returns true if a tsvector (document) matches a tsquery (query)
      To present search results it is ideal to show a part of each document and how it is related to the query. Usually, search engines show fragments of the document with
      marked search terms. PostgreSQL provides a function ts_headline that implements this functionality."
  [terms lang]
  (let [models ["lesson_plans" "questions"]]
    "(db/search-all term lang)"
    ))
