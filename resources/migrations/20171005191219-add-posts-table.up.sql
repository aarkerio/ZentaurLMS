-- ;; lein migratus create create-posts-table
-- ;; lein run migrate
-- ;; lein with-profile test run migrate
CREATE TABLE posts (
   id serial PRIMARY KEY,
   title varchar(250) NOT NULL,
   slug varchar(250) NOT NULL,
   body text NOT NULL,
   published boolean NOT NULL DEFAULT false,
   user_id int NOT NULL REFERENCES users(id),
   discussion boolean NOT NULL DEFAULT TRUE,   -- Discussion on entry, Actived/Desactived
   tags varchar(100),
   created_at timestamp(0) with time zone NOT NULL DEFAULT now(),
   updated_at timestamp(0) with time zone NOT NULL DEFAULT now()
);
--;;
INSERT INTO posts (title, body, published, user_id, tags, slug) VALUES ('Be happy is so easy', 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet', true, 1, 'latin, history, art', 'be_happy_is_so_easy');
--;;
INSERT INTO posts (title, body, published, user_id, tags, slug) VALUES ('Lorem ipsum dolor sit amet', 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet', true, 1, 'latin, history, art', 'be_happy_is_so_easy');
--;;
INSERT INTO posts (title, body, published, user_id, tags, slug) VALUES ('CDMX en 1946. Se aprecian los límites de la ciudad y el lago de Texcoco', 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. CDMX en 1946. Se aprecian los límites de la ciudad y el lago de Texcoco At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet', true, 1, 'latin, history, art', 'be_happy_is_so_easy');
--;;
CREATE TRIGGER trig_posts
BEFORE UPDATE ON "posts"
FOR EACH ROW
EXECUTE PROCEDURE trigger_set_timestamp();
