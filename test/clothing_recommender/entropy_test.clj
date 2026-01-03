(ns clothing-recommender.entropy-test
  (:require [clojure.test :refer :all]
            [clothing-recommender.entropy :as e]))

(deftest entropy-pure-set
  (is (= 0.0 (e/entropy [{:label :yes}
                       {:label :yes}]))))

(deftest entropy-balanced-set
  (is (< (Math/abs
           (- 1.0
              (e/entropy [{:label :yes}
                        {:label :no}])))
         0.0001)))

(deftest entropy-trivial-set
  (is (= 0.0 (e/entropy [])))
  (is (= 0.0 (e/entropy [{:label :yes}]))))
