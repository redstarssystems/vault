(ns ^{:author "Mike Ananev"}
  org.rssys.vault.config
  (:require [config.core :refer [load-env] :as conf]
            [mount.core :refer [defstate] :as mount]
            [io.pedestal.log :as log]
            [clojure.string :as str]
            [org.rssys.vault.util :as util]
            [clojure.edn :as edn]
            [clojure.spec.alpha :as s]
            [clojure.java.io :as io]))

;; this is the only place where system reads its config from external world
;; and build central config map

(s/def :config/not-empty-string? #(and (string? %) (not (str/blank? %))))
(s/def :config/web-or-folder #{"folder" "web"})
(s/def :config/pos-int pos-int?)
(s/def :config/boolean boolean?)
(s/def :config/h2-or-pg #{"h2" "pg12"})

(def config-spec {:db/type              :config/h2-or-pg
                  :db/user              :config/not-empty-string?
                  :db/secret-folder     :config/not-empty-string?
                  :db/secret-from       :config/web-or-folder
                  :db/filename          :config/not-empty-string?
                  :db/first-start?      :config/boolean})

(defn validate-config
  "check config for valid options.
  returns config value (untouched) if config is valid, otherwise throws Exception (program should be terminated)."
  [config config-spec]
  (let [result     (util/match-explain config config-spec)
        q          (:db/secret-quorum-num config)
        msg-quorum "quorum must be less or equal to total number of participants"]

    (when-not (= result :success)
      (let [cause-data (edn/read-string result)
            cause      (mapcat #((juxt :expected :path) %) cause-data) ;; here we prevent sensitive data leak to logs
            cause-vec  (vec (partition 2 cause))]
        (log/error :msg "config validation" :result "not valid" :cause cause-vec)
        (throw (ex-info "config is not valid" {:cause cause-vec}))))
    )

  config)

(defn build-config
  "return config based on data from OS & JVM env/properties"
  [env]
  (log/info :msg "configuration" :status "loading")
  (let [config {
                :config/type          (:vaultconfigtype env)

                ;; database properties
                :db/type              (:dbtype env)
                :db/user              (:dbuser env)
                :db/secret-folder     (:dbsecretfolder env)
                :db/secret-from       (:dbsecretfrom env)
                :db/filename          (:dbfilename env)

                ;; if db is not exist then we should create db and perform init protocol
                :db/first-start?      (not (.exists (io/file (:dbfilename env))))

                }]
    (log/info :msg "configuration" :status "loaded" :type (:config/type config))
    config))

(defstate ^:dynamic *config*
  :start (validate-config (build-config (conf/load-env)) config-spec)
  :stop nil)

(comment
  (mount/start #'*config*)
  (mount/stop #'*config*))

