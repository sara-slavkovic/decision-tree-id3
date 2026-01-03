(ns clothing-recommender.discretization-test
  (:require [clojure.test :refer :all]
            [clothing-recommender.discretization :as disc]))

(deftest price-bin-test
  (is (= :low (disc/price-bin 0.1)))
  (is (= :medium (disc/price-bin 0.5)))
  (is (= :high (disc/price-bin 0.9))))

(deftest rating-bin-test
  (is (= :bad (disc/rating-bin 0.2)))
  (is (= :ok (disc/rating-bin 0.6)))
  (is (= :good (disc/rating-bin 0.9))))

(deftest discretize-instance-test
  (let [inst {:price 0.2 :rating 0.8}]
    (is (= {:price :low :rating :good}
           (disc/discretize-instance inst)))))
