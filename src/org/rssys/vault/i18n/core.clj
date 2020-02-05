(ns org.rssys.vault.i18n.core
  (:require [tongue.core :as tongue]
            [org.rssys.vault.i18n.lang.en :as en]))

(def dictionary
  {:en              en/dictionary
   :tongue/fallback :en})

(def translate (tongue/build-translate dictionary))