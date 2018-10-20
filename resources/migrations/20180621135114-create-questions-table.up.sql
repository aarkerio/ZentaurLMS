-- ;; lein migratus create create-answers-table
-- ;; lein run migrate

CREATE TABLE questions(
  id serial PRIMARY KEY,
  user_id INT NOT NULL REFERENCES users(id),
  question text NOT NULL,
  qtype INT NOT NULL DEFAULT 1,  -- qtype 1: multiple option, 2: open, 3: fullfill, 4: composite questions
  hint VARCHAR(300),
  explanation text,
  active BOOLEAN NOT NULL DEFAULT false,
  reviewed_lang BOOLEAN NOT NULL DEFAULT false,
  reviewed_fact BOOLEAN NOT NULL DEFAULT false,
  reviewed_cr BOOLEAN NOT NULL DEFAULT false,    -- reviewd copyright
  created_at timestamp(0) with time zone NOT NULL DEFAULT now(),
  updated_at timestamp(0) with time zone
 );

ALTER TABLE questions ADD CHECK (qtype IN (1,2,3,4));

COMMENT on column questions.qtype is '1: multiple option, 2: open, 3: fullfill, 4: composite questions (columns)';
