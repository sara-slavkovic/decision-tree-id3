(ns clothing-recommender.normalization)

;;------------------------------------
;; Helper functions
;; -----------------------------------

(defn clamp
  [x]
  (cond
    (< x 0.0) 0.0
    (> x 1.0) 1.0
    :else x))

;;------------------------------------
;; Normalization functions
;; -----------------------------------

(defn min-max
  [values]
  {:min (apply min values)
   :max (apply max values)})

(defn normalize
  [x min max]
  (if (= min max)
    0.0
    (/ (double (- x min))
       (double (- max min)))))

(defn normalize-products
  [products]
  (let [{pmin :min pmax :max}
        (min-max (map :price products))

        {rmin :min rmax :max}
        (min-max (map :rating products))]
    (map (fn [p]
           (assoc p
             :price-norm  (normalize (:price p) pmin pmax)
             :rating-norm (normalize (:rating p) rmin rmax)))
         products)))

(defn normalize-user
  [user products]
  (let [{pmin :min pmax :max}
        (min-max (map :price products))

        {:keys [min max]}
        (min-max (map :rating products))

        min-rating (get-in user [:preferences :min-rating] min)]
    (-> user
        (assoc :budget-norm
               (clamp (normalize (:budget user) pmin pmax)))
        (assoc :min-rating-norm
               (clamp (normalize min-rating min max))))))


