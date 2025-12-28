(ns clothing-recommender.user)

(defn make-user
  [id name preferences sizes budget]
  {:user-id           id
   :name              name
   :preferences       preferences
   :sizes             sizes
   :budget            budget
   })

(defn valid-user?
  [u]
  (and (number? (:user-id u))
       (string? (:name u))
       (map? (:preferences u))
       (map? (:sizes u))
       (number? (:budget u))
       ))