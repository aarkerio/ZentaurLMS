CREATE TABLE website (
  id serial PRIMARY KEY,
  urlbase varchar(150) NOT NULL,
  name varchar(150) NOT NULL,
  slogan varchar(100) NOT NULL,
  email varchar(40) NOT NULL,
  keywords varchar(200) NOT NULL,
  facebook varchar(200) NOT NULL,
  twitter varchar(200) NOT NULL,
  github varchar(200) NOT NULL,
  description varchar(100) NOT NULL,
  address varchar(200) NOT NULL
);
