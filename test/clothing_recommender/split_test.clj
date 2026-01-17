(ns clothing-recommender.split-test
  (:require [clojure.test :refer :all]
            [clothing-recommender.split :as split]
            [clojure.set :as set]))

(deftest test-train-test-split
  (let [dataset (vec (range 10))
        ratio   0.7
        result  (split/train-test-split dataset ratio)
        train   (:train result)
        test    (:test result)]

    (is (= (+ (count train) (count test))
           (count dataset)))

    (is (= (count train)
           (int (* ratio (count dataset)))))

    (is (empty? (set/intersection
                  (set train)
                  (set test))))

    (is (= (set dataset)
           (set (concat train test))))))