-- ;; lein migratus create create-images-table
-- ;; lein run migrate
-- ;; <-- this is necessary between statements to avoid "Too many update results were returned."
CREATE TABLE images (
   id serial PRIMARY KEY,
   file varchar(40) NOT NULL,
   user_id int REFERENCES users(id) ON DELETE CASCADE
);
