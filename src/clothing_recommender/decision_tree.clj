(ns clothing-recommender.decision-tree)

;; -------------------------
;; Helper functions
;; -------------------------

(defn in?
  [coll x]
  (some #(= % x) coll))

(defn product-size-type
  [product]
  (case (:product-name product)
    "T-shirt"        :tops
    "Sweater"        :tops
    "Jeans"          :pants
    "Shoes"          :shoes
    "Dress"          :dress
    nil))

;; -------------------------
;; Decision nodes (scoring)
;; -------------------------

(defn price-score
  [user product]
  (let [budget (:budget-norm user)
        price  (:price-norm product)]
    (cond
      (<= price budget)           30
      (<= price (* 1.2 budget))   15
      :else                        0)))

(defn size-score
  [user product]
  (let [product-type (product-size-type product)
        user-sizes (:sizes user)
        product-size (:size product)]
    (cond
      ;; tops, pants, shoes - direct comparison
      (#{:tops :pants :shoes} product-type)
      (if (= (get user-sizes product-type) product-size)
        25
        0)

      ;; dress - partial comparison
      (= product-type :dress)
      (cond
        (and (= (:tops user-sizes) product-size)
             (= (:pants user-sizes) product-size))
        25

        (or (= (:tops user-sizes) product-size)
            (= (:pants user-sizes) product-size))
        15

        :else 0)

      :else 0)))

(defn rating-score
  [user product]
  (let [min-rating (:min-rating-norm user)
        rating     (:rating-norm product)]
    (cond
      (>= rating min-rating)          20
      (>= rating (- min-rating 0.1))  10
      :else                           0)))

(defn category-score
  [user product]
  (let [preferred (get-in user [:preferences :categories])
        category  (:category product)]
    (if (and preferred (in? preferred category))
      10
      0)))

(defn brand-score
  [user product]
  (let [preferred (get-in user [:preferences :brands])
        brand     (:brand product)]
    (if (and preferred (in? preferred brand))
      10
      0)))

(defn color-score
  [user product]
  (let [preferred (get-in user [:preferences :colors])
        color     (:color product)]
    (if (and preferred (in? preferred color))
      5
      0)))

;; -------------------------
;; Final decision tree
;; -------------------------

(defn decision-tree-score
  "Evaluates a single product for a given user and returns a score (0–100)."
  [user product]
  (+ (price-score user product)
     (size-score user product)
     (rating-score user product)
     (category-score user product)
     (brand-score user product)
     (color-score user product)))

(defn recommend
  "Scores all products for a user and returns top N recommendations."
  [user products n]
  (->> products
       (map (fn [p]
              {:product p
               :score   (decision-tree-score user p)}))
       (sort-by :score >)
       (take n)))
