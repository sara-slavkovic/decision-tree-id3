(ns clothing-recommender.normalization-test
  (:require [clojure.test :refer :all]
            [clothing-recommender.normalization :as norm]
            [clothing-recommender.recommendation :as rec]
            [clothing-recommender.users :as users]))

(deftest normalize-range-test
  (let [values [10 20 30]
        {:keys [min max]} (norm/min-max values)]
    (is (= 0.0 (norm/normalize 10 min max)))
    (is (= 1.0 (norm/normalize 30 min max)))))

(deftest normalize-products-test
  (let [products [{:price 50 :rating 3.0}
                  {:price 100 :rating 5.0}]
        result (norm/normalize-products products)]
    (is (contains? (first result) :price-norm))
    (is (contains? (first result) :rating-norm))))

(deftest normalize-user-test
  (let [user {:budget 80 :preferences {:min-rating 4.0}}
        products [{:price 40 :rating 2.0}
                  {:price 100 :rating 5.0}]
        user-n (norm/normalize-user user products)]
    (is (<= 0 (:budget-norm user-n) 1))
    (is (<= 0 (:min-rating-norm user-n) 1))))

(deftest recommendation-pipeline-test
  (let [result (rec/recommend-for-user users/sara 5)]
    (is (seq result))
    (is (contains? (first result) :score))))
