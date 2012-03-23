(ns peanut-butter-cup.midje.parser
  (:use [clojure.core.match :only (match)]
        [clojure.pprint :only (pprint)]))

(defn- ^:testable form-seq
  "Returns the forms read from rdr as a lazy sequence. rdr must implement
java.io.PushbackReader"
  [^java.io.PushbackReader rdr]
  (when-let [form (read rdr false nil)]
    (cons form (lazy-seq (form-seq rdr)))))

(defn- ^:testable about-string?
  "Returns true if the s is a string of the form \"about ...\""
  [s]
  (and (string? s)
       (boolean (re-matches #"^about\s+.+" s))))

(defn- parse-prereq-clauses
  [ps]
  (match [ps]
         [([lhs '=> rhs & more] :seq)] (cons {:lhs lhs :rhs rhs}
                                             (parse-prereq-clauses more))))

(defn- parse-fact-clauses
  [cs]
  (match [cs]
         [([lhs '=> rhs (['provided & prereqs] :seq) & more] :seq)]
         (cons {:lhs lhs :rhs rhs :prereqs (parse-prereq-clauses prereqs)}
               (parse-fact-clauses more))
               
         [([lhs '=> rhs & more] :seq)] (cons {:lhs lhs :rhs rhs}
                                             (parse-fact-clauses more))))

(defn parse-fact-form
  "Parses a Midje fact form and extracts a sequence of fact maps. Each map
contains :lhs and :rhs keys whose values are the left-hand side and right-hand
side of the fact, respectively."
  [fact]
  (match [fact]
         [([(:or 'fact 'facts) (s :when about-string?) & clauses] :seq)]
         {:doc s :clauses (parse-fact-clauses clauses)}
         [([(:or 'fact 'facts) & clauses] :seq)]
         {:clauses (parse-fact-clauses clauses)}))