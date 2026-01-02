(ns clothing-recommender.training-data
  (:require [clothing-recommender.decision-tree :as tree]))

(defn label-from-score
  "Discretizes score into class label (Quinlan-style)."
  [score]
  (if (>= score 70)
    :recommend
    :not-recommend))

(defn feature-vector
  [user product score]
  {:price        (:price-norm product)
   :rating       (:rating-norm product)
   :size-match   (if (> (tree/size-score user product) 0) 1 0)
   :category     (if (> (tree/category-score user product) 0) 1 0)
   :brand        (if (> (tree/brand-score user product) 0) 1 0)
   :color        (if (> (tree/color-score user product) 0) 1 0)
   :label        (label-from-score score)})

(defn build-training-data
  "Creates training set: normalized attributes + class"
  [user products]
  (map (fn [p]
         (let [score (tree/decision-tree-score user p)]
           (feature-vector user p score)))
       products))