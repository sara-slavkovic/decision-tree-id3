(ns clothing-recommender.core-test
  (:require [clothing-recommender.core :refer :all]
            [clojure.test :refer :all]))

(deftest simple-recommendation-test
  (testing "simple clothing recommendation based on temperature"
    (is (= "Wear a jacket." (simple-recommendation 10)))
    (is (= "A t-shirt is fine." (simple-recommendation 20)))))

(deftest filter-warm-clothes-test
  (testing "filter warm clothes from items list"
    (let [items [{:name "Jacket" :type :warm}
                 {:name "T-shirt" :type :light}]]
      (is (= [{:name "Jacket" :type :warm}]
             (filter-warm-clothes items))))))

(def test-products
  [{:product-id 1 :product-name "Blue Jeans" :brand "Levis"
    :category   "Pants" :price 89.99 :rating 4.5 :color "Blue" :size "M"}
   {:product-id 2 :product-name "White T-Shirt" :brand "Nike"
    :category   "Tops" :price 29.99 :rating 4.8 :color "White" :size "L"}
   {:product-id 3 :product-name "Black Jacket" :brand "Levis"
    :category   "Jackets" :price 120.00 :rating 4.2 :color "Black" :size "M"}
   {:product-id 4 :product-name "Red Dress" :brand "Zara"
    :category   "Dresses" :price 59.99 :rating 4.7 :color "Red" :size "S"}
   {:product-id 5 :product-name "Blue Jeans" :brand "Nike"
    :category   "Pants" :price 79.99 :rating 4.3 :color "Blue" :size "L"}])

(deftest filter-products-test
  (testing "filter by brand"
    (let [result (filter-products test-products "Levis" nil nil)]
      (is (= 2 (count result)))
      (is (= "Levis" (:brand (first result))))))

  (testing "filter by category"
    (let [result (filter-products test-products nil "Pants" nil)]
      (is (= 2 (count result)))
      (is (= "Pants" (:category (first result))))))

  (testing "filter by max price"
    (let [result (filter-products test-products nil nil 80.0)]
      (is (= 3 (count result)))
      (is (<= (:price (first result)) 80.0))))

  (testing "filter by brand, category and max price"
    (let [result (filter-products test-products "Levis" nil 100.0)]
      (is (= 1 (count result)))
      (is (= "Blue Jeans" (:product-name (first result)))))))

(deftest average-rating-by-category-test
  (testing "average rating by category"
    (let [result (average-rating-by-category test-products "Pants")]
      (is (not (nil? result)))
      (is (< 4.0 result))
      (is (> 5.0 result))))

  (testing "return nil for non-existent category"
    (is (nil? (average-rating-by-category test-products "Shoes")))))

(deftest most-expensive-by-color-test
  (testing "find most expensive product by color"
    (let [result (most-expensive-by-color test-products "Blue")]
      (is (not (nil? result)))
      (is (= "Blue" (:color result)))
      (is (= 89.99 (:price result)))))

  (testing "return nil for non-existent color"
    (is (nil? (most-expensive-by-color test-products "Green")))))

(deftest group-by-brand-test
  (testing "group by brand"
    (let [result (group-by-brand test-products)]
      (is (contains? result "Levis"))
      (is (contains? result "Nike"))
      (is (contains? result "Zara"))
      (is (= 2 (count (get result "Levis"))))
      (is (= 2 (count (get result "Nike"))))
      (is (= 1 (count (get result "Zara")))))))

(deftest recommend-by-value-test
  (testing "recommend products by value"
    (let [result (recommend-by-value test-products 2)]
      (is (= 2 (count result)))
      (is (not (nil? (first result))))
      (is (not (nil? (second result)))))))

(deftest sort-by-price-test
  (testing "sort products by ascending price"
    (let [result (sort-by-price test-products)]
      (is (= 29.99 (:price (first result))))
      (is (= 120.00 (:price (last result))))
      (is (= ["White T-Shirt" "Red Dress" "Blue Jeans" "Blue Jeans" "Black Jacket"]
             (map :product-name result))))))

(deftest cheapest-by-category-test
  (testing "find cheapest product in Pants category"
    (let [result (cheapest-by-category test-products "Pants")]
      (is (= "Blue Jeans" (:product-name result)))
      (is (= 79.99 (:price result)))))

  (testing "returns nil for non-existing category"
    (is (nil? (cheapest-by-category test-products "Shoes")))))

(deftest find-by-name-test
  (testing "find all products with the given name"
    (let [result (find-by-name test-products "Blue Jeans")]
      (is (= 2 (count result)))
      (is (= #{"Levis" "Nike"} (set (map :brand result))))))

  (testing "returns empty list when name does not exist"
    (is (empty? (find-by-name test-products "Golden Jacket")))))