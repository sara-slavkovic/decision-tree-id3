(ns clothing-recommender.db
  (:require [next.jdbc :as jdbc]))

;; DATABASE CONFIG
(def db-spec
  {:dbtype "h2"
   :dbname "fashion-db"})

(def ds
  (jdbc/get-datasource db-spec))

;; SCHEMA
(defn create-products-table!
  []
  (jdbc/execute!
    ds
    ["CREATE TABLE IF NOT EXISTS products (
        product_id INT,
        product_name VARCHAR(255),
        brand VARCHAR(255),
        category VARCHAR(255),
        price DOUBLE,
        rating DOUBLE,
        color VARCHAR(50),
        size VARCHAR(10)
      )"]))