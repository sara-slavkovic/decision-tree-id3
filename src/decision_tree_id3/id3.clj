(ns decision-tree-id3.id3
  (:require [decision-tree-id3.entropy :as e]))

;;------------------------------
;; Helper functions
;; -----------------------------

(defn split-by-attribute
  "Splits dataset by values of an attribute."
  [dataset attribute]
  (group-by attribute dataset))

(defn majority-label
  "Returns the most frequent label in dataset."
  [dataset label-key]
  (->> dataset
       (map label-key)
       frequencies
       (apply max-key val)
       key))

(defn same-label?
  "Checks if all samples have the same label."
  [dataset label-key]
  (apply = (map label-key dataset)))

;;-------------------------------
;; ID3
;; ------------------------------

(defn information-gain
  "Computes IG(S, A)."
  [dataset attribute label-key]
  (let [base-entropy (e/entropy-memo dataset label-key)
        total (count dataset)
        splits (split-by-attribute dataset attribute)]
    (- base-entropy
       (reduce
         (fn [acc [_ subset]]
           (let [p (/ (count subset) total)]
             (+ acc (* p (e/entropy-memo subset label-key)))))
         0.0
         splits))))

;;root node
(defn best-attribute
  "Selects attribute with highest information gain (IG)."
  [dataset attributes label-key]
  (apply max-key
         #(information-gain dataset % label-key)
         attributes))

(defn build-tree
  "Builds an ID3 decision tree."
  [dataset attributes label-key]
  (cond
    ;; all same label -> leaf
    (same-label? dataset label-key)
    (label-key (first dataset))

    ;; no attributes left -> majority vote
    (empty? attributes)
    (majority-label dataset label-key)

    :else
    (let [attr (best-attribute dataset attributes label-key)
          remaining (remove #{attr} attributes)
          splits (split-by-attribute dataset attr)]
      {attr
       (into {}
             (map (fn [[value subset]]
                    [value (build-tree subset remaining label-key)])
                  splits))})))

(defn print-tree
  ([tree]
   (print-tree tree 0))

  ([tree indent]
   (let [pad (apply str (repeat indent "  "))]
     (if (map? tree)
       (let [[attr branches] (first tree)]
         (println pad attr)
         (doseq [[value subtree] branches]
           (println pad "=>" value)
           (print-tree subtree (inc indent))))
       (println pad "->" tree)))))

(defn predict
  "Classifies a single instance using trained ID3 tree.
   Returns the majority (default) label if instance value not present in tree branches."
  [tree instance dataset label-key]
  (if (not (map? tree))
    tree
    (let [[attr branches] (first tree)
          value (get instance attr)]
      (if-let [subtree (get branches value)]
        (predict subtree instance dataset label-key)
        ;; if there's no branch -> return majority label of dataset
        (majority-label dataset label-key)))))