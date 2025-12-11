(ns clothing-recommender.product-test
  (:require [clojure.test :refer :all]
            [clothing-recommender.product :refer :all]))

(deftest make-product-test
  (testing "Making product"
    (let [product (make-product 10 "Blue Jeans" "Levis" "Pants" 89.99 4.5 "Blue" "M")]
      (is (= (:product-id product) 10))
      (is (= (:product-name product) "Blue Jeans"))
      (is (= (:price product) 89.99))
      (is (= (:rating product) 4.5))
      )
    ))

(deftest valid-product?-test
  (testing "Valid product check"
    (let [product {:product-id 10
                   :product-name "Blue Jeans"
                   :brand "Levis"
                   :category "Pants"
                   :price 89.99
                   :rating 4.5
                   :color "Blue"
                   :size "M"}]
      (is (true? (valid-product? product))))

    (let [invalid-product {:product-id 10
                   :product-name "Blue Jeans"
                   :brand "Levis"
                   :category "Pants"
                   :price "89.99"                           ;;can't be string
                   :rating 4.5
                   :color "Blue"
                   :size "M"}]
      (is (false? (valid-product? invalid-product))))
    )
  )
