(ns clothing-recommender.dataset-store
  (:require
    [clothing-recommender.product-repository :as repo]
    [clothing-recommender.user-generator :as ug]
    [clothing-recommender.dataset :as ds]))

(def dataset-path "resources/dataset.edn")

(defn generate-and-save-dataset!
  "Generates dataset once and saves it to disk."
  []
  (let [products (repo/find-all)
        users    (ug/generate-users 50 products)
        dataset  (ds/build-dataset users products)]
    (ds/save-dataset dataset dataset-path)
    (println "Dataset saved to" dataset-path)
    (count dataset)))

;(require '[clothing-recommender.dataset-store :as store])
;(store/generate-and-save-dataset!)