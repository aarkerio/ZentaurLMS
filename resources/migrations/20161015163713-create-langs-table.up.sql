-- ;; lein migratus create create-langs-table
-- ;; lein run migrate
-- ;; lein with-profile test run migrate
CREATE TABLE langs (
   id INT PRIMARY KEY,
   lang varchar(20) NOT NULL
);
--;;
INSERT INTO langs (id, lang) VALUES (1, 'English');
--;;
INSERT INTO langs (id, lang) VALUES (2, 'Español');
--;;
INSERT INTO langs (id, lang) VALUES (3, 'Deutsche');
