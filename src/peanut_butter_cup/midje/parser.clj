(ns peanut-butter-cup.midje.parser
  (:use [clojure.core.match :only (match)]
        [clojure.pprint :only (pprint)])
  (:require [clojure.string :as str]))

(defn- ^:testable form-seq
  "Returns the forms read from rdr as a lazy sequence. rdr must implement
java.io.BufferedReader"
  [^java.io.BufferedReader rdr]
  (when-let [form (read rdr false nil)]
    (cons form (lazy-seq (form-seq rdr)))))

(defn- ^:testable about-string?
  "Returns true if the s is a string of the form \"about ...\""
  [s]
  (and (string? s)
       (boolean (re-matches #"^about\s+.+" s))))

(defn- ^:testable fn-name-from-about
  "Extracts the name of the function, as a symbol, from an about string"
  [s]
  (symbol (nth (str/split s #"\s+") 1)))

(defn- doc-str?
  [x]
  (and (map? x) (contains? x :doc)))

(defn- expectation?
  [x]
  (and (map? x) (contains? x :lhs)))

(defn- prereq?
  [x]
  (and (map? x) (contains? x :prereqs)))

(defn- parse-prereq-clauses
  [ps]
  (match [ps]
         [([lhs '=> rhs & more] :seq)] (cons {:lhs lhs :rhs rhs}
                                             (parse-prereq-clauses more))))

(defn- ^:testable extract-fact-clauses
  "Given the tail of a fact expression, extracts the clauses that peanut butter
cup cares about:
  - doc strings
  - top-level expectations
  - prerequisites
Other clauses are ignored."
  [cs]
  (match [cs]
         [([(s :when string?) & more] :seq)]
         (cons {:doc s} (extract-fact-clauses more))
         [([lhs '=> rhs & more] :seq)]
         (cons {:lhs lhs :rhs rhs} (extract-fact-clauses more))
         [([(['provided & clauses] :seq) & more] :seq)]
         (cons {:prereqs (parse-prereq-clauses clauses)}
               (extract-fact-clauses more))
         [([_ & more] :seq)] (if (seq more) (extract-fact-clauses more))))

(defn- ^:testable combine-fact-clauses
  "Combines a seq of fact clauses such that expectations are merged with doc
strings that immediately precede them and prerequisites that immediately
follow them. Doc strings and prerequisites that are found in unexpected
posisitons are ignored."
  [cs]
  (match [cs]
         [([(d :when doc-str?) (e :when expectation?) (p :when prereq?) & more] :seq)]
         (cons (merge d e p) (combine-fact-clauses more))
         [([(d :when doc-str?) (e :when expectation?) & more] :seq)]
         (cons (merge d e) (combine-fact-clauses more))
         [([(e :when expectation?) (p :when prereq?) & more] :seq)]
         (cons (merge e p) (combine-fact-clauses more))
         [([(e :when expectation?) & more] :seq)]
         (cons e (combine-fact-clauses more))
         [([_ & more] :seq)]
         (if (seq more) (combine-fact-clauses more))))

(defn- parse-fact-clauses
  [cs]
  (-> cs extract-fact-clauses combine-fact-clauses))

(defn parse-fact-form
  "Parses a Midje fact form and extracts a sequence of fact maps. Each map
contains :lhs and :rhs keys whose values are the left-hand side and right-hand
side of the fact, respectively."
  [fact]
  (match [fact]
         [([(:or 'fact 'facts) (s :when about-string?) & clauses] :seq)]
         {:about (fn-name-from-about s) :clauses (parse-fact-clauses clauses)}
         [([(:or 'fact 'facts) & clauses] :seq)]
         {:clauses (parse-fact-clauses clauses)}))

(defn parse
  "Reads and parses Midje fact forms from rdr. Non-fact forms are ignored."
  [rdr]
  (keep parse-fact-form (form-seq (java.io.PushbackReader. rdr))))