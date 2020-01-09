-- ;; lein migratus create create-categories-table
-- ;; lein run migrate
-- ;; lein with-profile test run migrate
-- ;;
CREATE TABLE subjects (
   id serial PRIMARY KEY,
   subject varchar(150) NOT NULL
);
--;;
INSERT INTO subjects (subject) VALUES ('Maths');
--;;
INSERT INTO subjects (subject) VALUES ('English');
--;;
INSERT INTO subjects (subject) VALUES ('Spanish');
--;;
INSERT INTO subjects (subject) VALUES ('Geography');
--;;
INSERT INTO subjects (subject) VALUES ('Art');
--;;
INSERT INTO subjects (subject) VALUES ('Music');
--;;
INSERT INTO subjects (subject) VALUES ('History');
--;;
INSERT INTO subjects (subject) VALUES ('Chemistry');
--;;
INSERT INTO subjects (subject) VALUES ('Physics');
--;;
INSERT INTO subjects (subject) VALUES ('Biology');
--;;
INSERT INTO subjects (subject) VALUES ('Geometry');
--;;
INSERT INTO subjects (subject) VALUES ('Science');
--;;
INSERT INTO subjects (subject) VALUES ('Politics');
--;;
INSERT INTO subjects (subject) VALUES ('Philosophy');
--;;
INSERT INTO subjects (subject) VALUES ('Physical Education');

