-- ;; lein migratus create create-composite-questions-table
-- ;; lein run migrate
CREATE TABLE composite_answers (
  id serial PRIMARY KEY,
  question_id INT NOT NULL REFERENCES questions(id),
  first_column VARCHAR(300) NOT NULL,
  second_column VARCHAR(300) NOT NULL,
  correct_column VARCHAR(300) NOT NULL,
  name_column VARCHAR(300) NOT NULL,
  created_at timestamp(0) with time zone NOT NULL DEFAULT now(),
  updated_at timestamp(0) with time zone
);

