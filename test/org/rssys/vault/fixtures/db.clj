(ns org.rssys.vault.fixtures.db
  (:require [clojure.test :refer :all]
            [io.pedestal.log :as log]
            [mount.core :as mount]
            [org.rssys.vault.fixtures.config :as fixture.conf]
            [org.rssys.vault.db.core :as db]))

(use-fixtures :once fixture.conf/config-fixture)

(defn db-spec-fixture
  "Start/stop db spec fixture."
  [f]
  (mount/start #'db/*db-spec*)
  (f)
  (mount/stop #'db/*db-spec*))