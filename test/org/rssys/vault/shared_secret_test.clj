(ns org.rssys.vault.shared-secret-test
  (:require [clojure.test :refer :all]
            [matcho.core :refer [match not-match]]
            [org.rssys.vault.shared-secret :as sut]))

(deftest ^:unit gen-random-bytes-test
  (testing "random data is not equal"
    (let [r1 (sut/gen-random-bytes 10)
          r2 (sut/gen-random-bytes 10)]
      (is (not (= (seq r1) (seq r2)))))))

(deftest ^:unit gen-secure-password-test
  (testing "string is 128 bit strength and random"
    (let [p1 (sut/gen-secure-password)
          p2 (sut/gen-secure-password)]
      (not-match p1 p2)
      (match (.length p1) 32)
      (match (.length p2) 32))))

(deftest ^:unit generate-shared-secret-test
  (testing "gen/restore secret based on quorum"
    (let [N      10
          secret (sut/gen-secure-password)]
      (println "secure password:" secret "max participants: " N)
      (into [] (for [n (range 2 N)
             k (range 2 N)
             :when (>= n k)]
         (let [shared-sec-vec  (sut/generate-shared-secret secret n k)
               shuffled-sec    (shuffle shared-sec-vec)
               quorum          (take k shuffled-sec)
               restored-secret (sut/restore-secret quorum)]
           (println "restored password: " restored-secret "quorum:" k "max:" n)
           (match restored-secret secret)))))))
