(ns zentaur.seeder
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
                   "Nibh tellus molestie nunc non blandit. Molestie at elementum eu facilisis sed odio morbi quis commodo."])
(def points-int [1 2 3 4 5])

(defn start []
       (mount/start #'zentaur.config/env
                    #'zentaur.handler/app-routes
                    #'zentaur.db.core/*db*))

(defn create [subject-id level-id]
  (let [question   (rand-nth question-txt)
        points     (rand-nth points-int)
        pre-params {:user_id 1 :question question :qtype 1 :hint "vestibulum sed arcu"
                    :points points :origin 0 :explanation "" :fulfill "" :active true}
        params     (assoc pre-params :level_id level-id :subject_id subject-id)]
  (db/create-question! params)))

(defn main []
  (let [_        (start)
        subjects (mt/get-subjects)
        levels   (mt/get-levels)]
    (for [n (range 1000)]
        (for [subject subjects
          level   levels]
      (create (:id subject) (:id level))))))
