(ns ^{:author "Mike Ananev"}
  org.rssys.vault.util
  (:require [matcho.core :as matcho]))

(defmacro match?
  "Match against each pattern and return true if match and false if not"
  [x & pattern]
  `(let [x# ~x
         patterns# [~@pattern]
         errors# (apply matcho/match* x# patterns#)]
     (empty? errors#)))

(defmacro not-match?
  "Match against each pattern and return true if not match and false match."
  [x & pattern]
  `(let [x# ~x
         patterns# [~@pattern]
         errors# (apply matcho/match* x# patterns#)]
     (not (empty? errors#))))

(defmacro match-explain
  "Match against each pattern and return :success if match and String explanation if not"
  [x & pattern]
  `(let [x# ~x
         patterns# [~@pattern]
         errors# (apply matcho/match* x# patterns#)]
     (if-not (empty? errors#)
       (pr-str errors# x# patterns#)
       :success)))

(comment
  (match? {:a 1 :b 2} {:a pos-int? :b 2})
  (not-match? {:a 1 :b 2} {:a pos-int? :b 3})
  (match-explain {:a 1 :b 2} {:a 1 :b 2})
  (match-explain {:a 1 :b 2} {:a 1 :b 3})
  )
