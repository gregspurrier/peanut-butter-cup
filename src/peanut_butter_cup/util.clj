(ns peanut-butter-cup.util
  "Misc. utility funtions"
  (:require [clojure.string :as str]))

(defn- hyphen-to-underscore
  [s]
  (apply str (str/replace s \- \_)))

(defn name-to-test-path
  "Returns the path to the test file corresponding to the namespace"
  [ns]
  (let [parts (str/split (hyphen-to-underscore (name ns)) #"\.")]
    (str (str/join "/" (list* "test" (first parts) "test" (rest parts)))
         ".clj")))