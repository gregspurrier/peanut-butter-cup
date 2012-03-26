(ns peanut-butter-cup.test.templates.namespace
  (:use peanut-butter-cup.templates.namespace
        midje.sweet
        midje.util))

(expose-testables peanut-butter-cup.templates.namespace)

(fact "about usage-str"
  (usage-str 'foo '(bar & args))
  => "(foo bar & args)")