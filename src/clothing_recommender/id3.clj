(ns clothing-recommender.id3
  (:require [clothing-recommender.entropy :as e]))

(defn split-by-attribute
  "Splits dataset by values of an attribute."
  [dataset attribute]
  (group-by attribute dataset))

(defn information-gain
  "Computes IG(S, A)."
  [dataset attribute]
  (let [base-entropy (e/entropy dataset)
        total (count dataset)
        splits (split-by-attribute dataset attribute)]
    (- base-entropy
       (reduce
         (fn [acc [_ subset]]
           (let [p (/ (count subset) total)]
             (+ acc (* p (e/entropy subset)))))
         0.0
         splits))))

;;root node
(defn best-attribute
  "Selects attribute with highest information gain (IG)."
  [dataset attributes]
  (apply max-key
         #(information-gain dataset %)
         attributes))