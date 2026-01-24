(ns decision-tree-id3.core
  (:require
    [decision-tree-id3.csv-loader :as loader]
    [decision-tree-id3.preprocessing :as pre]
    [decision-tree-id3.split :as split]
    [decision-tree-id3.id3 :as id3]
    [decision-tree-id3.metrics :as metrics]
    [criterium.core :as c]
    [decision-tree-id3.experiment :as exp]))

;;-----------------------------------
;; Load dataset
;;-----------------------------------

(def csv-file "csv/loan_approval_dataset.csv")

(def raw-dataset
  (loader/load-csv->maps csv-file))

(println "Loaded loan_approval_dataset size:")
(println (count raw-dataset))

(println "First 5 rows:")
(doseq [row (take 5 raw-dataset)]
  (println row))

;;-----------------------------------
;; Preprocessing and discretization
;;-----------------------------------

(def label-key :loan_status)

(def processed-result
  (pre/discretize-dataset raw-dataset label-key))

(def processed-data
  (:data processed-result))

(println "\nAfter preprocessing:")
(println "Number of instances:" (count processed-data))

(println "Sample rows:")
(doseq [row (take 5 processed-data)]
  (println row))

;;-----------------------------------
;; Train/test split
;;-----------------------------------

(def split-result
  (split/train-test-split processed-data 0.8))

(def train-data (:train split-result))
(def test-data  (:test split-result))

(println "\nTrain size:" (count train-data))
(println "Test size:" (count test-data))


;;-----------------------------------
;; Build ID3 decision tree
;;-----------------------------------

(def attributes
  (pre/attributes processed-data label-key))

(println "\nAttributes used for training:")
(println attributes)

(def decision-tree
  (id3/build-tree train-data (seq attributes) label-key))

(println "\n--- Benchmarking tree building with quick-bench ---")
(c/quick-bench
  (id3/build-tree train-data (seq attributes) label-key))

(println "\n--- Benchmarking tree building with progress reporting ---")
(c/with-progress-reporting
  (c/quick-bench
    (id3/build-tree train-data (seq attributes) label-key)))

;; Optional: scalability test
(def big-train
  (vec (apply concat (repeat 5 train-data))))

(println "\n--- Benchmarking tree building on larger dataset ---")
(c/with-progress-reporting
  (c/quick-bench
    (id3/build-tree big-train (seq attributes) label-key)))

(println "\nDecision tree built successfully.")

;;-----------------------------------
;; Prediction on test set
;;-----------------------------------

(def predictions
  (map
    #(id3/predict decision-tree % train-data label-key)
    test-data))

(def true-labels
  (map label-key test-data))

(println "\nSample predictions:")
(doseq [[p t] (take 5 (map vector predictions true-labels))]
  (println "Predicted:" p "| Actual:" t))

;;-----------------------------------
;; Model evaluation
;;-----------------------------------

(def confusion-matrix
  (metrics/confusion-matrix true-labels predictions "Approved"))

(def evaluation-results
  {:accuracy  (metrics/accuracy% confusion-matrix)
   :precision (metrics/precision% confusion-matrix)
   :recall    (metrics/recall% confusion-matrix)
   :f1        (metrics/f1-score% confusion-matrix)})

(println "\n-----------------------------------")
(println "CONFUSION MATRIX")
(println "-----------------------------------")
(println confusion-matrix)

(println "\n-----------------------------------")
(println "EVALUATION METRICS")
(println "-----------------------------------")
(println "Accuracy :" (:accuracy evaluation-results) "%")
(println "Precision:" (:precision evaluation-results) "%")
(println "Recall   :" (:recall evaluation-results) "%")
(println "F1 score :" (:f1 evaluation-results) "%")

;;-----------------------------------
;; Optional : print tree structure
;;-----------------------------------

;(println "\nDecision tree structure:")
;(id3/print-tree decision-tree)


;;-----------------------------------
;; Run experiment (clean API)
;;-----------------------------------
;; NOTE:
;; run-id3 is a clean experimental API.
;; Code above demonstrates step-by-step pipeline and benchmarking.

(def raw-dataset-2
  (loader/load-csv->maps "csv/car_evaluation.csv"))

(def result-2
  (exp/run-id3 raw-dataset-2 :decision "acc"))

(println "\n===== CAR EVALUATION DATASET =====")
(doseq [[k v] (:metrics result-2)]
  (println (name k) ":" v "%"))


(def raw-dataset-3
  (loader/load-csv->maps "csv/play_tennis_dataset.csv"))

(def result-3
  (exp/run-id3 raw-dataset-3 :play "Yes"))

(println "\n===== PLAY TENNIS DATASET =====")
(doseq [[k v] (:metrics result-3)]
  (println (name k) ":" v "%"))


(println "\n===== BENCHMARK (TREE BUILD) - OTHER DATASETS (API) =====")

(println "\n--- Benchmarking tree building with quick-bench (Car Evaluation) ---")
(exp/bench-id3 raw-dataset-2 :decision)

(println "\n--- Benchmarking tree building with quick-bench (Play Tennis) ---")
(exp/bench-id3 raw-dataset-3 :play)