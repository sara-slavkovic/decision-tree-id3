(ns clothing-recommender.user-test
  (:require [clojure.test :refer :all]
            [clothing-recommender.user :refer :all]))

(deftest make-user-test
  (testing "Creating a user"
    (let [user (make-user
                 1
                 "Sara"
                 {:categories ["Women's Fashion"]
                  :brands     ["Zara" "H&M"]
                  :colors     ["Black"]
                  :min-rating 4.0}
                 {:tops "M" :pants "S" :shoes "M"}
                 200.0)]
      (is (= (:user-id user) 1))
      (is (= (:name user) "Sara"))
      (is (= (:preferences user) {:categories ["Women's Fashion"]
                                  :brands     ["Zara" "H&M"]
                                  :colors     ["Black"]
                                  :min-rating 4.0}))
      (is (= (:sizes user) {:tops "M" :pants "S" :shoes "M"}))
      (is (= (:budget user) 200.0)))))

(deftest valid-user-test
  (testing "Invalid users"

    (testing "preferences is not a map"
      (let [invalid-user
            (make-user
              2
              "Anna"
              ["casual" "sporty"]   ;;  should be map
              {:tops "M" :pants "L" :shoes "M"}
              100.0)]
        (is (false? (valid-user? invalid-user)))))

    (testing "sizes is not a map"
      (let [invalid-user
            (make-user
              3
              "Ivana"
              {:categories ["Women's Fashion"]}
              ["M" "L"]             ;;  should be map
              100.0)]
        (is (false? (valid-user? invalid-user)))))

    (testing "missing name"
      (let [invalid-user
            {:user-id 4                                     ;;there's no name
             :preferences {:categories ["Men's Fashion"]}
             :sizes {:tops "L" :pants "M" :shoes "L"}
             :budget 50.0}]
        (is (false? (valid-user? invalid-user)))))))
