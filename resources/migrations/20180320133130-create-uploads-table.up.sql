-- ;; lein migratus create create-uploads-table
-- ;; lein run migrate
-- ;; lein with-profile test run migrate

CREATE TABLE uploads (
   id serial PRIMARY KEY,
   filename varchar(250) NOT NULL,
   active boolean NOT NULL DEFAULT true,
   user_id int NOT NULL REFERENCES users(id),
   tags varchar(100),
   done boolean NOT NULL DEFAULT false,
   json text,
   content text,
   hashvar varchar(250) NOT NULL UNIQUE, -- MD5 file checksum   ALTER TABLE uploads ADD CONSTRAINT hashvar_unique UNIQUE (hashvar);
   created_at timestamp(0) with time zone NOT NULL DEFAULT now(),
   updated_at timestamp(0) with time zone NOT NULL DEFAULT now()
);
--;;
CREATE TRIGGER trig_uploads
BEFORE UPDATE ON "uploads"
FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();
