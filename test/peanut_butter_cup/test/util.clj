(ns peanut-butter-cup.test.util
  (:use peanut-butter-cup.util
        midje.sweet))

(fact "about name-to-test-path"
  (name-to-test-path 'peanut-butter-cup.util)
  => "test/peanut_butter_cup/test/util.clj")