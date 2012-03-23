(ns peanut-butter-cup.test.midje.parser
  (:use peanut-butter-cup.midje.parser
        midje.sweet
        midje.util)
  (:import [java.io PushbackReader StringReader]))

(expose-testables peanut-butter-cup.midje.parser)

(facts "about form-seq"
  (form-seq (PushbackReader. (StringReader. "")))
  => nil
  (form-seq (PushbackReader. (StringReader. "(a b c)(d e f)")))
  => '((a b c) (d e f)))

(comment
  ;; Pending resolution of https://github.com/marick/Midje/issues/117
  (facts "about parse-fact-form"
    (parse-fact-form '(fact ..lhs.. => ..rhs..))
    => [{:lhs ..lhs.. :rhs ..rhs..}]
    (parse-fact-form '(fact ..lhs1.. => ..rhs1.. ..lhs2.. => ..rhs2..))
    => [{:lhs ..lhs1.. :rhs ..rhs1..}
        {:lhs ..lhs2.. :rhs ..rhs2..}]
    (parse-fact-form '(fact ..lhs.. => ..rhs..
                        (provided ..p-lhs.. => ..p-rhs..)))
    => [{:lhs ..lhs1.. :rhs ..rhs1.. :prereqs [{:lhs ..p-lhs..
                                                :rhs ..p-rhs..}]}
        {:lhs ..lhs2.. :rhs ..rhs2..}])
  )
