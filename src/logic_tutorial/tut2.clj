(ns logic-tutorial.tut2
  (:refer-clojure :exclude [reify inc ==])
  (:use [clojure.core.logic]))


;; myappendo is the same as appendo in core.logic
(defne myappendo [x y z]
  ([() _ y])
  ([[?a . ?d] _ [?a . ?r]] (myappendo ?d y ?r)))
