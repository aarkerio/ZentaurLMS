-- ;; lein migratus create create-pages
-- ;; lein run migrate
-- ;; pages in the users blogs
CREATE TABLE pages (
 "id" serial PRIMARY KEY,
 "title" varchar(50) NOT NULL,
 "body" text NOT NULL,
 "order" smallint DEFAULT 1 NOT NULL,
 "status" smallint NOT NULL DEFAULT 0,
 "user_id" int REFERENCES users(id) ON DELETE CASCADE,
 "discussion" smallint NOT NULL DEFAULT 0,
 "tags" varchar(100),
 "slug" varchar(150),
 "created_at" timestamp(0) with time zone NOT NULL DEFAULT now(),
 "updated_at" timestamp(0) with time zone
);
