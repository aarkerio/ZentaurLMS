-- ;; lein migratus create create-levels-table
-- ;; lein run migrate
-- ;; lein with-profile test run migrate

CREATE TABLE levels (
   id INT PRIMARY KEY,
   level varchar(150) NOT NULL
);
--;;
INSERT INTO levels (id, level) VALUES (1, 'Elementary School - First grade');
--;;
INSERT INTO levels (id, level) VALUES (2, 'Elementary School - Second grade');
--;;
INSERT INTO levels (id, level) VALUES (3, 'Elementary School - Third grade');
--;;
INSERT INTO levels (id, level) VALUES (4, 'Elementary School - Fourth grade');
--;;
INSERT INTO levels (id, level) VALUES (5, 'Elementary School - Fifth grade');
--;;
INSERT INTO levels (id, level) VALUES (6, 'Middle school -	Sixth grade');
--;;
INSERT INTO levels (id, level) VALUES (7, 'Middle school - Seventh grade');
--;;
INSERT INTO levels (id, level) VALUES (8, 'Middle school - Eighth grade');
--;;
INSERT INTO levels (id, level) VALUES (9, 'High school - Ninth grade (freshman)');
--;;
INSERT INTO levels (id, level) VALUES (10, 'High school - Tenth grade (sophomore)');
--;;
INSERT INTO levels (id, level) VALUES (11, 'High school - Eleventh grade (junior)');
--;;
INSERT INTO levels (id, level) VALUES (12, 'High school - Twelfth grade (senior)');
--;;
INSERT INTO levels (id, level) VALUES (13, 'University - First year');

