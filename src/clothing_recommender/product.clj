(ns clothing-recommender.product)

(defn make-product
  [id name brand category price rating color size]
  {:product-id   id
   :product-name name
   :brand        brand
   :category     category
   :price        price
   :rating       rating
   :color        color
   :size         size
   })

(defn valid-product?
  [p]
  (and (:product-id p)
       (:product-name p)
       (:brand p)
       (:category p)
       (number? (:price p))
       (number? (:rating p))
       ))