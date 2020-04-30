(ns luminus.seeder
  "Seed DB for dev and test environments"
  (:require [clojure.tools.logging :as log]
            [mount.core :as mount]
            [zentaur.db.core :as db]
            [zentaur.models.tests :as mt]))

(def question-txt ["Condimentum mattis pellentesque id nibh tortor id aliquet lectus. "
                   "Convallis a cras semper auctor neque vitae tempus quam pellentesque. Aliquam sem fringilla ut morbi."
                   "Molestie nunc non blandit massa enim nec. Fusce ut placerat orci nulla pellentesque dignissim enim sit amet. "
                   "Dictum at tempor commodo ullamcorper a lacus vestibulum sed arcu. Quis vel eros donec ac. Fusce id velit ut tortor pretium."
                   "Vitae tortor condimentum lacinia quis vel. Et tortor consequat id porta nibh venenatis cras sed felis. Elit pellentesque habitant morbi tristique senectus."
                   "Nibh tellus molestie nunc non blandit. Molestie at elementum eu facilisis sed odio morbi quis commodo."
                   "In München treffen sich drei Studenten. Sie kennen sich von der Universität."
                   "Meine Eltern leben auf einer Insel, die man nur mit einer Fähre erreichen kann. Auf der Insel fährt kein Auto und keine Bahn."
                   "In Barcelona habe ich ein Auto gemietet und mir die Stadt angesehen. Außerdem fuhr ich mit der Tram. Das ist eine Straßenbahn. "
                   "Mit dem Gemüse kocht er eine Suppe. Dafür braucht er ein Kilo Karotten, einige große Kartoffeln, ein halbes Kilo Zwiebeln und verschiedene Pilze."])
(def points-int [1 2 3 4 5])

(def corr [true false])

(defn start []
  (mount/start #'zentaur.config/env
               #'zentaur.db.core/*db*))

(def first-test (atom nil))

(defn create [subject-id level-id lang-id]
  (let [question     (rand-nth question-txt)
        points       (rand-nth points-int)
        points       (rand-nth points-int)
        pre-params   {:user_id 1 :question question :qtype 1 :hint "vestibulum sed arcu"
                      :points points :origin 0 :explanation "" :fulfill "" :active true}
        params       (assoc pre-params :subject_id subject-id :level_id level-id :lang_id lang-id)
        new-question (db/create-question! params)]
        (log/info (str ">>> PARAM >>>>> " params  "   new-question >>> " new-question))
        (map (mt/create-answer! {:question_id (:id new-question) :answer (rand-nth question-txt) :correct (rand-nth corr)}) (range 4))))

(defn main []
  (let [_        (start)
        subjects (mt/get-subjects)
        levels   (mt/get-levels)
        langs    (mt/get-langs)
        test     (mt/create-test! {:title "Some foo test name" :tags "one two" :subject_id 1 :level_id 1 :lang_id 1} 1)
        _        (reset! first-test test)]
    (for [n (range 20)]
        (for [subject subjects
              level   levels
              lang    langs]
      (create (:id subject) (:id level) (:id lang))))))

