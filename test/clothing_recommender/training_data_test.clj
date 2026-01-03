(ns clothing-recommender.training-data-test
  (:require [clojure.test :refer :all]
            [clothing-recommender.training-data :as td]))

(deftest label-from-score-test
  (is (= :recommend (td/label-from-score 90)))
  (is (= :not-recommend (td/label-from-score 30))))

(deftest build-training-data-test
  (let [dummy-user {:budget-norm 0.5
                    :min-rating-norm 0.6
                    :sizes {:tops "M" :pants "S" :shoes "M"}
                    :preferences {:categories []
                                  :brands []
                                  :colors []}}

        dummy-products [{:price-norm 0.2
                         :rating-norm 0.8
                         :size "M"
                         :category "Women's Fashion"
                         :brand "Adidas"
                         :color "Black"
                         :product-name "Sweater"}]

        result (vec (td/build-training-data dummy-user dummy-products))
        row    (first result)]

    (is (= 1 (count result)))
    (is (contains? row :label))
    (is (contains? row :price))
    (is (contains? row :rating))))