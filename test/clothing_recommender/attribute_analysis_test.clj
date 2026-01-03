(ns clothing-recommender.attribute-analysis-test
  (:require [clojure.test :refer :all]
            [clothing-recommender.attribute-analysis :as aa]))

(def sample-training-data
  [{:price :low
    :rating :good
    :size-match 1
    :category 1
    :brand 1
    :color 1
    :label :recommend}

   {:price :high
    :rating :bad
    :size-match 0
    :category 0
    :brand 1
    :color 0
    :label :not-recommend}

   {:price :medium
    :rating :ok
    :size-match 1
    :category 1
    :brand 0
    :color 1
    :label :recommend}])

(deftest attribute-impact-test
  (let [impact (aa/attribute-impact sample-training-data :brand)]
    (is (number? impact))
    (is (<= 0 impact 1))))

(deftest analyze-attributes-test
  (let [result (aa/analyze-attributes sample-training-data)]
    (is (map? result))
    (is (= (set aa/attributes) (set (keys result))))
    (is (every? #(<= 0 % 1) (vals result)))))