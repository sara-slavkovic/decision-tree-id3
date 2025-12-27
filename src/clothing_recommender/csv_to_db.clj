(ns clothing-recommender.csv-to-db
  (:require
    [clojure.data.csv :as csv]
    [clojure.java.io :as io]
    [next.jdbc :as jdbc]
    [clothing-recommender.db :as db]))

(defn import-products!
  [csv-path]
  (let [rows (with-open [reader (io/reader csv-path)]
               (doall (csv/read-csv reader)))
        data (rest rows)]   ;; skips header
    (doseq [[_ pid name brand category price rating color size] data]
      (jdbc/execute!
        db/ds
        ["INSERT INTO products
          (product_id, product_name, brand, category, price, rating, color, size)
          VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
         (Integer/parseInt pid)
         name
         brand
         category
         (Double/parseDouble price)
         (Double/parseDouble rating)
         color
         size]))))