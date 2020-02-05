(ns org.rssys.vault.config-test
  (:require [clojure.test :refer :all]
            [org.rssys.vault.fixtures.config :as fixture.conf]
            [org.rssys.vault.config :refer [build-config]]))

(use-fixtures :once
  fixture.conf/config-fixture)

(deftest build-config-test

  )
