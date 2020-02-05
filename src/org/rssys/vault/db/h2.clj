(ns org.rssys.vault.db.h2
  (:require [mount.core :refer [defstate]]
            [org.rssys.vault.config :refer [*config*]]))

(def h2-db-spec {;; mandatory use AES 128 bit for database files encryption,
                 ;; cause secrets should always be in encrypted form
                 :jdbcUrl         (str "jdbc:h2:file:" (:db/filename *config*) ";CIPHER=AES")
                 :username        (:db/user *config*)
                 :password        (str (:db/encr-pwd *config*) " " (:db/user-pwd *config*))
                 :autoCommit      true
                 :maximumPoolSize 10
                 :poolName        "db-pool"})