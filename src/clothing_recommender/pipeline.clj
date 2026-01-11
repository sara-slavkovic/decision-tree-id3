(ns clothing-recommender.pipeline
  (:require
    [clothing-recommender.product-repository :as repo]
    [clothing-recommender.user-generator :as ug]
    [clothing-recommender.dataset :as ds]
    [clothing-recommender.discretization :as disc]
    [clothing-recommender.id3 :as id3]
    [clothing-recommender.metrics :as m]
    [clothing-recommender.model-store :as ms]))

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

(defn evaluate-all
  [model test-data]
  (let [true-labels (map :label test-data)
        predicted   (map #(predict-instance model %) test-data)
        cm          (m/confusion-matrix true-labels predicted :recommend)]
    {:confusion cm
     :accuracy  (m/accuracy cm)
     :precision (m/precision cm)
     :recall    (m/recall cm)
     :f1        (m/f1-score
                  (m/precision cm)
                  (m/recall cm))}))

; generates different dataset everytime, trains it, and evaluates metrics on model - it will be different metrics everytime
(defn run-all []
  (let [result (run-pipeline)
        model (:model result)
        test (:test result)
        sample (first test)
        prediction (predict-instance model sample)
        accuracy (evaluate model test)
        metrics (evaluate-all model test)]
    (println "Sample instance:" sample)
    (println "Prediction for first sample:" prediction)
    (println "Model accuracy on test set:" accuracy)
    ;;result
    (println "Evaluation metrics:" metrics)
    ))

; training from persisted dataset - uses dataset.edn, trains model, and saves it
; we call this function only once, bc model has to be trained only once, and saved to resources folder
(defn train-model-from-dataset!
  "Loads dataset from disk, trains model, saves it."
  []
  (let [dataset (ds/load-dataset "resources/dataset.edn")
        split   (train-test-split dataset 0.8)
        discretizers (disc/build-discretizers (:train split))
        train-data (map #(disc/discretize-instance % discretizers)
                        (:train split))
        model (train-model train-data)]
    (ms/save-model model)
    model))

(defn load-trained-model
  "Loads previously trained model from disk."
  []
  (ms/load-model))

(defn evaluate-trained-model
  "Evaluates already trained and saved model on test part of persisted dataset."
  []
  (let [dataset (ds/load-dataset "resources/dataset.edn")
        split   (train-test-split dataset 0.8)
        discretizers (disc/build-discretizers (:train split))
        test-data (map #(disc/discretize-instance % discretizers)
                       (:test split))
        model (load-trained-model)
        metrics (evaluate-all model test-data)]
    (println "Evaluation of persisted model:")
    (println metrics)
    metrics))
