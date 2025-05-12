(define (domain grid)
(:requirements :strips)
(:predicates (conn ?x ?y)
             (at ?r ?x )
	         (at-robot ?x ?r)
             (place ?p)
             (open ?x)
)

(:action move
:parameters (?curpos ?nextpos ?robot)
:precondition (and (place ?curpos) (place ?nextpos)
               (at-robot ?curpos ?robot) (conn ?curpos ?nextpos) (open ?nextpos))
:effect (and (at-robot ?nextpos ?robot) (not (at-robot ?curpos ?robot))))
)


	
