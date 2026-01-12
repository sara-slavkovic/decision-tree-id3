(ns clothing-recommender.ml-recommender
  (:require
    [clothing-recommender.model-store :as ms]
    [clothing-recommender.product-repository :as repo]
    [clothing-recommender.training-data :as td]
    [clothing-recommender.discretization :as disc]
    [clothing-recommender.id3 :as id3]))

(defn product->instance
  "Builds feature vector for (user, product) pair."
  [user product]
  (td/feature-vector user product))

(defn predict-product
  [model discretizers user product]
  (let [instance (product->instance user product)
        disc-inst (disc/discretize-instance instance discretizers)]
    (id3/predict model disc-inst)))

(defn recommend-for-user
  "Returns n products recommended for a user using trained ML model."
  [user n]
  (let [model        (ms/load-model)
        products     (repo/find-all)
        ;; build discretizers from train dataset
        train-data   (-> "resources/dataset.edn"
                         slurp
                         read-string)
        discretizers (disc/build-discretizers train-data)]
    (->> products
         (map (fn [p]
                {:product p
                 :prediction (predict-product model discretizers user p)}))
         (filter #(= :recommend (:prediction %)))
         (map :product)
         (take n))))

;(require '[clothing-recommender.ml-recommender :as rec])
;(require '[clothing-recommender.user :as u])
;(def sara
;  (u/make-user
;    1
;    "Sara"
;    {:categories ["Women's Fashion"]
;     :brands     ["Adidas" "Nike"]
;     :colors     ["Black" "White"]
;     :min-rating 4.0}
;    {:tops "M" :pants "S" :shoes "M"}
;    120.0))
;(rec/recommend-for-user sara 5)