(ns clothing-recommender.id3-test
  (:require [clojure.test :refer :all]
            [clothing-recommender.id3 :as id3]
            [clothing-recommender.training-data :as td]
            [clothing-recommender.product-repository :as repo]
            [clothing-recommender.users :as users]
            [clothing-recommender.normalization :as norm]))

(def products (repo/find-all))
(def products-n (norm/normalize-products products))
(def user-n (norm/normalize-user users/sara products))
(def training (td/build-training-data user-n products-n))

(deftest information-gain-non-negative
  (is (>= (id3/information-gain 
            training
            :price) 0)))

(deftest information-gain-zero-when-no-split
  (let [data [{:a 1 :label :yes}
              {:a 1 :label :no}]]
    (is (= 0.0 (id3/information-gain data :a)))))

(deftest best-attribute-picks-correct-one
  (let [data [{:size 1 :color 0 :label :yes}
              {:size 1 :color 1 :label :yes}
              {:size 0 :color 1 :label :no}
              {:size 0 :color 0 :label :no}]]
    (is (= :size
           (id3/best-attribute data [:size :color])))))


