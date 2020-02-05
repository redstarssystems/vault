(ns ^{:author "Mike Ananev"}
  org.rssys.vault.core
  (:gen-class)
  (:require [mount.core :as mount]
            [org.rssys.vault.config]
            [org.rssys.vault.db.core]))

(defn -main
  "entry point to program."
  [& args]
  (mount/start)
  (println "system started.")

  (mount/stop)
  (System/exit 0))
