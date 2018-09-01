-- ;; lein migratus create create-answers-table
-- ;; lein run migrate

CREATE TABLE answers(
  id serial PRIMARY KEY,
  question_id INT NOT NULL REFERENCES questions(id),
  answer VARCHAR(300) NOT NULL,
  correct BOOLEAN NOT NULL DEFAULT false,
  active BOOLEAN NOT NULL DEFAULT false,
  created_at timestamp(0) with time zone NOT NULL DEFAULT now(),
  updated_at timestamp(0) with time zone
 );
