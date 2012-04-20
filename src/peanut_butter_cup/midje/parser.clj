(ns peanut-butter-cup.midje.parser
  "Extraction of examples from Midje facts."
  (:use [peanut-butter-cup.util :only (form-to-str)])
  (:require [midje.semi-sweet :as mss]))

(defn- make-fact-extractor
  "Returns a function suitable for binding to *expect-checking-fn* that will
append fact maps to the provided atom."
  [a]
  (fn [e f]
    (swap! a conj {:expect e :fakes f})
    :do-nothing))

(defn- extract-midje-facts
  "Returns a sequence of fact maps extracted from the Midje tests found in the
provided namespace. Each entry is a map containing the expectation
map (:expect) and the fakes map (:fakes), if any."
  [ns]
  (let [facts (atom [])]
    (binding [mss/*expect-checking-fn* (make-fact-extractor facts)]
      (try
        (require ns :reload)
        (catch java.io.FileNotFoundException e
          ;; No tests were found. Remain calm and carry on.
          nil)))
    @facts))

(defn- remove-undocumented-facts
  [facts]
  (filter #(seq (get-in % [:expect :description])) facts))

(defn- ^:testable midje-to-pbc
  "Converts Midje expectation and fake maps into the fact clause representation
used internally by Peanut Butter Cup."
  [{:keys [expect fakes]}]
  {:description (:description expect)
   :lhs (form-to-str (:call-form expect))
   :arrow (form-to-str (:arrow expect))
   :rhs (form-to-str (:expected-result-text-for-failures expect))})

(defn parse
  "Extracts facts from a Midje test namespace and returns them as a map
from top-level doc string to sequence of facts. Facts without doc strings
are ignored."
  [ns]
  (->> (extract-midje-facts ns)
       remove-undocumented-facts
       (map midje-to-pbc)
       (group-by #(first (:description %)))))