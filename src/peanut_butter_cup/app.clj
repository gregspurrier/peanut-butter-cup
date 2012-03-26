(ns peanut-butter-cup.app
  (:require [peanut-butter-cup.core :as core]
            [peanut-butter-cup.templates.namespace :as ns-template]
            [clojure.pprint :as pp]))

(defn -main
  [& args]
  (doseq [arg args]
    (let [ns (symbol arg)]
      (require ns)
      (println (apply str (ns-template/template
                           ns
                           (core/get-fn-metadata-and-facts ns)))))))