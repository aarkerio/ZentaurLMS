-- ;; lein migratus create create-tests-table
-- ;; lein run migrate
-- ;; lein with-profile test run migrate
CREATE TABLE tests(
  id serial PRIMARY KEY,
  user_id int NOT NULL REFERENCES users(id),
  subject_id int NOT NULL REFERENCES subjects(id),
  level_id int NOT NULL REFERENCES levels(id),
  lang_id int NOT NULL REFERENCES langs(id),
  title VARCHAR(200) NOT NULL,
  tags VARCHAR(200),
  origin VARCHAR(150),
  uurlid varchar(60) NOT NULL UNIQUE,
  from_test int,
  description VARCHAR(300),
  instructions VARCHAR(300),
  difficulty INT NOT NULL DEFAULT 1,
  active BOOLEAN NOT NULL DEFAULT true,
  archived BOOLEAN NOT NULL DEFAULT false,
  shared BOOLEAN NOT NULL DEFAULT true,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now()
 );
--;;
CREATE RULE test_del_protect AS ON DELETE TO tests DO INSTEAD NOTHING;
--;;
COMMENT on column tests.origin is 'OPTIONAL. URL Where the test came from. Only used if the test was imported';
--;;
COMMENT on column tests.active is 'This boolean flag acts as a fake delete since a test is actually never deleted';
--;;
COMMENT on column tests.archived is 'Flag marks a test disposed bythe user';
--;;
COMMENT on column tests.from_test is 'Only used when the test is created from another test';
--;;
COMMENT on column tests.difficulty is 'Diffcult level, from 1 to 5 id est, how hard the test is';
--;;
CREATE TRIGGER trig_tests
BEFORE UPDATE ON "tests"
FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();
--;;
-- INSERT INTO tests (user_id, subject_id, title, tags) VALUES (1, 4, 'Test a new test', 'math, geometry');
