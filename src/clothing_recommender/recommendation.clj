(ns clothing-recommender.recommendation
  (:require [clothing-recommender.decision-tree :as tree]
            [clothing-recommender.product-repository :as repo]
            [clothing-recommender.normalization :as norm]))

(defn recommend-for-user
  [user n]
  (let [products   (repo/find-all)
        products-n (norm/normalize-products products)
        user-n     (norm/normalize-user user products)]
    (->> products-n
         (map (fn [p]
                (assoc p :score
                         (tree/decision-tree-score user-n p))))
         (sort-by :score >)
         (take n))))

