-- ;; lein migratus create create-vclassrooms-table
-- ;; lein run migrate
-- ;; lein with-profile test run migrate
CREATE TABLE vclassrooms (
  "id" serial PRIMARY KEY,
  "name" varchar(150) NOT NULL,
  "uurlid" varchar(60) NOT NULL UNIQUE,
  "user_id" int REFERENCES users(id) ON DELETE CASCADE,
  "draft" boolean NOT NULL DEFAULT true,
  "historical" boolean NOT NULL DEFAULT false,
  "secret" varchar(10),
  "public" boolean NOT NULL DEFAULT false,
  "welcome_message" text,
  "gcalendar_id" varchar(70),  --google calendar ID
  "created_at" timestamp(0) with time zone DEFAULT now() NOT NULL,
  "updated_at" timestamp(0) with time zone DEFAULT now() NOT NULL
);
--;;
COMMENT ON COLUMN vclassrooms.draft IS 'Define published or draft';
--;;
COMMENT ON COLUMN vclassrooms.historical IS 'Vclassroom is now a historical record';
--;;
COMMENT ON COLUMN vclassrooms.uurlid IS 'Unique identifier for the vClass';
--;;
COMMENT ON COLUMN vclassrooms.secret IS 'Secret code to allow students register by themselves';
--;;
COMMENT ON COLUMN vclassrooms.public IS 'Public VC, in other words, without secret code';
--;;
CREATE TABLE student_vclassrooms (
  "id" serial PRIMARY KEY,
  "student_id" int NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  "vclassroom_id" int NOT NULL REFERENCES vclassrooms(id) ON DELETE CASCADE,
  "created_at" timestamp(0) with time zone DEFAULT now() NOT NULL,
  "updated_at" timestamp(0) with time zone DEFAULT now() NOT NULL,
  UNIQUE ("student_id", "vclassroom_id")
);
