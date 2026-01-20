(ns decision-tree-id3.entropy-test
  (:require [clojure.test :refer :all]
            [decision-tree-id3.entropy :as e]))

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

(deftest entropy-memo-returns-same-value
  (let [dataset [{:outcome :yes}
                 {:outcome :yes}
                 {:outcome :no}]
        label-key :outcome]
    (is
      (= (e/entropy dataset label-key)
         (e/entropy-memo dataset label-key)))))