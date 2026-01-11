(ns clothing-recommender.model-store
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(def model-path "resources/model.edn")

(defn save-model
  "Saves trained ML model to disk."
  [model]
  (io/make-parents model-path)
  (spit model-path (pr-str model))
  (println "Model saved to" model-path))

(defn load-model
  "Loads trained ML model from disk."
  []
  (-> model-path
      slurp
      edn/read-string))