(ns decision-tree-id3.entropy)

(defn log2 [x]
  (/ (Math/log x) (Math/log 2)))

(defn entropy
  "Computes entropy of a dataset based on :label."
  [dataset label-key]
  (let [total (count dataset)
        freqs (frequencies (map label-key dataset))]
    (reduce-kv
      (fn [acc _ count]
        (let [p (/ count total)]
          (if (zero? p)
            acc
            (- acc (* p (log2 p))))))
      0.0
      freqs)))

(def entropy-memo
  (memoize entropy))