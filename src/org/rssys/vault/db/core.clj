(ns ^{:author "Mike Ananev"}
  org.rssys.vault.db.core
  (:require [next.jdbc :as jdbc]
            [next.jdbc.connection :as connection]
            [mount.core :refer [defstate] :as mount]
            [io.pedestal.log :as log]
            [hikari-cp.core :as pool]
            [org.rssys.vault.config :refer [*config*]]
            [org.rssys.vault.db.h2 :as h2])
  (:import (com.zaxxer.hikari HikariDataSource)))

(defstate ^:dynamic *db-spec*
  :start h2/h2-db-spec
  :stop nil)

(defn check-connection
  "check db available.
  return :success if connection established or throws exception if not."
  [db-spec pooled-ds]
  (log/info :msg "db connection" :status "verifying" :jdbcUrl (:jdbcUrl db-spec))
  (jdbc/execute! pooled-ds ["select 1;"])
  (log/info :msg "db connection" :status "connected")
  :success)

(defn make-connection-pool
  "create jdbc connection pool based on given db spec.
  return ^HikariDataSource object with established db connection pool."
  ^HikariDataSource
  [db-spec]
  (let [pooled-data-source (connection/->pool HikariDataSource db-spec)]
    (check-connection db-spec pooled-data-source)
    pooled-data-source))

(defn close-connection-pool
  "close ^HikariDataSource connection pool.
  return nil."
  [^HikariDataSource pool-ds]
  (when pool-ds
    (.close pool-ds)
    (log/info :msg "db connection" :status "closed")))

(defstate ^:dynamic ^{:on-reload :noop} *pool-ds*
  :start (make-connection-pool *db-spec*)
  :stop (close-connection-pool *pool-ds*))

(comment
  (mount/start #'*config*)
  (mount/start #'*db-spec* #'*pool-ds*)
  (mount/stop #'*db-spec* #'*pool-ds*))
