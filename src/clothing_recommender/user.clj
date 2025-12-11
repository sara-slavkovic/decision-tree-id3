(ns clothing-recommender.user)

(defn make-user
  [id name style-preferences sizes budget]
  {:user-id           id
   :name              name
   :style-preferences style-preferences
   :sizes             sizes
   :budget            budget
   })

(defn valid-user?
  [u]
  (and (:user-id u)
       (:name u)
       (map? (:sizes u))
       (number? (:budget u))
       ))