(ns clothing-recommender.user-test
  (:require [clojure.test :refer :all]
            [clothing-recommender.user :refer :all]))

(deftest make-user-test
  (testing "Creating a user"
    (let [user (make-user 1 "Sara" ["casual" "sporty"] {:shirt "M" :pants "L"} 200.0)]
      (is (= (:user-id user) 1))
      (is (= (:name user) "Sara"))
      (is (= (:style-preferences user) ["casual" "sporty"]))
      (is (= (:sizes user) {:shirt "M" :pants "L"}))
      (is (= (:budget user) 200.0))))

  (let [invalid-user {:user-id 3
                      :name    "Anna"
                      :sizes   ["M" "L"]                    ;;it's not map
                      :budget  100}]
    (is (not (valid-user? invalid-user))))

  (let [invalid-user2 {:user-id 4                           ;;there's no name
                       :sizes   {:shirt "S"}
                       :budget  50}]
    (is (not (valid-user? invalid-user2)))))


