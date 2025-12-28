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
    (let [invalid-user {:user-id 3
                        :name    "Anna"
                        :sizes   ["M" "L"]                  ;;it's not map
                        :budget  100}
          invalid-user2 {:user-id 4                         ;;there's no name
                         :sizes   {:shirt "S"}
                         :budget  50}]
      (is (not (valid-user? invalid-user)))
      (is (not (valid-user? invalid-user2))))))
