(ns org.rssys.vault.db-test
  (:require [clojure.test :refer :all]
            [matcho.core :refer [match]]
            [mount.core :as mount]
            [org.rssys.vault.db.core :as db]
            [clojure.string :as str]
            [org.rssys.vault.fixtures.config :as fixture.conf]
            [org.rssys.vault.fixtures.db :as fixture.db]))

(use-fixtures :once
  fixture.conf/config-fixture
  fixture.db/db-spec-fixture)

(deftest ^:unit h2-db-encryption-enabled
  (testing "h2 db files encryption is enabled"
    (is (str/includes? (:jdbcUrl db/*db-spec*) "CIPHER=AES"))))

(deftest ^:integration make-connection-pool-test
  )
