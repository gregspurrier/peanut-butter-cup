(ns peanut-butter-cup.templates.namespace
  "Template for the documentation page for a namespace"
  (:require [net.cgrand.enlive-html :as html])
  (:import [com.petebevin.markdown MarkdownProcessor]))

(def ^:private namespace-html
  (html/html-resource "peanut_butter_cup/templates/namespace.html"))

(defn- ^:testable usage-str
  "Given a function name and an arglist returns a usage string."
  [name args]
  (print-str (cons name args)))

(defn- namespace-doc
  "Returns the doc string, if any, associated with the namespace"
  [ns]
  (if-let [ns-meta (meta (find-ns ns))]
    (:doc ns-meta)))

(defn- format-doc-string
  [s]
  (.markdown (MarkdownProcessor.) s))

(html/defsnippet usage-item namespace-html
  [:#publics [:.var html/first-of-type] :.usages [:li html/first-of-type]]
  [name args]
  [:.usage] (html/content (usage-str name args)))

(html/defsnippet usages namespace-html
  [:#publics [:.var html/first-of-type] :.usages]
  [fn-meta]
  [:ul] (html/content (map (partial usage-item (:name fn-meta))
                           (:arglists fn-meta))))

(html/defsnippet example-item namespace-html
  [:#publics [:.var html/first-of-type] [:.example html/first-of-type]]
  [e]
  [:.lhs] (html/content (print-str (:lhs e)))
  [:.rhs] (html/content (print-str (:rhs e))))

(html/defsnippet examples namespace-html
  [:#publics [:.var html/first-of-type] :.examples]
  [facts]
  [:ul] (html/content (map example-item facts)))

(html/defsnippet var-item namespace-html
  [:#publics [:.var html/first-of-type]]
  [fn-meta]
  [:.name] (html/content (name (:name fn-meta)))
  [:.usages] (if (seq (:arglists fn-meta))
               (html/content (usages fn-meta)))
  [:.doc-string] (if-let [ds (:doc fn-meta)]
                  (html/html-content (format-doc-string ds)))
  [:.examples] (if-let [facts (seq (:facts fn-meta))]
                 (html/content (examples facts))))

(html/deftemplate template namespace-html
  [ns fn-metas]
  [:#namespace] (html/content (name ns))
  [:#namespace-doc] (if-let [d (namespace-doc ns)]
                      (html/html-content (format-doc-string d)))
  [:#publics] (html/content (map var-item fn-metas)))

(defn render-ns-html
  "Renders a namespace and its associated metadata as an HTML document string"
  [ns fn-metas]
  (apply str (template ns fn-metas)))