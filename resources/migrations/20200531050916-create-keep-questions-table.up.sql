-- ;; lein migratus create create-keep-questions-table
-- ;; lein run migrate
-- ;; lein with-profile test run migrate
CREATE TABLE keep_questions (
  id serial PRIMARY KEY,
  question_id INT NOT NULL REFERENCES questions(id),
  user_id INT NOT NULL REFERENCES users(id),
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  UNIQUE (question_id, user_id)
);

