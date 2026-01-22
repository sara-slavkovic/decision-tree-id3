(ns decision-tree-id3.experiment
  (:require
    [decision-tree-id3.preprocessing :as pre]
    [decision-tree-id3.split :as split]
    [decision-tree-id3.id3 :as id3]
    [decision-tree-id3.metrics :as metrics]))

(defn run-id3
  "Runs full ID3 pipeline on already loaded dataset.
   Returns {:tree :metrics :train-size :test-size}"
  [dataset label-key positive-label]
  (let [{processed :data} (pre/discretize-dataset dataset label-key)
        {:keys [train test]} (split/train-test-split processed 0.8)
        attributes (pre/attributes processed label-key)
        tree (id3/build-tree train attributes label-key)
        predictions (map #(id3/predict tree % train label-key) test)
        true-labels (map label-key test)
        cm (metrics/confusion-matrix true-labels predictions positive-label)]
    {:tree tree
     :train-size (count train)
     :test-size (count test)
     :metrics {:accuracy  (metrics/accuracy% cm)
               :precision (metrics/precision% cm)
               :recall    (metrics/recall% cm)
               :f1        (metrics/f1-score% cm)}}))