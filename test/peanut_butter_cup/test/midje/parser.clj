(ns peanut-butter-cup.test.midje.parser
  (:use peanut-butter-cup.midje.parser
        midje.sweet
        midje.util))

(expose-testables peanut-butter-cup.midje.parser)

(facts "about midje-to-pbc"
  (midje-to-pbc {:expect {:description ["about foo"]
                          :call-form '(foo bar)
                          :arrow '=>
                          :expected-result-text-for-failures "99"}})
  => {:description ["about foo"]
      :lhs "(foo bar)"
      :arrow "=>"
      :rhs "\"99\""})

