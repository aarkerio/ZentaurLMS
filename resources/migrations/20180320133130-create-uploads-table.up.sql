-- ;; lein migratus create create-uploads-table
-- ;; lein run migrate

CREATE TABLE uploads (
   id serial PRIMARY KEY,
   filename varchar(250) NOT NULL,
   hashvar varchar(250) NOT NULL,
   active boolean NOT NULL DEFAULT true,
   user_id int NOT NULL REFERENCES users(id),
   tags varchar(100),
   done boolean NOT NULL DEFAULT false,
   json text,
   content text,
   hashvar varchar(250) NOT NULL, -- MD5 file checksum
   created_at timestamp(0) with time zone NOT NULL DEFAULT now(),
   updated_at timestamp(0) with time zone
);
