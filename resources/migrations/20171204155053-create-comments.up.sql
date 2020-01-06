CREATE TABLE comments (    -- blogs's discussions
 "id" serial PRIMARY KEY,
 "comment" text,
 "post_id" int NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
 "user_id" int NOT NULL DEFAULT 0,
 "created_at" timestamp(0) with time zone DEFAULT now() NOT NULL,
 "updated_at" timestamp(0) with time zone DEFAULT now() NOT NULL
);
--;;
CREATE TRIGGER trig_comments
BEFORE UPDATE ON "comments"
FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();
