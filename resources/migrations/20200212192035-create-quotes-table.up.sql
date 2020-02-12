-- ;; lein migratus create create-quotes-table
-- ;; lein run migrate
CREATE TABLE quotes (
    id serial PRIMARY KEY,
    quote varchar(150) NOT NULL,
    author varchar(50) NOT NULL
);
--;;
INSERT INTO quotes (quote, author) VALUES ('Always forgive your enemies; nothing annoys them so much.', 'Oscar Wilde');
--;;
INSERT INTO quotes (quote, author) VALUES ('I am not young enough to know everything.', 'Oscar Wilde');
--;;
INSERT INTO quotes (quote, author) VALUES ('Seriousness is the only refuge of the shallow.', 'Oscar Wilde');
--;;
INSERT INTO quotes (quote, author) VALUES ('Patriotism is the willingness to kill and be killed for trivial reasons.', 'Bertrand Rusell');
--;;
INSERT INTO quotes (quote, author) VALUES ('There is much pleasure to be gained from useless knowledge.', 'Bertrand Rusell');
--;;
INSERT INTO quotes (quote, author) VALUES ('The time you enjoy wasting is not wasted time', 'Bertrand Rusell');

