(ns decision-tree-id3.core
  (:require
    [decision-tree-id3.csv-loader :as loader]
    [decision-tree-id3.preprocessing :as pre]
    [decision-tree-id3.split :as split]
    [decision-tree-id3.id3 :as id3]
    [decision-tree-id3.metrics :as metrics]))

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