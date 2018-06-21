-- ;; lein migratus create create-join-question-test-table
-- ;; lein run migrate

CREATE TABLE question_tests(
  id serial PRIMARY KEY,
  test_id INT NOT NULL REFERENCES tests(id),
  question_id INT NOT NULL REFERENCES questions(id),
  created_at timestamp(0) with time zone NOT NULL DEFAULT now(),
  updated_at timestamp(0) with time zone
);

CREATE INDEX test_question_idx ON question_tests (test_id, question_id);

