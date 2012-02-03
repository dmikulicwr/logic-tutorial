(ns logic-tutorial.tut2
  (:refer-clojure :exclude [reify inc ==])
  (:use [clojure.core.logic]))

(defn myappendo [l1 l2 o]
  (conde
    ((== l1 ()) (== l2 o))
    ((fresh [a d r]
       (conso a d l1)
       (conso a r l2)
       (appendo d l2 r)))))
