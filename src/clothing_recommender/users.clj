(ns clothing-recommender.users
  (:require [clothing-recommender.user :as user]))

(def sara
  (user/make-user
    1
    "Sara"
    {:categories ["Women's Fashion"]
     :brands     ["Adidas" "Nike"]
     :colors     ["Black" "White"]
     :min-rating 4.0}
    {:tops "M" :pants "S" :shoes "M"}
    120.0))

(def marko
  (user/make-user
    2
    "Marko"
    {:categories ["Men's Fashion"]
     :brands     ["Nike" "Adidas"]
     :colors     ["Black" "Blue"]
     :min-rating 4.2}
    {:tops "L" :pants "M" :shoes "L"}
    200.0))

(def jelena
  (user/make-user
    3
    "Jelena"
    {:categories ["Women's Fashion"]
     :brands     ["Zara" "Gucci"]
     :colors     ["Red" "Yellow"]
     :min-rating 3.8}
    {:tops "S" :pants "S" :shoes "S"}
    150.0))

(def mihajlo
  (user/make-user
    4
    "Mihajlo"
    {:categories ["Men's Fashion"]
     :brands     ["Adidas" "H&M"]
     :colors     ["Black" "Green"]
     :min-rating 4.0}
    {:tops "XL" :pants "XL" :shoes "XL"}
    60.0))

(def all-users
  [sara marko jelena mihajlo])
