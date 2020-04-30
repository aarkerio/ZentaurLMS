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

-- :name search-all :? :*
-- :doc search through several tables.
SELECT id, user_id, title, ts_headline('zentaur_"lang"', body, to_tsquery('zentaur_ :lang ','" :term "')) AS headline,
rank, username FROM (
SELECT DISTINCT "u"."username","q"."id","q"."user_id","q"."question", "q"."question",
ts_rank_cd(to_tsvector('zentaur_ :lang ', body), to_tsquery('zentaur_es',' :term ')) AS rank
FROM questions AS q, users AS u
WHERE to_tsquery('zentaur_ :lang ',' :term') @@ to_tsvector('zentaur_ :lang ', q.question)
AND u.id=q.user_id AND q.status = 1 ORDER BY rank DESC LIMIT 20) AS questions)


-- :name search-all-queries :? :*
-- :doc search through questions table.
SELECT id, question, subject_id, level_id, lang_id FROM questions WHERE subject_id = :value:subjects.0.id AND level_id = :value:levels.0.id AND lang_id = :value:langs.0.id


