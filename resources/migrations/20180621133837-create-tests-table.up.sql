-- ;; lein migratus create create-tests-table
-- ;; lein run migrate

CREATE TABLE tests(
  id serial PRIMARY KEY,
  user_id int NOT NULL REFERENCES users(id),
  title VARCHAR(100) NOT NULL,
  tags VARCHAR(100),
  lang VARCHAR(2) NOT NULL DEFAULT 'en',
  origin VARCHAR(150) NOT NULL,
  description VARCHAR(300) NOT NULL,
  instructions VARCHAR(300) NOT NULL,
  level INT NOT NULL DEFAULT 1,
  active BOOLEAN NOT NULL DEFAULT true,
  shared BOOLEAN NOT NULL DEFAULT true,
  created_at timestamp(0) with time zone NOT NULL DEFAULT now(),
  updated_at timestamp(0) with time zone
 );
