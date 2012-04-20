(ns peanut-butter-cup.app
  (:use [bultitude.core :only (namespaces-in-dir)])
  (:require [peanut-butter-cup.core :as core]
            [peanut-butter-cup.templates.namespace :as ns-template]
            [peanut-butter-cup.util :as util]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(def ^:dynamic *pbc-doc-root* "pbc-doc")

(defn args-to-namespaces
  "Returns the namespaces that correspond to the given command-line
arguments. If args is empty, returns all namespaces found under the
src directory."
  [args]
  (if (seq args)
    (map symbol args)
    (namespaces-in-dir "src")))

(defn combined-ns-docs
  [ns]
  (ns-template/render-ns-html ns (core/get-fn-metadata-and-facts ns)))

(defn reset-pbc-doc-directory!
  "Recreates the pbc-doc directory and installs the static assets."
  []
  (let [root (io/file *pbc-doc-root*)]
    (util/delete-file-recursively root true)
    (.mkdirs root)
    (->> (slurp "resources/pbc-assets/styles.css")
         (spit (str *pbc-doc-root* "/styles.css")))))

(defn -main
  [& args]
  (reset-pbc-doc-directory!)
  (doseq [ns (args-to-namespaces args)]
    (require ns)
    (spit (str *pbc-doc-root* "/" (util/html-file-name ns))
          (combined-ns-docs ns))))