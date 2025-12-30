(ns clothing-recommender.recommendation
  (:require [clothing-recommender.decision-tree :as tree]
            [clothing-recommender.product-repository :as repo]))

(defn recommend-for-user
  [user n]
  (->> (repo/find-all)
       (map (fn [p]
              (assoc p :score (tree/decision-tree-score user p))))
       (sort-by :score >)
       (take n)))
