(define (domain grid)
(:requirements :strips :typing)
(:predicates (conn ?x ?y)
             (at ?r ?x )
	           (at-robot ?x ?r)
             (place ?p)
             (open ?x)
             (is-goal ?x)
             (is-goal-dangerous ?x)
)

(:action move
:parameters (?curpos ?nextpos ?robot)
:precondition (and (place ?curpos) (place ?nextpos)
               (at-robot ?curpos ?robot) (conn ?curpos ?nextpos) (open ?nextpos))
:effect 
  (and 
    (not (at-robot ?curpos ?robot)) 
    (at-robot ?nextpos ?robot)
  )
)
)
