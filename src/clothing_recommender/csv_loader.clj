(ns clothing-recommender.csv-loader
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as str]))

;;---------------------------------------
;; Helper functions
;; --------------------------------------

(defn lower-key
  "Converts header string to lowercase keyword"
  [s]
  (-> s str/trim str/lower-case keyword))

(defn id-column?
  "Checks whether column name contains 'id'"
  [k]
  (str/includes? (name k) "id"))

(defn sanitize-headers
  "Lowercases headers, converts to keywords and removes ID columns.
   Returns [index keyword] pairs."
  [headers]
  (->> headers
       (map lower-key)
       (map-indexed vector)
       (remove (fn [[_ k]] (id-column? k)))))

(defn parse-value
  "Parses numeric values, otherwise returns string"
  [v]
  (let [v (str/trim v)]
    (cond
      (re-matches #"^-?\d+$" v)
      (Integer/parseInt v)

      (re-matches #"^-?\d+\.\d+$" v)
      (Double/parseDouble v)

      :else v)))

;;------------------------------------------
;; Row to map (with same order as in CSV)
;; -----------------------------------------

(defn row->map
  "Converts a CSV row into an ordered map using sanitized headers"
  [indexed-headers row]
  (into (array-map)
        (for [[idx k] indexed-headers]
          [k (parse-value (nth row idx))])))

;;------------------------------------------
;; CSV to maps
;; -----------------------------------------

(defn load-csv->maps
  "Loads a CSV file and converts it to a sequence of maps.
   ID columns are excluded automatically."
  [path]
  (with-open [reader (io/reader path)]
    (let [[headers & rows] (csv/read-csv reader)
          indexed-headers  (sanitize-headers headers)]
      (doall
        (map #(row->map indexed-headers %) rows)))))