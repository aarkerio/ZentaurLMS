-- ;; lein migratus create create-images-table
-- ;; lein run migrate
-- ;; <-- this is necessary between statements to avoid "Too many update results were returned."
CREATE TABLE files (
   id serial PRIMARY KEY,
   file varchar(40) NOT NULL,
   img boolean NOT NULL DEFAULT false,
   archive boolean NOT NULL DEFAULT false,
   identifier varchar(250) NOT NULL,
   user_id int REFERENCES users(id) ON DELETE CASCADE,
   created_at timestamp(0) with time zone NOT NULL DEFAULT now()
);
