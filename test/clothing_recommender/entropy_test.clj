(ns clothing-recommender.entropy-test
  (:require [clojure.test :refer :all]
            [clothing-recommender.entropy :as e]))

(deftest entropy-pure-set
  (is (= 0.0 (e/entropy [{:outcome :yes}
                       {:outcome :yes}]
                        :outcome))))

(deftest entropy-balanced-set
  (is (< (Math/abs
           (- 1.0
              (e/entropy [{:outcome :yes}
                        {:outcome :no}]
                         :outcome)))
         0.0001)))

(deftest entropy-trivial-set
  (is (= 0.0 (e/entropy [] :outcome)))
  (is (= 0.0 (e/entropy [{:outcome :yes}] :outcome))))
