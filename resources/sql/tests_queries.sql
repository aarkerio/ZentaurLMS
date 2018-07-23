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
-- :name cdreate-test! :! :n
-- :doc creates a new test record
INSERT INTO tests (id, fname, lname, email, pass)
VALUES (:id, :fname, :lname, :email, :pass)

-- :name upddate-test! :! :n
-- :doc update an existing test record
UPDATE tests
SET fname = :fname, last_name = :last_name, email = :email
WHERE id = :id

-- :name getd-test :? :1
-- :doc retrieve a test given the id.
SELECT * FROM tests
WHERE id = :id

-- :name getd-test-login :? :1
-- :doc retrieve a test given the email and password.
SELECT id, fname, last_name, email, admin FROM tests
WHERE email = :email AND password = :password

-- :name deleted-test! :! :n
-- :doc delete a test given the id
DELETE FROM tests
WHERE id = :id

