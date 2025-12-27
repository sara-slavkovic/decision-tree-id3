(ns clothing-recommender.users
  (:require [clothing-recommender.user :as user]))

(def sara
  (user/make-user
    1
    "Sara"
    ["casual" "sporty"]
    {:tops "M" :pants "S" :shoes "38"}
    120.0))

(def marko
  (user/make-user
    2
    "Marko"
    ["formal" "classic"]
    {:tops "L" :pants "M" :shoes "44"}
    200.0))

(def jelena
  (user/make-user
    3
    "Jelena"
    ["boho" "vintage"]
    {:tops "S" :pants "S" :shoes "37"}
    150.0))

(def mihajlo
  (user/make-user
    4
    "Mihajlo"
    ["streetwear" "sporty"]
    {:tops "XL" :pants "XL" :shoes "46"}
    60.0))

(def all-users
  [sara marko jelena mihajlo])
