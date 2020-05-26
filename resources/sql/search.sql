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
    (SELECT id, question, hint, qtype, subject_id, lang_id, level_id, ts_rank(tsv_en, q) AS rank, q FROM questions, plainto_tsquery('lacinia morbi') AS q
    WHERE tsv_en @@ q
    AND subject_id =  ANY (ARRAY[1, 2, 10, 14])
    AND lang_id =  ANY (ARRAY[1, 2])
    AND level_id =  ANY (ARRAY[1, 7, 9])
    ORDER BY rank DESC LIMIT :limit)
p ORDER BY rank DESC;

-- :name full-search-questions :? :*
-- :doc search through several tables.
SELECT id, question, hint, qtype, subject_id, lang_id, level_id FROM
    (SELECT id, question, qtype, hint, subject_id, lang_id, level_id, ts_rank(tsv_en, q) AS rank, q FROM questions, plainto_tsquery(:terms) AS q
    WHERE tsv_en @@ q
/*~
(str " OR subject_id =  ANY (ARRAY[" (clojure.string/join ", " (:subjects params)) "])"
     " OR level_id   =  ANY (ARRAY[" (clojure.string/join ", " (:levels   params)) "])"
     " OR lang_id    =  ANY (ARRAY[" (clojure.string/join ", " (:langs    params)) "])")
~*/
ORDER BY rank DESC)
p ORDER BY rank DESC  OFFSET :offset LIMIT :limit


/******* QUOTES ****/

-- :name get-one-random-quote :? :1
-- :doc retrieve a random quote.
SELECT * FROM	quotes OFFSET floor(random() * (SELECT COUNT(*)	FROM quotes)) LIMIT 1

-- :name get-quotes :? :*
-- :doc retrieve array quotes.
SELECT q.id, q.author, q.quote, (SELECT COUNT(*) FROM quotes) AS total
FROM quotes AS q
ORDER BY q.id DESC OFFSET :offset LIMIT :limit

-- :name get-quote :? :1
-- :doc retrieve one quote by its id.
SELECT q.id, q.author, q.quote, (SELECT COUNT(*) FROM quotes) AS total
FROM quotes AS q WHERE q.id = :id

-- :name create-quote :<! :1
-- :doc creates a new quote record
INSERT INTO quotes (author, quote) VALUES (:author, :quote) RETURNING id

-- :name update-quote :<! :1
-- :doc update an existing quote record
UPDATE quotes SET quote = :quote, author = :author WHERE id = :id RETURNING *

-- :name delete-quote :<! :1
-- :doc delete a user given the id
DELETE FROM quotes WHERE id = :id RETURNING id
