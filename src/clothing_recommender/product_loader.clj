(ns clothing-recommender.product-loader
  (:require [clojure.java.io :as io]
            [clojure.data.csv :as csv]
            [clothing-recommender.product :as product]))

(defn load-products
  "Reads products from a CSV file and converts them into product maps"
  [file-path]
  (let [rows (with-open [reader (io/reader file-path)]
               (doall (csv/read-csv reader)))
        header (map keyword (first rows))
        data   (rest rows)]
    (map (fn [row]
           (let [[user-id pid name brand cat price rating color size] row]
             (product/make-product
               (Integer/parseInt pid)
               name
               brand
               cat
               (Double/parseDouble price)
               (Double/parseDouble rating)
               color
               size)))
         data)))

;;(require '[clothing-recommender.product-loader :as csv])
;
;(def products (csv/load-products "C:\\Users\\Korisnik\\Desktop\\mas clojure\\fashion_products.csv"))