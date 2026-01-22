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