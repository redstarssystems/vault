(ns ^{:author "Mike Ananev"}
  org.rssys.vault.core
  (:gen-class)
  (:require [mount.core :as mount]
            [org.rssys.vault.config]
            [org.rssys.vault.db.core]
            [org.rssys.vault.i18n.core :refer [translate]]
            [io.pedestal.log :as log]))

(defn -main
  "entry point to program."
  [& args]
  (log/info :msg (translate :en :core/system-start))
  (mount/start)

  (mount/stop)
  (log/info :msg (translate :en :core/system-stop))
  (System/exit 0))
