(ns org.rssys.vault.shared-secret
  (:require [mount.core :as mount]
            [org.rssys.vault.config :as config]
            [io.pedestal.log :as log])
  (:import (org.bouncycastle.util.encoders Hex)
           (com.tiemens.secretshare.engine SecretShare)
           (java.security SecureRandom)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; private functions

(defn- prime384 []
  (SecretShare/getPrimeUsedFor384bitSecretPayload))

;; we will use 256 bit key as password for DB encryption (AES 128 in H2),
;; so prime for 384 bit payload is enough
(def current-prime (prime384))

(defn- public-info
  "Return all the publicly available information about a secret share.
   both required and optional information.
   n - total number of participants for shared secret.
   k - quorum (n >= k) which is required to restore secret from shared secret."
  ([n k description]
   (public-info n k current-prime description))
  ([n k]
   (public-info n k current-prime nil))
  ([n k m description]
   (com.tiemens.secretshare.engine.SecretShare$PublicInfo. (int n) k m description)))

(defn- ss
  "init SharedSecret engine with public info parameters."
  [pi]
  (com.tiemens.secretshare.engine.SecretShare. pi))

(defn- si->tok
  "convert shared secret to String representation.
  returns String."
  [si]
  (let [pi (.getPublicInfo si)]
    (str (.getIndex si) ":" (.getN pi) ":" (.getK pi) ":" (.toString (.getShare si) 36))))

(defn- tok->si
  "convert String representation of shared secret to Java object."
  [tok]
  (let [data  (clojure.string/split tok #":")
        index (java.lang.Long/parseLong (first data))
        n     (java.lang.Long/parseLong (get data 1))
        k     (java.lang.Long/parseLong (get data 2))
        share (java.math.BigInteger. (get data 3) 36)]
    (com.tiemens.secretshare.engine.SecretShare$ShareInfo. index share
      (public-info n k))))

(defn- split
  "Generate shared secrets based on given data. data should be java.math.BigInteger number.
  So if we have 32 bytes array of random data we should convert it to BigInteger number.
  returns sequence of Strings (shared secrets in String representation)."
  ([pi data]
   (split pi data si->tok))
  ([pi data f]
   (mapv f
     (.getShareInfos
       (.split (ss pi) data)))))

(defn- combine
  "restore secret based on quorum of shared secrets sequence. shares should be collection of Strings.
  returns ^java.math.BigInteger number as secret."
  ([shares]
   (combine shares tok->si))
  ([shares f]
   (let [shares (map f shares)
         pi     (.getPublicInfo (first shares))]
     (.getSecret
       (.combine (ss pi) (vec shares))))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; public functions

(defn gen-random-bytes
  "generate random bytes using SecureRandom class.
 return random bytes array length of n."
  [^long n]
  (let [rand-engine (SecureRandom.)
        barr        (byte-array n)
        _           (.nextBytes rand-engine barr)]
    barr))

(defn gen-secure-password
  "generate random String 128 bits strength."
  []
  (String. (Hex/encode (gen-random-bytes 16))))

(defn generate-shared-secret
  "generate shared secret based on given ^String secret.
  n - total number of participants with shared secret.
  k - quorum need to restore secret from shared secret (n >= k)
  returns collection of Strings with shared secrets"
  [^String secret-password ^long n ^long k]
  {:pre [(>= n k)]}
  (let [bignum (biginteger (.getBytes secret-password))
        pi     (public-info n k)]
    (split pi bignum)))

(defn restore-secret
  "restore secret from given collection of shared keys.
  returns String with secret if success or throws Exception with errors."
  [shared-coll]
  (String. (.toByteArray (combine shared-coll))))

(defn load-secret
  "load secret based on global config parameters
  return secret as string"
  [config]
  (if (:db/first-start? config)
    (gen-secure-password)
    ))

(mount/defstate ^:dynamic *secret-password*
  :start (load-secret config/*config*)
  :stop nil)
