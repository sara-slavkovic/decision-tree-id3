(ns clothing-recommender.pipeline
  (:require
    [clothing-recommender.product-repository :as repo]
    [clothing-recommender.user-generator :as ug]
    [clothing-recommender.dataset :as ds]
    [clothing-recommender.discretization :as disc]
    [clothing-recommender.id3 :as id3]))

(defn load-products []
  (repo/find-all))

(defn load-users
  [products]
  (ug/generate-users 50 products))

(defn build-raw-dataset
  [users products]
  (ds/build-dataset users products))

(defn train-test-split
  [dataset ratio]
  (let [shuffled (shuffle dataset)
        split-at (int (* ratio (count dataset)))]
    {:train (subvec (vec shuffled) 0 split-at)
     :test  (subvec (vec shuffled) split-at)}))

(defn discretize-datasets
  [{:keys [train test]}]
  (let [discretizers (disc/build-discretizers train)]
    {:train (map #(disc/discretize-instance % discretizers) train)
     :test  (map #(disc/discretize-instance % discretizers) test)}))

(def attributes
  [:price :rating :size-match :category :brand :color])

(defn train-model
  [train-data]
  (id3/build-tree train-data attributes))

(defn run-pipeline []
  (let [products (load-products)
        users    (load-users products)
        raw      (build-raw-dataset users products)
        split    (train-test-split raw 0.8)
        disc-ds  (discretize-datasets split)
        model    (train-model (:train disc-ds))]
    {:model model
     :train (:train disc-ds)
     :test  (:test disc-ds)}))

;;prediction helper
(defn predict-instance
  "Predicts label for one dataset row (removes :label before predicting)."
  [model instance]
  (id3/predict model (dissoc instance :label)))

;;evaluation helper
(defn evaluate
  "Computes accuracy on a dataset."
  [model test-data]
  (let [pairs (map (fn [x]
                     {:true (:label x)
                      :pred (predict-instance model x)})
                   test-data)
        correct (count (filter #(= (:true %) (:pred %)) pairs))]
    (/ correct (count test-data))))

(defn run-all []
  (let [result (run-pipeline)
        model (:model result)
        test (:test result)
        sample (first test)
        prediction (predict-instance model sample)
        accuracy (evaluate model test)]
    (println "Sample instance:" sample)
    (println "Prediction for first sample:" prediction)
    (println "Model accuracy on test set:" accuracy)
    ;;result
    ))