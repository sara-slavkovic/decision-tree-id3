(ns clothing-recommender.product-repository
  (:require [next.jdbc :as jdbc]
            [clothing-recommender.db :as db]))

(defn find-all
  []
  (jdbc/execute! db/ds
                 ["SELECT * FROM products"]))

(defn find-by-category
  [category]
  (jdbc/execute! db/ds
                 ["SELECT * FROM products WHERE category = ?" category]))

(defn find-by-max-price
  [max-price]
  (jdbc/execute! db/ds
                 ["SELECT * FROM products WHERE price <= ?" max-price]))

(defn find-top-rated
  [min-rating]
  (jdbc/execute! db/ds
                 ["SELECT * FROM products WHERE rating >= ? ORDER BY rating DESC" min-rating]))