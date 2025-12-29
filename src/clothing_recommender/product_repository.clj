(ns clothing-recommender.product-repository
  (:require [next.jdbc :as jdbc]
            [clothing-recommender.db :as db]))

(defn normalize-product
  [p]
  {:product-id   (:PRODUCTS/PRODUCT_ID p)
   :product-name (:PRODUCTS/PRODUCT_NAME p)
   :brand        (:PRODUCTS/BRAND p)
   :category     (:PRODUCTS/CATEGORY p)
   :price        (:PRODUCTS/PRICE p)
   :rating       (:PRODUCTS/RATING p)
   :color        (:PRODUCTS/COLOR p)
   :size         (:PRODUCTS/SIZE p)})

(defn find-all
  []
  (->> (jdbc/execute! db/ds ["SELECT * FROM products"])
       (map normalize-product)))

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