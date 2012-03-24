(ns peanut-butter-cup.app
  (:require [peanut-butter-cup.core :as core]
            [clojure.pprint :as pp]))

(defn -main
  [& args]
  (doseq [arg args]
    (let [name (symbol arg)]
      (require name)
      (pp/pprint (core/get-fn-metadata-and-facts name)))))