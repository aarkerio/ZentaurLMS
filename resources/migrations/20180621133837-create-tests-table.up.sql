-- ;; lein migratus create create-tests-table
-- ;; lein run migrate

CREATE TABLE tests(
  id serial PRIMARY KEY,
  user_id int NOT NULL REFERENCES users(id),
  title VARCHAR(100) NOT NULL,
  tags VARCHAR(100),
  lang VARCHAR(2) NOT NULL DEFAULT 'en',
  origin VARCHAR(150) UNIQUE,
  description VARCHAR(300),
  instructions VARCHAR(300),
  level INT NOT NULL DEFAULT 1,
  active BOOLEAN NOT NULL DEFAULT true,
  archived BOOLEAN NOT NULL DEFAULT false,
  shared BOOLEAN NOT NULL DEFAULT true,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone
 );

CREATE RULE test_del_protect AS ON DELETE TO tests DO INSTEAD NOTHING;

COMMENT on column tests.origin is 'OPTIONAL. URL Where the test came from. Only used if the test was imported';
COMMENT on column tests.active is 'This boolean flag acts as a fake delete since a test is actually never deleted';
COMMENT on column tests.archived is 'Flag marks a test disposed bythe user';
COMMENT on column tests.level is 'Diffcult leve, id est, how hard is the test';
