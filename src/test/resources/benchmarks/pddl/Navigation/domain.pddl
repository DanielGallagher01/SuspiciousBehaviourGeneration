(define (domain navigation)
  (:requirements :strips)
  (:types position)
  (:predicates (at-player ?l)
    	       (h-adjacent ?l1 ?l2)
    	       (v-adjacent ?l1 ?l2)
    	       (at-wall ?l)
  )

  ;; Move Player Up
  (:action up
    :effect (forall (?l1 ?l2 - position) 
      (when (and (at-player ?l1) 
                (v-adjacent ?l1 ?l2)
                (not (at-wall ?l2)))
        (and (not (at-player ?l1)) 
             (at-player ?l2)))
    )
  )

  ;; Move Player Down
  (:action down
    :effect (forall (?l1 ?l2 - position)
      (when (and (at-player ?l1) 
                (v-adjacent ?l2 ?l1)
                (not (at-wall ?l2)))
        (and (not (at-player ?l1)) 
             (at-player ?l2)))
    )
  )

  ;; Move Player Left
  (:action left
    :effect (forall (?l1 ?l2 - position)
      (when (and (at-player ?l1) 
                (h-adjacent ?l2 ?l1)
                (not (at-wall ?l2)))
        (and (not (at-player ?l1)) 
             (at-player ?l2)))
    )
  )

  ;; Move Player Right
  (:action right
    :effect (forall (?l1 ?l2 - position)
      (when (and (at-player ?l1) 
                (h-adjacent ?l1 ?l2) 
                (not (at-wall ?l2)))
        (and (not (at-player ?l1)) 
             (at-player ?l2)))
    )
  )
)

