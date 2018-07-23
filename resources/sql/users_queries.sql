/*
  Structure: :name :command :result
  Type of commands
     :? = fetch (query)
     :! = execute (statetment like INSERT)
  Type of results:
    :* = vectors [1, 3, 4]
    :affected or :n = number of rows affected (inserted/updated/deleted)
    :1 = one row
    :raw = passthrough an untouched result (default)
*/
-- :name cdreate-user! :! :n
-- :doc creates a new user record
INSERT INTO users (id, fname, lname, email, pass)
VALUES (:id, :fname, :lname, :email, :pass)

-- :name upddate-user! :! :n
-- :doc update an existing user record
UPDATE users
SET fname = :fname, last_name = :last_name, email = :email
WHERE id = :id

-- :name getd-user :? :1
-- :doc retrieve a user given the id.
SELECT * FROM users
WHERE id = :id

-- :name getd-user-login :? :1
-- :doc retrieve a user given the email and password.
SELECT id, fname, last_name, email, admin FROM users
WHERE email = :email AND password = :password

-- :name deleted-user! :! :n
-- :doc delete a user given the id
DELETE FROM users
WHERE id = :id


