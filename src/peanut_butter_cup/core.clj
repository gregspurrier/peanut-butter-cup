(ns peanut-butter-cup.core
  (:require [peanut-butter-cup.util :as util]
            [peanut-butter-cup.midje.parser :as pmp]))

(defn get-public-metadata
  [ns]
  (map meta (vals (ns-publics ns))))

(defn get-fn-metadata-and-facts
  "Given a namespace, returns a seq of metadata for public vars that has
been augmented with facts extracted from the corresponding midje test file."
  [ns]
  (let [fact-map (pmp/parse (util/name-to-test-name ns))]
    (map #(assoc % :facts (fact-map (str "about " (name (:name %)))))
         (get-public-metadata ns))))