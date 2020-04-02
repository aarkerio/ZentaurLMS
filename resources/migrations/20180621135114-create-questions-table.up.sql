-- ;; lein migratus create create-questions-table
-- ;; lein run migrate
CREATE TABLE questions (
  id serial PRIMARY KEY,
  subject_id int NOT NULL REFERENCES subjects(id),
  level_id int NOT NULL REFERENCES levels(id),
  user_id int NOT NULL REFERENCES users(id),
  question text NOT NULL,
  qtype smallint NOT NULL DEFAULT 1,
  hint varchar(300),
  points smallint NOT NULL DEFAULT 1,
  origin INT NOT NULL DEFAULT 0,
  explanation text,
  fulfill text NOT NULL DEFAULT '',
  reviewed_lang BOOLEAN NOT NULL DEFAULT false,
  reviewed_fact BOOLEAN NOT NULL DEFAULT false,
  reviewed_cr BOOLEAN NOT NULL DEFAULT false,    -- reviewed copyright
  created_at timestamp(0) with time zone NOT NULL DEFAULT now(),
  updated_at timestamp(0) with time zone NOT NULL DEFAULT now()
 );
--;;
ALTER TABLE questions ADD CHECK (qtype IN (1,2,3,4));
--;;
COMMENT on column questions.qtype is '1: multiple option, 2: open, 3: fulfill, 4: composite questions (columns)';
-- ;; INSERT INTO questions (user_id, question, qtype, hint, explanation, created_at) VALUES (1, 'Some Question', 1, 'Some hint', 'Some explanation', NOW());
--;;
COMMENT on column questions.origin is 'Marks if the question is edited from another question, if not 0';
--;; INSERT INTO question_tests (test_id, question_id, ordnen, created_at) VALUES (1, 2, 2, NOW());
COMMENT on column questions.fulfill is 'This field is used when the question is type 3: fulfill';
--;;
CREATE TRIGGER trig_questions
BEFORE UPDATE ON "questions"
FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();
