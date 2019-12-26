-- ;; lein migratus create create-answers-table
-- ;; lein run migrate

CREATE TABLE answers(
  id serial PRIMARY KEY,
  question_id INT NOT NULL REFERENCES questions(id),
  answer VARCHAR(300) NOT NULL,
  ordnen SMALLINT NOT NULL DEFAULT 1,
  correct BOOLEAN NOT NULL DEFAULT false,
  active BOOLEAN NOT NULL DEFAULT false,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now()
 );

CREATE TRIGGER trig_answers
BEFORE UPDATE ON "answers"
FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();
