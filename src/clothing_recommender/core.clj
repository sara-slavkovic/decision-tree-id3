(ns clothing-recommender.core
  (:require [clothing-recommender.product :as product]
            [clothing-recommender.user :as user]
            [clothing-recommender.users :as users]
            [clothing-recommender.product-loader :as product-loader]
            [clothing-recommender.product-repository :as repo]
            [clothing-recommender.recommendation :as rec]))

;; clothing recommendation based on temperature
(defn simple-recommendation
      [temperature]
      (if (< temperature 15)
        "Wear a jacket."
        "A t-shirt is fine."))

;; filter warm clothes from items list
(defn filter-warm-clothes
      [items]
      (filter #(= (:type %) :warm) items))

;; filter products by brand, category and max price
(defn filter-products
  [products brand category max-price]
  (filter (fn [product]
            (and
              (or (nil? brand) (= (:brand product) brand))
              (or (nil? category) (= (:category product) category))
              (or (nil? max-price) (<= (:price product) max-price))))
          products))

;; average rating by category
(defn average-rating-by-category
  [products category]
  (let [filtered-products (filter #(= (:category %) category) products)]
    (when (not (empty? filtered-products))
      (let [ratings (map :rating filtered-products)
            sum (reduce + ratings)
            count (count ratings)]
        (/ sum count)))))

;; find most expensive product by color
(defn most-expensive-by-color
  [products color]
  (let [filtered (filter #(= (:color %) color) products)]
    (when (not (empty? filtered))
      (reduce (fn [max-product current-product]
                (if (> (:price current-product) (:price max-product))
                  current-product
                  max-product))
              (first filtered)
              (rest filtered)))))

;; group products by brand
(defn group-by-brand
  [products]
  (reduce (fn [acc product]
            (let [brand (:brand product)
                  existing-products (get acc brand [])]
              (assoc acc brand (conj existing-products product))))
          {}
          products))

;; recommend products by value
(defn recommend-by-value
  [products n]
  (let [value-scores (map (fn [product]
                            {:product product
                             :score (/ (:rating product) (:price product))})
                          products)
        sorted (sort-by :score > value-scores)
        top-n (take n sorted)]
    (map :product top-n)))

;; order by price ascending
(defn sort-by-price
  [products]
  (sort-by :price products))

;; find cheapest product by category
(defn cheapest-by-category
  [products category]
  (let [filtered (filter #(= (:category %) category) products)]
    (when (seq filtered)
      (reduce (fn [min-product p]
                (if (< (:price p) (:price min-product))
                  p
                  min-product))
              filtered))))

;; find product by name
(defn find-by-name
  [products name]
  (filter #(= (:product-name %) name) products))

;; product is within budget
(defn within-budget?
  [user product]
  (<= (:price product) (:budget user)))

;; if a product fits user's budget, size and rating
(defn eligible-product?
  [user product]
  (let [good-rating?   (>= (:rating product) 4.0)
        user-size      (get-in user [:sizes :tops])
        size-match?    (= (:size product) user-size)]
    (and (within-budget? user product)
         good-rating?
         size-match?)))


(defn -main
      [& _]
      (let [products [{:product-id 1 :product-name "Blue Jeans" :brand "Levis" 
                       :category "Pants" :price 89.99 :rating 4.5 :color "Blue" :size "M"}
                      {:product-id 2 :product-name "White T-Shirt" :brand "Nike" 
                       :category "Tops" :price 29.99 :rating 4.8 :color "White" :size "L"}
                      {:product-id 3 :product-name "Black Jacket" :brand "Levis" 
                       :category "Jackets" :price 120.00 :rating 4.2 :color "Black" :size "M"}
                      {:product-id 4 :product-name "Red Dress" :brand "Zara" 
                       :category "Dresses" :price 59.99 :rating 4.7 :color "Red" :size "S"}
                      {:product-id 5 :product-name "Blue Jeans" :brand "Nike" 
                       :category "Pants" :price 79.99 :rating 4.3 :color "Blue" :size "L"}]
            products-csv
            (doall (product-loader/load-products
                           "C:\\Users\\Korisnik\\Desktop\\mas clojure\\fashion_products.csv"))
            ]

        (println "Example 1:" (simple-recommendation 10))
        (println "Example 2:" (filter-warm-clothes
                                [{:name "Jacket" :type :warm}
                                 {:name "T-shirt" :type :light}]))
        (println "\nFilter Products:")
        (println (filter-products products "Levis" nil 100))
        (println "\nAverage Rating:")
        (println (average-rating-by-category products "Pants"))
        (println "\nMost Expensive by Color:")
        (println (most-expensive-by-color products "Blue"))
        (println "\nGroup by Brand:")
        (println (group-by-brand products))
        (println "\nRecommend by Value:")
        (println (recommend-by-value products 3))
        (println "\nSort Products by Price:")
        (println (sort-by-price products))
        (println "\nCheapest by Category:")
        (println (cheapest-by-category products "Pants"))
        (println "\nFind by Name:")
        (println (find-by-name products "Red Dress"))

        (println "\nMake product:")
        (println (product/make-product
                   1 "Blue Jeans" "Levis" "Pants" 89.99 4.5 "Blue" "M"))
        (println "\nMake user:")
        (println (user/make-user
                   10 "Sara" ["Casual" "Sporty"] {:tops "M" :pants "M" :shoes 39} 120.00))
        (println "\nUser:" users/sara)
        (println "\nAll users:" users/all-users)
        (println "\nWithin budget sara 100:")
        (println (within-budget? users/sara {:price 100}))
        (println "\nWithin budget sara 150:")
        (println (within-budget? users/sara {:price 150}))
        (println "\nEligible products (first product):")
        (println (eligible-product? users/sara (first products)))

        ;;products from csv
        (println "\nFirst product from csv file:")
        (println (first products-csv))
        ;;realtional db
        (println "\nTop rated products:")
        (println (take 5 (repo/find-top-rated 4.5)))
        (println "\nMen's Fashion under 50:")
        (println (take 5 (repo/find-by-max-price 50)))

        ;;decision tree
        (println "\nDecision tree - recommend products for Sara:")
        (println (rec/recommend-for-user users/sara 5))
        (println "\nDecision tree - recommend products for Mihajlo:")
        (println (rec/recommend-for-user users/mihajlo 5))
        (println "\nDecision tree - recommend products for Jelena:")
        (println (rec/recommend-for-user users/jelena 5))
        )
  )
