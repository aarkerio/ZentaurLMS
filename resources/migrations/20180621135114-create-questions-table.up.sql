-- ;; lein migratus create create-questions-table
-- ;; lein run migrate
CREATE TABLE questions (
  id serial PRIMARY KEY,
  subject_id int NOT NULL REFERENCES subjects(id),
  level_id int NOT NULL REFERENCES levels(id),
  lang_id int NOT NULL REFERENCES langs(id),
  user_id int NOT NULL REFERENCES users(id),
  question text NOT NULL,
  qtype smallint NOT NULL DEFAULT 1 CHECK (IN (1,2,3,4)),
  hint varchar(300),
  points smallint NOT NULL DEFAULT 1,
  origin INT NOT NULL DEFAULT 0,
  explanation text,
  fulfill text NOT NULL DEFAULT '',
  reviewed_lang BOOLEAN NOT NULL DEFAULT false,
  reviewed_fact BOOLEAN NOT NULL DEFAULT false,
  reviewed_cr BOOLEAN NOT NULL DEFAULT false,    -- reviewed copyright
  tsv_en tsvector GENERATED ALWAYS AS (to_tsvector('english', question || ' ' || hint || ' ' || explanation)) STORED,
  tsv_es tsvector GENERATED ALWAYS AS (to_tsvector('spanish', question || ' ' || hint || ' ' || explanation)) STORED
  created_at timestamp(0) with time zone NOT NULL DEFAULT now(),
  updated_at timestamp(0) with time zone NOT NULL DEFAULT now()
 );
--;;
COMMENT on column questions.qtype is '1: multiple option, 2: open, 3: fulfill, 4: composite questions (columns)';
--;;
COMMENT on column questions.origin is 'Marks if the question is edited from another question, if not 0';
--;; INSERT INTO question_tests (test_id, question_id, ordnen, created_at) VALUES (1, 2, 2, NOW());
COMMENT on column questions.fulfill is 'This field is used when the question is type 3: fulfill';
--;;
CREATE TRIGGER trig_questions
BEFORE UPDATE ON "questions"
FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();
