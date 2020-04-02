/****
  HugSQL is a Clojure library for embracing SQL.
  Structure: :name :command :result
  Type of commands
     :?  = fetch (query)
     :!  = execute (statetment like INSERT) RETURNING DOES N0T WORK WITH THIS
     :<! = returning-execute, for INSERT or DELETE with RETURNING
  Type of results:
    :* = vectors [1, 3, 4]
    :affected or :n = number of rows affected (inserted/updated/deleted)
    :1 = one row
    :raw = passthrough an untouched result (default)
***/
/*************************** POSTS ***/

-- :name save-message! :<! :1
-- :doc creates a new message record
INSERT INTO comments
(title, body, tags, published, discussion, slug)
VALUES (:title, :body, :tags, :published, :discussion, :slug) RETURNING id

-- :name get-posts :? :*
-- :doc retrieve array posts given the id.
SELECT p.id, p.title, p.body, p.tags, p.published, p.discussion, p.user_id, p.created_at, p.slug, u.uuid
FROM posts p INNER JOIN users u
ON p.user_id = u.id
WHERE p.published = true
ORDER BY p.id DESC LIMIT 10

-- :name get-post :? :1
-- :doc retrieve a post given the id.
SELECT p.id, p.title, p.tags, p.body, p.published, p.discussion, p.user_id, p.created_at, p.slug, u.uuid
FROM posts p INNER JOIN users u
ON p.user_id = u.id
WHERE p.published = true AND p.id = :id

-- :name get-levels :? :raw
-- :doc retrieves all levels.
SELECT id, level FROM levels ORDER BY id ASC

-- :name get-subjects :? :raw
-- :doc retrieves all subjects.
SELECT id, subject FROM subjects ORDER BY subject ASC

-- :name save-post! :! :1
-- :doc creates a new post record
INSERT INTO posts (title, body, published, discussion, tags, user_id, slug)
VALUES (:title, :body, :published, :discussion, :tags, :user_id, :slug) RETURNING id

-- :name update-post! :! :1
-- :doc update an existing post record
UPDATE posts
SET title = :title, body = :body, tags = :tags, published = :published, discussion = :discussion
WHERE id = :id

-- :name toggle-post! :! :n
-- :doc update an existing post record
UPDATE posts SET published = :published WHERE id = :id

-- :name delete-post! :! :n
-- :doc delete a post given the id
DELETE FROM posts WHERE id = :id

-- :name save-comment :! :1
-- :doc creates a new message record
INSERT INTO comments (comment, post_id, user_id, created_at)
VALUES (:comment, :post_id, :user_id, :created_at) RETURNING *

-- :name get-comments :? :*
-- :doc retrieve comments from a post given the post id.
SELECT u.id AS user_id, u.fname, u.lname, c.id, c.comment, c.created_at
FROM users AS u, comments AS c
WHERE c.post_id = :id AND u.id=c.user_id ORDER BY c.id

-- :name admin-get-posts :? :*
-- :doc retrieve array posts given the user id.
SELECT
    p.id, p.title, p.body, p.published, p.discussion, p.user_id, p.created_at, p.slug, u.uuid
FROM
    posts p INNER JOIN users u
    ON p.user_id = u.id
WHERE
    p.user_id = :user-id
ORDER BY p.id DESC

-- /*******************  USER FILES   ***/

-- :name get-files :? :*
-- :doc retrieve files owned per user.
SELECT * FROM files WHERE user_id = :user-id AND archived = :archived ORDER BY id DESC LIMIT 30

-- :name get-one-file :? :1
-- :doc retrieves one file owned per user.
SELECT * FROM files WHERE user_id = :user-id AND uurlid = :uurlid

-- :name save-file! :<! :1
-- :doc creates a new file record
INSERT INTO files (file, user_id, img, uurlid) VALUES
(:file, :user-id, :img, :uurlid) RETURNING id

-- :name toggle-file :! :1
-- :doc update an existing file record
UPDATE files SET archived = :archived WHERE uurlid = :uurlid

-- /*******************  UPLOADS   ***/

-- :name save-upload! :<!
-- :doc creates a new upload record
INSERT INTO uploads
(filename, active, tags, user_id, created_at, hashvar, done)
VALUES (:filename, :active, :tags, :user-id, :created_at, :hashvar, :done) RETURNING id

-- :name get-uploads :? :*
-- :doc retrieve uploads given the user id.
SELECT * FROM uploads
WHERE user_id = :user-id ORDER BY id DESC

-- :name get-upload :? :1
-- :doc retrieve an upload record given the id.
SELECT * FROM uploads WHERE id = :id

-- :name get-upload-by-hashvar :? :1
-- :doc retrieve an upload id given the hashvar.
SELECT id FROM uploads WHERE hashvar = :hashvar

/**************   TESTS    ****/

-- :name create-test! :<! :1
-- :doc creates a new test record
INSERT INTO tests (title, description, instructions, level, lang, tags, origin, user_id, subject_id, uurlid)
VALUES (:title, :description, :instructions, :level, :lang, :tags, :origin, :user-id, :subject-id, :uurlid) RETURNING *

-- :name create-minimal-test :<! :1
-- :doc creates a minimal test record
INSERT INTO tests (title, tags, user_id, subject_id, level_id, uurlid) VALUES (:title, :tags, :user_id, :subject_id, :level_id, :uurlid) RETURNING *

-- :name create-question! :<! :1
-- :doc creates a new question record
INSERT INTO questions (question, qtype, hint, explanation, user_id, points, subject_id, level_id, origin)
VALUES (:question, :qtype, :hint, :explanation, :user_id, :points, :subject_id, :level_id, :origin) RETURNING id

-- :name create-question-test! :<! :n
-- :doc creates a new question test record
INSERT INTO question_tests (question_id, test_id, ordnen) VALUES (:question_id, :test_id, :ordnen) RETURNING true

-- :name update-question! :<! :1
-- :doc updates a question record
UPDATE questions
SET question = :question, qtype = :qtype, hint = :hint, explanation = :explanation, points = :points
WHERE id = :id RETURNING *

-- :name random-questions :? :raw
-- :select random questions
SELECT id, question, qtype, hint, explanation, user_id, points, subject_id, level_id FROM questions
WHERE subject_id = :subject_id AND level_id = :level_id ORDER BY RANDOM() LIMIT :limit

-- :name update-question-fulfill! :<! :1
-- :doc updates the fulfill field in the question
UPDATE questions SET fulfill = :fulfill WHERE id = :id RETURNING *

-- :name update-answer! :<! :1
-- :doc updates an answer record
UPDATE answers SET answer = :answer, correct = :correct WHERE id = :id RETURNING *

-- :name update-test! :<! :1
-- :doc updates an answer record
UPDATE tests SET title = :title, tags = :tags, description = :description, subject_id = :subject_id
WHERE uurlid = :uurlid RETURNING *

-- :name create-answer! :<! :1
-- :doc creates a new answer record
INSERT INTO answers (question_id, answer, correct, ordnen) VALUES (:question_id, :answer, :correct, :ordnen) RETURNING *

-- :name get-tests :? :*
-- :doc retrieve a test given the id.
SELECT t.id, t.title, t.tags, t.description, t.shared, t.user_id, t.created_at, t.origin, t. uurlid, s.subject, l.level
FROM tests t
INNER JOIN subjects s ON t.subject_id = s.id
INNER JOIN levels l ON t.level_id = l.id
WHERE t.user_id = :user-id AND t.archived = false
ORDER BY t.id DESC

-- :name get-one-test :? :1
-- :doc retrieve a test given the uurlid.
SELECT t.id, t.title, t.tags, t.description, t.shared, t.user_id, t.created_at, t.origin, t.subject_id, t.level_id, t.uurlid, s.subject, l.level
FROM tests t
INNER JOIN subjects s ON t.subject_id = s.id
INNER JOIN levels l ON t.level_id = l.id
WHERE t.archived = :archived AND t.uurlid = :uurlid
ORDER BY t.id DESC

-- :name get-questions :? :*
-- :doc retrieve all questions tests.
SELECT q.id, q.question, q.user_id, q.qtype, q.hint, q.points, q.explanation, q.fulfill, q.reviewed_lang, q.reviewed_fact,
q.reviewed_cr, q.created_at, qt.ordnen FROM question_tests qt INNER JOIN questions q
ON q.id = qt.question_id
WHERE qt.test_id = :test-id AND qt.question_id = q.id ORDER BY qt.ordnen ASC

-- :name get-one-question :? :1
-- :doc retrieve a question given the id.
SELECT q.id, q.question, q.qtype, q.hint, q.points, q.explanation, q.fulfill, q.reviewed_lang, q.reviewed_fact, q.reviewed_cr,
q.created_at, q.user_id, q.origin, qt.ordnen FROM question_tests qt INNER JOIN questions q
ON q.id = qt.question_id WHERE q.id = :id LIMIT 1

-- :name get-last-answer :? :1
-- :doc retrieve all questions tests.
SELECT * FROM answers WHERE question_id = :question-id  ORDER BY id DESC LIMIT 1

-- :name get-last-ordnen-questions :? :1
-- :doc retrieve the last ordnen.
SELECT ordnen FROM question_tests WHERE test_id = :test-id ORDER BY ordnen DESC LIMIT 1

-- :name get-last-ordnen-answer :? :1
-- :doc retrieve the last ordnen.
SELECT ordnen FROM answers WHERE question_id = :question-id ORDER BY ordnen DESC LIMIT 1

-- :name get-answers :? :*
-- :doc retrieve all tests.
SELECT id, question_id, answer, ordnen, correct FROM answers WHERE question_id = :question_id  ORDER BY ordnen ASC

-- :name unlink-question! :<! :1
-- :doc unlink a question from the test
DELETE FROM question_tests WHERE test_id = :test_id AND question_id = :question_id RETURNING TRUE

-- :name remove-answer! :<! :1
-- :doc remove an answer given the question-id
DELETE FROM answers WHERE question_id = :question_id AND id = :answer_id RETURNING TRUE

-- :name toggle-test :<! :1
-- :doc set test as archived
UPDATE tests SET archived = true WHERE uurlid = :uurlid RETURNING TRUE

/***** REORDER QUESTIONS STARTS ******/

-- :name question-order-up :? :*
-- :doc get two records
SELECT id, test_id, question_id, ordnen FROM question_tests WHERE test_id = :test_id AND ordnen <= :ordnen ORDER BY ordnen DESC LIMIT 2

-- :name question-order-down :? :*
-- :doc get two records
SELECT id, test_id, question_id, ordnen FROM question_tests WHERE test_id = :test_id AND ordnen >= :ordnen ORDER BY ordnen ASC LIMIT 2

-- :name update-question-order :<! :1
-- :doc set reorder in question
UPDATE question_tests SET ordnen = :ordnen WHERE id = :id RETURNING TRUE

-- :name unlink-question :<! :1
-- :doc remove question from test
DELETE FROM question_tests WHERE question_id = :question_id AND test_id = :test_id RETURNING ordnen

/***** REORDER QUESTIONS ENDS ******/

/***** REORDER ANSWERS STARTS ******/

-- :name answer-order-up :? :*
-- :doc get two records
SELECT id, ordnen FROM answers WHERE question_id = :question-id  AND ordnen <= :ordnen ORDER BY ordnen DESC LIMIT 2

-- :name answer-order-down :? :*
-- :doc get two records
SELECT id, ordnen FROM answers WHERE question_id = :question-id  AND ordnen >= :ordnen ORDER BY ordnen ASC LIMIT 2

-- :name update-answer-order :<! :1
-- :doc set reorder in question
UPDATE answers SET ordnen = :ordnen WHERE id = :id RETURNING TRUE

/***** REORDER ANSWERS ENDS ******/

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

/******************* USERS ***/

-- :name create-user! :<! :1
-- :doc creates a new user record
INSERT INTO users (fname, lname, email, password, admin, active, role_id, uuid)
VALUES (:fname, :lname, :email, :password, :admin, :active, :role_id, :uuid) RETURNING *

-- :name update-user! :! :n
-- :doc update an existing user record
UPDATE users
SET fname = :fname, lname = :lname, email = :email
WHERE uuid = :uuid

-- :name get-user :? :1
-- :doc retrieve a user given the id.
SELECT * FROM users WHERE id = :id

-- :name get-users :? :*
-- :doc retrieve all users given the active column.
SELECT * FROM users
WHERE active = :active ORDER BY id DESC

-- :name get-user-login :? :1
-- :doc retrieve a user given the email and password.
SELECT id, fname, lname, uuid, email, admin FROM users
WHERE email = :email AND password = :password

-- :name delete-user! :<! :n
-- :doc delete a user given the id
DELETE FROM users WHERE id = :id  RETURNING TRUE

-- :name delete-all-tables! :! :n
-- :doc delete all contest ONLY in TEST env
DROP RULE test_del_protect ON tests;
TRUNCATE pages, composite_answers, roles, users, posts, question_tests, tests, uploads, questions, answers, comments

/******* QUOTES ****/

-- :name get-one-quote :? :1
-- :doc retrieve a random quote.
SELECT * FROM	quotes OFFSET floor(random() * (SELECT COUNT(*)	FROM quotes)) LIMIT 1

/********  VCLASSROOMS ***/

-- :name get-vclassrooms :? :*
-- :doc retrieve array posts given the id.
SELECT id, name, user_id, draft, historical, secret, public, uurlid, description, created_at
FROM vclassrooms WHERE historical = :historical AND user_id = :user-id ORDER BY id DESC LIMIT 10

-- :name get-vclass :? :1
-- :doc retrieve a vclassroom given the uurlid.
SELECT id, name, draft, historical, secret, public, uurlid, description, created_at
FROM vclassrooms WHERE user_id = :user-id AND uurlid = :uurlid

-- :name create-vclass! :<! :1
-- :doc creates a new message record
INSERT INTO vclassrooms (name, user_id, draft, historical, secret, public, description, uurlid)
 VALUES (:name, :user-id, :draft, :historical, :secret, :public, :description, :uurlid) RETURNING *

-- :name update-vclass :<! :1
-- :doc update an existing vclassroom record
UPDATE vclassrooms SET name = :name, secret = :secret, description = :description,
draft = :draft, historical = :historical, public = :public WHERE uurlid = :uurlid AND user_id = :user-id RETURNING *

-- :name toggle-vclassroom :! :n
-- :doc update an existing vclassroom record
UPDATE vclassrooms SET draft = :draft WHERE uurlid = :uurlid

-- :name delete-vclassroom :! :n
-- :doc delete a post given the id
DELETE FROM vclassrooms WHERE uurlid = :uurlid
