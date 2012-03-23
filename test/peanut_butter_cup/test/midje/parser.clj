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

(facts "about about-string?"
  (about-string? "about foo")
  => true
  (about-string? "something else")
  => false
  (about-string? '(x y z))
  => false)

(facts "about fn-name-from-about"
  (fn-name-from-about "about make-monkey")
  => 'make-monkey)

(facts "about extract-fact-caluses"
  (extract-fact-clauses ["hello"])
  => '({:doc "hello"})
  (extract-fact-clauses '(..lhs.. => ..rhs..))
  => '({:lhs ..lhs.. :rhs ..rhs..})
  (extract-fact-clauses '((provided ..lhs.. => ..rhs..)))
  => '({:prereqs ({:lhs ..lhs.. :rhs ..rhs..})}))

(facts "about combine-fact-clauses"
  (combine-fact-clauses [{:doc ..d..} {:lhs ..l..} {:prereqs ..p..}])
  => '({:doc ..d.. :lhs ..l.. :prereqs ..p..})
  (combine-fact-clauses [{:doc ..d..} {:lhs ..l..}])
  => '({:doc ..d.. :lhs ..l..})
  (combine-fact-clauses [{:lhs ..l..} {:prereqs ..p..}])
  => '({:lhs ..l.. :prereqs ..p..})
  (combine-fact-clauses [{:lhs ..l..}])
  => '({:lhs ..l..})
  (combine-fact-clauses [{:lhs ..l1..} {:lhs ..l2..}])
  => '({:lhs ..l1..} {:lhs ..l2..})
  (combine-fact-clauses [{:doc ..d..} {:lhs ..l1..} {:lhs ..l2..} {:prereqs ..p..}])
  => '({:doc ..d.. :lhs ..l1..} {:lhs ..l2.. :prereqs ..p..}))

(comment
  ;; Pending resolution of https://github.com/marick/Midje/issues/117
  (facts "about parse-fact-form"
    (parse-fact-form '(fact ..lhs.. => ..rhs..))
    => {:clauses [{:lhs ..lhs.. :rhs ..rhs..}]}
    (parse-fact-form '(fact "about some-fn" ..lhs.. => ..rhs..))
    => {:about 'some-fn :clauses [{:lhs ..lhs.. :rhs ..rhs..}]}
    (parse-fact-form '(fact ..lhs1.. => ..rhs1..
                        "one more thing"
                        ..lhs2.. => ..rhs2..))
    => {:clauses [{:lhs ..lhs1.. :rhs ..rhs1..}
                   {:doc "one more thing" :lhs ..lhs2.. :rhs ..rhs2..}]}
    (parse-fact-form '(fact ..lhs.. => ..rhs..
                        (provided ..p-lhs.. => ..p-rhs..)))
    => {:clauses [{:lhs ..lhs1.. :rhs ..rhs1.. :prereqs [{:lhs ..p-lhs..
                                                          :rhs ..p-rhs..}]}
                  {:lhs ..lhs2.. :rhs ..rhs2..}]})
  )

(facts "about parse"
  (parse (StringReader. "(fact \"about foo\" (foo 37) => 42)"))
  => [{:about 'foo :clauses [{:lhs '(foo 37) :rhs 42}]}]
  "non-fact forms are ignored"
  (parse (StringReader. "(ns monkey)(fact \"about foo\" (foo 37) => 42)(bar)"))
  => [{:about 'foo :clauses [{:lhs '(foo 37) :rhs 42}]}])