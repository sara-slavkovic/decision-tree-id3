(ns decision-tree-id3.experiment-test
  (:require
    [clojure.test :refer :all]
    [decision-tree-id3.experiment :as exp]))

(deftest run-id3-test
  (let [dataset [{:a :x :label :yes}
                 {:a :x :label :yes}
                 {:a :y :label :no}
                 {:a :y :label :no}]
        result (exp/run-id3 dataset :label :yes)]
    (is (map? result))
    (is (contains? result :tree))
    (is (contains? result :metrics))
    (is (<= 0 (:accuracy (:metrics result)) 100))))

(deftest bench-id3-test
  (let [dataset [{:a 1 :label :yes}
                 {:a 2 :label :yes}
                 {:a 3 :label :no}
                 {:a 4 :label :no}]
        result (exp/bench-id3 dataset :label)]
    (is (map? result))
    (is (contains? result :train-size))
    (is (contains? result :test-size))
    (is (pos-int? (:train-size result)))
    (is (pos-int? (:test-size result)))
    ;; 80/20 split over 4 items -> train 3, test 1 (because int(*0.8*4)=3)
    (is (= 4 (+ (:train-size result) (:test-size result))))))