(ns clothing-recommender.id3-test
  (:require [clojure.test :refer :all]
            [clothing-recommender.id3 :as id3]))

(def toy-dataset
  [{:a :low  :b :low  :label :yes}
   {:a :low  :b :high :label :yes}
   {:a :high :b :low  :label :no}
   {:a :high :b :high :label :no}])

(def attrs [:a :b])

(deftest information-gain-non-negative
  (is (>= (id3/information-gain toy-dataset :a :label) 0)))

(deftest best-attribute-picks-a
  (is (= :a
         (id3/best-attribute toy-dataset attrs :label))))

(deftest build-tree-test
  (let [tree (id3/build-tree toy-dataset attrs :label)]
    ;; root node
    (is (contains? tree :a))
    ;; leaves
    (is (= :yes (get-in tree [:a :low])))
    (is (= :no  (get-in tree [:a :high])))))

(deftest predict-test
  (let [tree (id3/build-tree toy-dataset attrs :label)]
    (is (= :yes
           (id3/predict tree {:a :low :b :low})))
    (is (= :no
           (id3/predict tree {:a :high :b :low})))))