-- ;; lein migratus create create-questions-table
-- ;; lein run migrate
CREATE TABLE questions(
  id serial PRIMARY KEY,
  user_id int NOT NULL REFERENCES users(id),
  question text NOT NULL,
  qtype int NOT NULL DEFAULT 1,  -- qtype 1: multiple option, 2: open, 3: fullfill, 4: composite questions
  hint varchar(300),
  points smallint NOT NULL DEFAULT 1,
  explanation text,
  active BOOLEAN NOT NULL DEFAULT false,
  reviewed_lang BOOLEAN NOT NULL DEFAULT false,
  reviewed_fact BOOLEAN NOT NULL DEFAULT false,
  reviewed_cr BOOLEAN NOT NULL DEFAULT false,    -- reviewed copyright
  created_at timestamp(0) with time zone NOT NULL DEFAULT now(),
  updated_at timestamp(0) with time zone
 );
--;;
ALTER TABLE questions ADD CHECK (qtype IN (1,2,3,4));
--;;
COMMENT on column questions.qtype is '1: multiple option, 2: open, 3: fullfill, 4: composite questions (columns)';

-- ;; INSERT INTO questions (user_id, question, qtype, hint, explanation, created_at) VALUES (1, 'Some Question', 1, 'Some hint', 'Some explanation', NOW());
-- ;; INSERT INTO question_tests (test_id, question_id, ordnen, created_at) VALUES (1, 2, 2, NOW());
