(ns peanut-butter-cup.test.util
  (:use peanut-butter-cup.util
        midje.sweet))

(fact "about name-to-test-path"
  (name-to-test-path 'peanut-butter-cup.util)
  => "test/peanut_butter_cup/test/util.clj")

(fact "about name-to-test-name"
  (name-to-test-name 'peanut-butter-cup.util)
  => 'peanut-butter-cup.test.util)

(facts "about form-to-str"
  (form-to-str "a")
  => "\"a\""
  (form-to-str 9)
  => "9"
  (form-to-str '(a list))
  => "(a list)"
  (form-to-str ''(a quoted list))
  => "'(a quoted list)")

(fact "about html-file-name"
  (html-file-name 'foo-bar.baz)
  => "foo_bar_baz.html")