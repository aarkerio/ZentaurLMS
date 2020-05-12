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
SELECT id, question, hint, subject_id, lang_id, level_id FROM
    (SELECT id, question, hint, subject_id, lang_id, level_id, ts_rank(tsv_en, q) AS rank, q FROM questions, plainto_tsquery('lacinia morbi') AS q
    WHERE tsv_en @@ q
    AND subject_id =  ANY (ARRAY[1, 2, 10, 14])
    AND lang_id =  ANY (ARRAY[1, 2])
    AND level_id =  ANY (ARRAY[1, 7, 9])
    ORDER BY rank DESC LIMIT :limit)
p ORDER BY rank DESC;

-- :name search-langs-questions :? :*
-- :doc search through several tables.
SELECT id, question, hint, subject_id, lang_id, level_id FROM
    (SELECT id, question, hint, subject_id, lang_id, level_id, ts_rank(tsv_en, q) AS rank, q FROM questions, plainto_tsquery(:terms) AS q
    WHERE tsv_en @@ q
/*~
(str "AND subject_id =  ANY (ARRAY[" (clojure.string/join ", " (:subjects params)) "])"
     "AND level_id   =  ANY (ARRAY[" (clojure.string/join ", " (:levels   params)) "])"
     "AND lang_id    =  ANY (ARRAY[" (clojure.string/join ", " (:langs    params)) "])")
~*/
ORDER BY rank DESC LIMIT 5)
p ORDER BY rank DESC;


