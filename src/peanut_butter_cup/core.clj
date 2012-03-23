(ns peanut-butter-cup.core
  (:require [peanut-butter-cup.util :as util]
            [peanut-butter-cup.midje.parser :as pmp]))

(defn read-midje-facts
  [ns]
  (with-open [rdr (clojure.java.io/reader (util/name-to-test-path ns))]
    (doall (filter :about (pmp/parse rdr)))))

(defn get-public-metadata
  [ns]
  (map meta (vals (ns-publics ns))))

(defn get-fn-metadata-and-facts
  "Given a namespace, returns a seq of metadata for public vars that has
been augmented with facts extracted from the corresponding midje test file."
  [ns]
  (let [fact-map (group-by :about (read-midje-facts ns))]
    (map #(assoc % :facts (flatten (map :clauses (fact-map (:name %)))))
         (get-public-metadata ns))))