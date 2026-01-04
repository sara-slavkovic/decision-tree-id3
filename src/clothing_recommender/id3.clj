(ns clothing-recommender.id3
  (:require [clothing-recommender.entropy :as e]))

;;------------------------------
;; Helper functions
;; -----------------------------

(defn split-by-attribute
  "Splits dataset by values of an attribute."
  [dataset attribute]
  (group-by attribute dataset))

(defn majority-label
  "Returns the most frequent label in dataset."
  [dataset]
  (->> dataset
       (map :label)
       frequencies
       (apply max-key val)
       key))

(defn same-label?
  "Checks if all samples have the same label."
  [dataset]
  (apply = (map :label dataset)))

;;-------------------------------
;; ID3
;; ------------------------------

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

(defn build-tree
  "Builds an ID3 decision tree."
  [dataset attributes]
  (cond
    ;; all same label -> leaf
    (same-label? dataset)
    (:label (first dataset))

    ;; no attributes left -> majority vote
    (empty? attributes)
    (majority-label dataset)

    :else
    (let [attr (best-attribute dataset attributes)
          remaining (remove #{attr} attributes)
          splits (split-by-attribute dataset attr)]
      {attr
       (into {}
             (map (fn [[value subset]]
                    [value (build-tree subset remaining)])
                  splits))})))

(defn predict
  "Classifies a single instance using trained ID3 tree."
  [tree instance]
  (if (keyword? tree)
    tree
    (let [[attr branches] (first tree)
          value (get instance attr)]
      (if-let [subtree (get branches value)]
        (predict subtree instance)
        ;; if there's no branch -> not recommend
        :not-recommend))))