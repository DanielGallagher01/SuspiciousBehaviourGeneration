(define (domain robot-move)
  (:requirements :strips)
  
  (:predicates
    (robot-at ?robot ?location)
    (object-at ?object ?location)
    (clear ?location)
    (holding ?robot ?object)
  )
  
  (:action move
    :parameters (?robot ?from ?to)
    :precondition (and (robot-at ?robot ?from) (clear ?to))
    :effect (and (not (robot-at ?robot ?from)) (robot-at ?robot ?to) (not (clear ?to)) (clear ?from))
  )
  
  (:action pickup
    :parameters (?robot ?object ?location)
    :precondition (and (robot-at ?robot ?location) (clear ?location) (object-at ?object ?location))
    :effect (and (holding ?robot ?object) (not (object-at ?object ?location)) (not (clear ?location)))
  )
  
  (:action drop
    :parameters (?robot ?object ?location)
    :precondition (and (holding ?robot ?object) (clear ?location))
    :effect (and (not (holding ?robot ?object)) (object-at ?object ?location) (not (clear ?location)) (clear ?location))
  )
)

