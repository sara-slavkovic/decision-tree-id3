# Decision Tree Classifier (ID3)

This project implements a **Decision Tree classifier** using the **ID3 algorithm**, written from scratch in **Clojure**, without using any machine learning libraries. 

It is designed as an **educational and experimental project**, demonstrating the complete machine learning pipeline:
- data loading
- preprocessing and discretization
- training and prediction
- evaluation with standard classification metrics
- performance benchmarking

The implementation is generic and can be applied to different **categorical datasets**.

---

## Project Documentation

A detailed explanation of the project evolution, design decisions, experiments,
and critical findings is available in the **Wiki** section of this repository.

See: **[Wiki – Decision Tree (ID3) From Scratch in Clojure](https://github.com/sara-slavkovic/decision-tree-id3/wiki)**


---

## Datasets

The algorithm was evaluated on three datasets obtained from Kaggle:
- [Loan Approval Dataset](https://www.kaggle.com/datasets/architsharma01/loan-approval-prediction-dataset) (~4269 instances)
- [Car Evaluation Dataset](https://www.kaggle.com/datasets/elikplim/car-evaluation-data-set) (~1728 instances)
- [Play Tennis Dataset](https://www.kaggle.com/datasets/milapgohil/play-tennis-dataset-weather-based-classifier) (~6667 instances)

Numerical attributes are **discretized** before training.

---

## Project Structure

decision_tree_id3/
- src/
    - decision_tree_id3/
        - core.clj              ; Main pipeline and experiments
        - csv_loader.clj        ; CSV loading utilities
        - preprocessing.clj     ; Discretization and preprocessing
        - split.clj             ; Train/test splitting
        - id3.clj               ; ID3 decision tree implementation
        - entropy.clj           ; Entropy and information gain calculations
        - metrics.clj           ; Confusion matrix and evaluation metrics
        - experiment.clj        ; Experimental API
- test/
    - decision_tree_id3/
        - csv_loader_test.clj
        - entropy_test.clj
        - experiment_test.clj
        - id3_test.clj
        - metrics_test.clj
        - preprocessing_test.clj
        - split_test.clj
- csv/                     
    - car_evaluation.csv
    - loan_approval_dataset.csv
    - play_tennis_dataset.csv
- deps.edn                 



---

## Methodology

1. **Data Loading**
    - CSV datasets are loaded and converted into maps `{:attribute value}`
    - Headers are sanitized and columns ending with `_id` are excluded
    - Numeric values are parsed into `Integer` / `Double` when possible
    - Implemented in `csv_loader.clj`

2. **Preprocessing & Discretization**
    - Numerical attributes are discretized into categorical bins (`:low / :medium / :high`) using quantiles
    - Categorical attributes are preserved
    - Class label is excluded from discretization
    - Implemented in `preprocessing.clj`

3. **Train/Test Split**
    - The dataset is split into training and testing sets
    - The split ratio is configurable, in all experiments a ratio of **80% train / 20% test** is used
    - The split is randomized using dataset shuffling
    - Implemented in `split.clj`

4. **ID3 Decision Tree Algorithm**
    - Entropy of class labels is computed using information theory (`entropy.clj`)
    - Entropy values are memoized (`entropy-memo`) to avoid redundant recomputation during tree construction
    - For each node, the attribute with the **highest information gain** is selected
    - The decision tree is built **recursively**
    - Tree construction stops when:
        - all instances in the current subset have the same class label, or
        - no attributes remain, in which case **majority voting** is applied
    - Implemented in `id3.clj`

5. **Prediction**
    - Single-instance classification using the trained ID3 decision tree
    - Tree traversal follows attribute values until a leaf node is reached
    - If a branch for a given attribute value does not exist, prediction falls back to the
      **majority class of the training dataset**
    - Implemented in `id3.clj`

6. **Evaluation Metrics**
    - Confusion matrix: `TP`, `FP`, `FN`, `TN`
    - Accuracy, Precision, Recall and F1 score
    - All values are reported as **percentages**
    - Implemented in `metrics.clj`

7. **Experimental API**
    - Experimental interface is provided in `experiment.clj`
    - `run-id3` function executes the complete ID3 pipeline:
      preprocessing, train/test split, training, prediction and evaluation
    - `bench-id3` function benchmarks **only tree construction** using Criterium `quick-bench`
    - This API enables easy comparison of model performance across different datasets


 ### Positive Label Note

ID3 itself does not require a positive class.
However, **precision, recall and F1** are binary metrics and therefore require specifying a **positive label** during evaluation.

Examples:
* Loan Approval: `"Approved"`
* Car Evaluation: `"acc"`
* Play Tennis: `"Yes"`

This choice affects **only evaluation**, not training.

---

### Entropy and Information Gain

ID3 selects the splitting attribute using **Information Gain**, which measures the reduction in entropy after splitting the dataset by a given attribute.

**Entropy** measures class impurity in a dataset:

`Entropy(S) = − ∑ pᵢ · log₂(pᵢ)`

where pᵢ is the probability of class i in dataset S.

**Information Gain** for attribute A is defined as:

`IG(S, A) = Entropy(S) − ∑ (|Sᵥ| / |S|) · Entropy(Sᵥ)`

where Sᵥ is the subset of S for which attribute A has value v.

A step-by-step example of entropy and information gain calculation
(using the Play Tennis dataset) is provided in the **Wiki**.

---

## How to Run

From the project root:

`clj -M`

Or from a REPL:

`(require '[decision-tree-id3.core :as core])`


This will:
* run the full ID3 pipeline step-by-step on the **Loan Approval** dataset
* evaluate the model on **Car Evaluation** and **Play Tennis** datasets using the experimental API
* print evaluation metrics and sample predictions
* run performance benchmarks using Criterium

---

## Experimental API

A clean experimental interface is provided in `experiment.clj`

`run-id3`

Executes the full ID3 pipeline: preprocessing, train/test split, training,
prediction and evaluation.

    (run-id3 dataset label-key positive-label)

Returns:

    {:tree ...
    :train-size ...
    :test-size ...
    :metrics {:accuracy ... :precision ... :recall ... :f1 ...}}

Example usage:

    (require '[decision-tree-id3.csv-loader :as loader])
    (require '[decision-tree-id3.experiment :as exp])
    
    (def dataset
    (loader/load-csv->maps "csv/loan_approval_dataset.csv"))
    
    (exp/run-id3 dataset :loan_status "Approved")

This API allows quick experimentation on different datasets without duplicating pipeline logic. It is not a web service API, but a library-level API.

`bench-id3`

Benchmarks **ID3 tree construction** only using Criterium `quick-bench`.

    (bench-id3 dataset label-key)

The function prints a full Criterium report and returns dataset sizes.

Example usage:

    (exp/bench-id3 dataset :loan_status)

---

## Performance Benchmarking

The ID3 decision tree construction performance was evaluated using the **Criterium** benchmarking library.

Tree construction time is measured using Criterium `quick-bench`, and
`with-progress-reporting` is used for longer runs and the 5x dataset scalability test.

Tree construction time:
- Small/medium datasets: ~10-30 ms
- Larger dataset (5x duplicated): ~120 ms

Outliers may appear due to JVM warm-up, GC, and OS scheduling. Mean and quantiles are reported.

### Optimization Summary

| Optimization                                      | 	Effect                                               |
|---------------------------------------------------|-------------------------------------------------------|
| Entropy memoization `entropy-memo`                | 	~2.5x speedup (from ~86ms to ~31ms)                  |
| Entropy computed with `reduce-kv`                 | Optimized entropy computation                         |
| `same-label?` rewritten using `every?`            | Early exit when labels differ (avoids full traversal) |
| `build-tree` uses `mapv` for branches             | Avoids lazy sequences during tree construction        |
| `split-by-attribute` materializes dataset (`vec`) | Improves performance of repeated grouping operations  |

These optimizations were applied incrementally during development, and the reported speedup reflects the combined effect observed in Criterium benchmarks.


### Results Summary

| Dataset        | Accuracy | Precision | Recall | F1 score |
|----------------|----------|-----------|--------|----------|
| Loan Approval  | ~86%     | ~88%      | ~90%   | ~89%     |
| Car Evaluation | ~92%     | ~85%      | ~75%   | ~80%     |
| Play Tennis    | ~89%     | ~86%      | ~98%   | ~92%     |

---

## Tests

Tests cover all major components, including the experimental API.

Tests are executed from the REPL using `clojure.test`.

To run a specific test namespace:

    (require '[clojure.test :refer :all])
    (require 'decision-tree-id3.experiment-test)
    (run-tests 'decision-tree-id3.experiment-test)

To run all project tests:

    (require '[clojure.test :refer :all])
    (run-all-tests)

All tests are located in the `test/` directory and follow the same namespace structure as the source files.

`core.clj` is mainly a pipeline script and does not have tests.

---

## Dependencies

- Clojure 1.12.4
- clojure.data.csv 1.1.1
- Criterium 0.4.6

---

## References
- Quinlan, J.R. (1986), _Induction of Decision Trees_
- [Decision Tree in Machine Learning](https://www.geeksforgeeks.org/machine-learning/decision-tree-introduction-example/)
- [Decision Trees: ID3 Algorithm Explained](https://medium.com/data-science/decision-trees-for-classification-id3-algorithm-explained-89df76e72df1)
- [ID3, C4.5, CART and Pruning](https://bitmask93.github.io/ml-blog/ID3-C4-5-CART-and-Pruning/)