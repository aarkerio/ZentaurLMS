CREATE TABLE comments (    -- blogs's discussions
 "id" serial PRIMARY KEY,
 "comment" text,
 "post_id" int NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
 "user_id" int NOT NULL DEFAULT 0,
 "created_at" timestamp(0) with time zone DEFAULT now() NOT NULL,
 "updatet_at" timestamp(0) with time zone DEFAULT now() NOT NULL
);
