(ns clothing-recommender.preprocessing)

(defn attributes
  "Returns all attribute keys except label."
  [dataset label-key]
  (-> dataset first keys set (disj label-key)))

(defn numeric-attribute?
  [dataset attr]
  (every? number? (map attr dataset)))

(defn build-numeric-discretizer
  "Creates a discretizer function for a numeric attribute."
  [values]
  (let [minv (apply min values)
        maxv (apply max values)
        step (/ (- maxv minv) 3)]
    (fn [v]
      (cond
        (< v (+ minv step))        :low
        (< v (+ minv (* 2 step)))  :medium
        :else                      :high))))

(defn build-discretizers
  "Builds discretizers for all numeric attributes."
  [dataset label-key]
  (let [attrs (attributes dataset label-key)]
    (reduce
      (fn [acc attr]
        (if (numeric-attribute? dataset attr)
          (assoc acc attr
                     (build-numeric-discretizer (map attr dataset)))
          acc))
      {}
      attrs)))

(defn discretize-instance
  [instance discretizers]
  (reduce
    (fn [inst [attr f]]
      (update inst attr f))
    instance
    discretizers))

(defn discretize-dataset
  "Returns {:data discretized-data :discretizers discretizers}"
  [dataset label-key]
  (let [discretizers (build-discretizers dataset label-key)
        data (map #(discretize-instance % discretizers) dataset)]
    {:data data
     :discretizers discretizers}))
