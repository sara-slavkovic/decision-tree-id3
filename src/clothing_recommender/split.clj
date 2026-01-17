(ns clothing-recommender.split)

(defn train-test-split
  "Splits dataset into train and test sets using ratio.
   Ratio is the proportion used for training."
  [dataset ratio]
  (let [shuffled (shuffle dataset)
        split-at (int (* ratio (count dataset)))]
    {:train (subvec (vec shuffled) 0 split-at)
     :test  (subvec (vec shuffled) split-at)}))