(ns peanut-butter-cup.util
  "Misc. utility funtions"
  (:require [clojure.string :as str]
            [clojure.pprint :as pp]
            [clojure.java.io :as io]))

(defn- hyphen-to-underscore
  [s]
  (apply str (str/replace s \- \_)))

(defn name-to-test-path
  "Returns the path to the test file corresponding to the namespace"
  [ns]
  (let [parts (str/split (hyphen-to-underscore (name ns)) #"\.")]
    (str (str/join "/" (list* "test" (first parts) "test" (rest parts)))
         ".clj")))

(defn name-to-test-name
  "Returns the name of the test namespace corresponding to the one provided"
  [ns]
  (let [parts (str/split (name ns) #"\.")]
    (symbol (str/join "." (list* (first parts) "test" (rest parts))))))

(defn form-to-str
  "Given a form, returns a pretty-printed string represenation of it"
  [form]
  (let [writer (java.io.StringWriter.)]
    (pp/pprint form writer)
    (str/trim-newline (.toString writer))))

(defn html-file-name
  "Returns the file name to be used for the docs for ns"
  [ns]
  (str (str/replace (name ns) #"[-.]" "_") ".html"))

;; The original is at from https://github.com/richhickey/clojure-contrib/blob/a1c66df5287776b4397cf3929a5f498fbb34ea32/src/main/clojure/clojure/contrib/java_utils.clj#L185
(defn delete-file-recursively
  "Delete file f. If it's a directory, recursively delete all its contents.
Raise an exception if any deletion fails unless silently is true.
Adapted from clojure.contrib.java-utils/delete-file-recursively."
  [f & [silently]]
  (let [f (io/file f)]
    (if (.isDirectory f)
      (doseq [child (.listFiles f)]
        (delete-file-recursively child silently)))
    (io/delete-file f silently)))
