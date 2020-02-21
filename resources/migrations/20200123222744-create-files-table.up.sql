-- ;; lein migratus create create-files-table
-- ;; lein run migrate
-- ;; <-- this is necessary between statements to avoid "Too many update results were returned."
CREATE TABLE files (
   id serial PRIMARY KEY,
   file varchar(250) NOT NULL,
   img boolean NOT NULL DEFAULT false,
   archived boolean NOT NULL DEFAULT false,
   uurlid varchar(250) NOT NULL UNIQUE,
   user_id int REFERENCES users(id) ON DELETE CASCADE,
   created_at timestamp(0) with time zone NOT NULL DEFAULT now(),
   updated_at timestamp(0) with time zone NOT NULL DEFAULT now()
);
--;;
COMMENT on column files.uurlid is 'Unique Identifier for the file';
--;;
COMMENT on column files.archived is 'Mark the file as archived';
