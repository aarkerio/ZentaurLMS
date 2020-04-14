-- ;; lein migratus create create-lesson-plans-table
-- ;; lein run migrate
-- ;; lein with-profile test run migrate
CREATE TABLE lesson_plans (
  id serial PRIMARY KEY,
  user_id INT NOT NULL REFERENCES users(id),
  subject_id INT NOT NULL REFERENCES subjects(id),
  level_id INT NOT NULL REFERENCES levels(id),
  lang_id INT NOT NULL REFERENCES langs(id),
  title VARCHAR(200) NOT NULL,
  tags VARCHAR(200),
  origin VARCHAR(150),
  uurlid varchar(60) NOT NULL UNIQUE,
  from_lp INT,
  class_size INT,
  curric_objectives TEXT,
  the_hook TEXT,
  exploration TEXT,
  connection TEXT,
  practice TEXT,
  lesson_objectives TEXT,
  duration INT NOT NULL DEFAULT 15,
  materials TEXT,
  INTroduction TEXT,
  activities TEXT,
  summary TEXT,
  evaluation TEXT,
  notes TEXT,
  active BOOLEAN NOT NULL DEFAULT true,
  archived BOOLEAN NOT NULL DEFAULT false,
  shared BOOLEAN NOT NULL DEFAULT true,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now()
 );
--;;
COMMENT on column lesson_plans.from_lp is 'Optional. Lesson plan origin. 0 if is original.';
--;;
COMMENT on column lesson_plans.duration is 'Optional. Lesson duration in minutes.';
--;;
COMMENT on column lesson_plans.curric_objectives is 'Optional. Curriculum objectives.';
--;;
COMMENT on column lesson_plans.lesson_objectives is 'Optional. Lesson objectives.';
--;;
CREATE TRIGGER trig_tests
BEFORE UPDATE ON "lesson_plans"
FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();
