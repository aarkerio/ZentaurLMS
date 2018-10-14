(ns zentaur.config-test)

(defn one-time-setup []
  (println "one time setup"))

(defn one-time-teardown []
  (println "one time teardown"))

(defn once-fixture [f]
  (one-time-setup)
  (f)
  (one-time-teardown))

(defn setup-test []
  (println "setup"))

(defn teardown-test []
  (println "teardown"))

(defn wrap-setup
  [f]
  (println "wrapping setup")
  ;; note that you generally want to run teardown-tests in a try ...
  ;; finally construct, but this is just an example
  (setup-test)
  (f)
  (teardown-test))

(defn setup []
  (println "setup"))

(defn teardown []
  (println "teardown"))

(defn each-fixture [f]
  (setup)
  (f)
  (teardown))
