/****
  Structure: :name :command :result
  Type of commands
     :? = fetch (query)
     :! = execute (statetment like INSERT)
  Type of results:
    :* = vectors [1, 3, 4]
    :affected or :n = number of rows affected (inserted/updated/deleted)
    :1 = one row
    :raw = passthrough an untouched result (default)
***/

/*************************** POSTS ***/

-- :name save-message! :! :n
-- :doc creates a new message record
INSERT INTO comments
(title, body, tags, published, discution, slug)
VALUES (:title, :body, :tags, :published, :discution, :slug)

-- :name get-posts :? :*
-- :doc retrieve array posts given the id.
SELECT p.id, p.title, p.body, p.published, p.discution, p.user_id, p.created_at, p.slug, u.uname FROM posts as p, users as u
WHERE p.published = true AND p.user_id = u.id
ORDER BY id DESC LIMIT 10

-- :name get-post :? :1
-- :doc retrieve a post given the id.
SELECT * FROM posts WHERE id = :id

-- :name save-post! :! :n
-- :doc creates a new post record
INSERT INTO posts
(title, body, published, discution, tags, user_id, slug)
VALUES (:title, :body, :published, :discution, :tags, :user_id, :slug)

-- :name update-post! :! :n
-- :doc update an existing post record
UPDATE posts
SET title = :title, body = :body, tags = :tags, active = :active, discution = :discution
WHERE id = :id

-- :name toggle-post! :! :n
-- :doc update an existing post record
UPDATE posts SET published = :published
WHERE id = :id

-- :name delete-post! :! :n
-- :doc delete a post given the id
DELETE FROM posts
WHERE id = :id

-- :name save-comment :! :n
-- :doc creates a new message record
INSERT INTO comments
(comment, post_id, user_id, created_at)
VALUES (:comment, :post_id, :user_id, :created_at)

-- :name get-comments :? :*
-- :doc retrieve comments from a post given the post id.
SELECT u.id AS user_id, u.fname, u.lname, c.id, c.comment, c.created_at
FROM users AS u, comments AS c
WHERE c.post_id = :id AND u.id=c.user_id ORDER BY c.id

-- :name admin-get-posts :? :*
-- :doc retrieve array posts given the user id.
SELECT p.id, p.title, p.body, p.published, p.discution, p.user_id, p.created_at, p.slug, u.uname FROM posts as p, users as u
WHERE p.user_id = :user-id AND p.user_id = u.id
ORDER BY id DESC

/*******************  UPLOADS   ***/

-- :name save-upload! :! :n
-- :doc creates a new upload record
INSERT INTO uploads
(filename, active, tags, user_id, created_at, hashvar, done)
VALUES (:filename, :active, :tags, :user_id, :created_at, :hashvar, :done)

-- :name get-uploads :? :*
-- :doc retrieve uploads given the user id.
SELECT * FROM uploads
WHERE user_id = :user-id ORDER BY id DESC

-- :name get-upload :? :1
-- :doc retrieve an upload file given the id.
SELECT * FROM uploads
WHERE id = :id

/**************   TESTS    ****/

-- :name create-test! :<!
-- :doc creates a new test record
INSERT INTO tests (title, description, instructions, level, lang, tags, origin, user_id)
VALUES (:title, :description, :instructions, :level, :lang, :tags, :origin, :user-id) returning id

-- :name create-question! :<!
-- :doc creates a new question record
INSERT INTO questions (question, qtype, hint, explanation, active, user_id)
VALUES (:question, :qtype, :hint, :explanation, :active, :user-id) returning id

-- :name create-question-test! :! :n
-- :doc creates a new question test record
INSERT INTO question_tests (question_id, test_id)
VALUES (:question-id, :test-id)

-- :name create-answer! :<!
-- :doc creates a new answer record
INSERT INTO answers (question_id  answer correct)
VALUES (:question_id  :answer :correct) returning id

-- :name get-test :? :1
-- :doc retrieve a test given the id.
SELECT * FROM tests WHERE id = :id

-- :name delete-test! :! :n
-- :doc delete a test given the id
DELETE FROM tests WHERE id = :id

/**** ROLES   ****/

-- :name get-roles :? :*
-- :doc retrieve all roles.
SELECT * FROM roles

/******   GENERICS CALLS   ****/

-- :name clj-expr-generic-update :! :n
/* :require [clojure.string :as string]
            [hugsql.parameters :refer [identifier-param-quote]] */
update :i:table set
/*~
(string/join ","
  (for [[field _] (:updates params)]
    (str (identifier-param-quote (name field) options)
      " = :v:updates." (name field))))
~*/
where id = :id

-- :name clj-generic-last-id :? :1
-- :doc generic last inserted id
SELECT id FROM :i:table-name ORDER BY id DESC LIMIT 1
