-- ;; lein migratus create create-tests-table
-- ;; lein run migrate

CREATE TABLE tests(
  id serial PRIMARY KEY,
  user_id int NOT NULL REFERENCES users(id),
  name VARCHAR(30) NOT NULL,
  tags VARCHAR(30),
  lang VARCHAR(30) NOT NULL DEFAULT 'en',
  origin VARCHAR(30) NOT NULL,
  description VARCHAR(300) NOT NULL,
  instructions VARCHAR(300) NOT NULL,
  level INT NOT NULL DEFAULT 1,
  active BOOLEAN NOT NULL DEFAULT true,
  shared BOOLEAN NOT NULL DEFAULT true,
  created_at timestamp(0) with time zone NOT NULL DEFAULT now(),
  updated_at timestamp(0) with time zone
 );
