(ns clothing-recommender.training-data
  (:require [clothing-recommender.decision-tree :as tree]
            [clothing-recommender.discretization :as disc]))

(defn label-from-score
  "Discretizes score into class label (Quinlan-style)."
  [score]
  (if (>= score 70)
    :recommend
    :not-recommend))

;(defn feature-vector
;  "Features - rule-based scoring."
;  [user product score]
;  {:price        (:price-norm product)
;   :rating       (:rating-norm product)
;   :size-match   (if (> (tree/size-score user product) 0) 1 0)
;   :category     (if (> (tree/category-score user product) 0) 1 0)
;   :brand        (if (> (tree/brand-score user product) 0) 1 0)
;   :color        (if (> (tree/color-score user product) 0) 1 0)
;   :label        (label-from-score score)})

(defn feature-vector
  "ML features – no rule-based scoring."
  [user product]
  (let [ptype (tree/product-size-type product)]
    {:price        (:price-norm product)
     :rating       (:rating-norm product)

     ;; size depends on product type {:tops "M" :pants "S"}
     :size-match   (and ptype
                        (= (:size product)
                           (get-in user [:sizes ptype])))

     ;; preference matches
     :category     (if (some #{(:category product)}
                         (get-in user [:preferences :categories]))
                     :match
                     :no-match)

     :brand        (if (some #{(:brand product)}
                         (get-in user [:preferences :brands]))
                     :match
                     :no-match)

     :color        (if (some #{(:color product)}
                         (get-in user [:preferences :colors]))
                     :match
                     :no-match)}))

;(defn build-training-data
;  "Creates training set: normalized attributes + class"
;  [user products]
;  (map (fn [p]
;         (let [score (tree/decision-tree-score user p)
;               fv (feature-vector user p score)]
;           (disc/discretize-instance fv)))
;       products))

(defn build-training-data
  "Creates ML training set (features + label from baseline)."
  [user products]
  (map (fn [p]
         (let [score (tree/decision-tree-score user p)
               fv    (feature-vector user p)]
           (-> fv
               (assoc :label (label-from-score score))
               disc/discretize-instance)))
       products))