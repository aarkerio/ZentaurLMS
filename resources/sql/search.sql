/****
  HugSQL is a Clojure library for embracing SQL.
  Structure: :name :command :result
  Type of commands
     :?  = fetch (query)
     :!  = execute (statetment like INSERT) RETURNING DOES N0T WORK WITH THIS
     :<! = returning-execute, for INSERT or DELETE with RETURNING
  Type of results:
    :* = vectors [1, 3, 4]
    :affected or :n = number of rows affected (inserted/updated/deleted)
    :1 = one row
    :raw = passthrough an untouched result (default)
***/

-- :name search-english-questions :? :*
-- :doc search through several tables.
SELECT id, question, hint FROM
(SELECT id, question, hint, ts_rank(tsv_en, q) AS rank, q FROM questions, plainto_tsquery('lacinia morbi') AS q WHERE tsv_en @@ q ORDER BY rank DESC LIMIT 5) p ORDER BY rank DESC

-- :name search-all-queries :? :*
-- :doc search through questions table.
SELECT id, question, subject_id, level_id, lang_id FROM questions WHERE subject_id = :value:subjects.0.id AND level_id = :value:levels.0.id AND lang_id = :value:langs.0.id


