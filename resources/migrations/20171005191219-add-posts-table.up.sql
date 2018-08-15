-- ;; lein migratus create create-posts-table
-- ;; lein run migrate

CREATE TABLE posts (
   id serial PRIMARY KEY,
   title varchar(250) NOT NULL,
   slug varchar(250) NOT NULL,
   body text NOT NULL,
   published boolean NOT NULL DEFAULT false,
   user_id int NOT NULL REFERENCES users(id),
   discution boolean NOT NULL DEFAULT TRUE,   -- Discution on entry, Actived/Desactived   1/0
   tags varchar(100),
   created_at timestamp(0) with time zone NOT NULL DEFAULT now(),
   updated_at timestamp(0) with time zone
);
