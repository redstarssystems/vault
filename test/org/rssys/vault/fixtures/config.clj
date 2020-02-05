(ns org.rssys.vault.fixtures.config
  (:require [clojure.test :refer :all]
            [io.pedestal.log :as log]
            [mount.core :as mount]
            [org.rssys.vault.config :as conf]))

;; global vars
(def test-resources-root "test/resources")

(defn config-setup
  "init test config."
  []
  (log/info :msg "test config setup in progress...")
  (mount/start #'conf/*config*)
  (log/info :msg "test config setup complete."))

(defn config-cleanup
  "cleanup test config."
  []
  (log/info :msg "test config cleanup in progress...")
  (mount/stop #'conf/*config*)
  (log/info :msg "test config cleanup complete."))

(defn config-fixture
  "config setup/cleanup fixture."
  [f]
  (config-setup)
  (f)
  (config-cleanup))