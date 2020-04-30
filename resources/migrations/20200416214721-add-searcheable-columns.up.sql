-- ;; lein migratus create add-searchable-columns
-- ;; lein run migrate
-- ;; lein with-profile test run migrate
-- ;; Only available in PostgreSQL 12
-- Español
-- Three steps to create Text Full Search
-- This create a dictionary to create several kind of tokens : emails, number, lexems
-- lexems are created according language
CREATE TEXT SEARCH DICTIONARY zentaur_es (
    template  = snowball,
    language  = spanish,
    stopwords = spanish
);

-- A text search configuration specifies a text search parser that can divide a string into tokens,
-- plus dictionaries that can be used to determine which tokens are of interest for searching.
-- See: http://developer.postgresql.org/pgdocs/postgres/textsearch-configuration.html
CREATE TEXT SEARCH CONFIGURATION public.zentaur_es ( COPY = pg_catalog.spanish );

-- FTS (Full Text Search) index = Storing preprocessed documents optimized for searching
-- Also allow create ranked searchs
-- A data type "tsvector" is provided for storing preprocessed documents
-- For text search purposes, each document (text field) must be reduced to the preprocessed tsvector format.
-- CREATE INDEX pgnews_idx  ON questions    USING gin(to_tsvector('zentaur_es', body));
-- CREATE INDEX pgentr_idx  ON lesson_plans USING gin(to_tsvector('zentaur_es', body));

-- English
-- CREATE TEXT SEARCH DICTIONARY zentaur_en (
--     template  = snowball,
--     language  = english,
--     stopwords = english
-- );

-- CREATE TEXT SEARCH CONFIGURATION public.zentaur_en ( COPY = pg_catalog.english );

-- CREATE INDEX pgnews_idx ON questions USING gin(to_tsvector('zentaur_en', question));
-- CREATE INDEX pgless_idx ON lesson_plans USING gin(to_tsvector('zentaur_en', body));
-- German (ToDo)
--CREATE TEXT SEARCH DICTIONARY zentaur_de (
--    template  = snowball,
--    language  = german,
--    stopwords = german
--);
