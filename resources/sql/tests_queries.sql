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

-- :name saved-message! :! :n
-- :doc creates a new message record
INSERT INTO posts
(fname, last_name, email, pass)
VALUES (:fname, :last_name, :email, :pass)

-- :name saved-upload! :! :n
-- :doc creates a new upload record
INSERT INTO uploads
(filename, active, tags, test_id, created_at)
VALUES (:filename, :active, :tags, :test_id, :created_at)

-- :name getd-uploads :? :*
-- :doc retrieve uploads given the test id.
SELECT id, filename, active, tags, test_id, created_at FROM uploads
WHERE test_id = :test_id ORDER BY id DESC

-- :name getd-posts :? :*
-- :doc retrieve a post given the id.
SELECT id, title, body, active, discution, test_id, created_at FROM posts
ORDER BY id DESC LIMIT 5

-- :name getd-post :? :1
-- :doc retrieve a post given the id.
SELECT * FROM posts
WHERE id = :id


