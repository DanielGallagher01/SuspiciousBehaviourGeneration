;; logistics domain Typed version.
;;

(define (domain logistics)
  (:requirements :strips :typing :negative-preconditions :disjunctive-preconditions) 
  (:types truck
          airplane - vehicle
          package
          vehicle - physobj
          airport
          location - place
          city
          place 
          physobj - object
          slot
          )
  
  (:predicates 	
    (in-city ?loc - place ?city - city)
		(at ?obj - physobj ?loc - place)
		(in ?pkg - package ?veh - vehicle)
		(in-slot ?pkg - package ?t - truck ?s - slot)
    (slot-free ?t - truck ?s - slot)
    (in-slot-airplane ?pkg - package ?a - airplane ?s - slot)
    (slot-free-airplane ?a - airplane ?s - slot)
		(iscity ?city - city)
  )
  
;; Load into a truck slot
  (:action LOAD-TRUCK
    :parameters (?pkg - package ?t - truck ?loc - place ?s - slot)
    :precondition (and 
      (at ?pkg ?loc)
      (at ?t ?loc)
      (slot-free ?t ?s))
    :effect (and
      (not (at ?pkg ?loc))
      (in-slot ?pkg ?t ?s)
      (in ?pkg ?t)
      (not (slot-free ?t ?s))))

; (:action LOAD-AIRPLANE
;   :parameters   (?pkg - package ?airplane - airplane ?loc - place)
;   :precondition (and (at ?pkg ?loc) (at ?airplane ?loc))
;   :effect       (and (not (at ?pkg ?loc)) (in ?pkg ?airplane)))

(:action UNLOAD-TRUCK
    :parameters (?pkg - package ?t - truck ?loc - place ?s - slot)
    :precondition (and 
      (at ?t ?loc)
      (in-slot ?pkg ?t ?s))
    :effect (and
      (not (in-slot ?pkg ?t ?s))
      (not (in ?pkg ?t))
      (at ?pkg ?loc)
      (slot-free ?t ?s)))

; (:action UNLOAD-AIRPLANE
;   :parameters    (?pkg - package ?airplane - airplane ?loc - place)
;   :precondition  (and (in ?pkg ?airplane) (at ?airplane ?loc))
;   :effect        (and (not (in ?pkg ?airplane)) (at ?pkg ?loc)))

(:action LOAD-AIRPLANE-SLOT
  :parameters (?pkg - package ?a - airplane ?loc - place ?s - slot)
  :precondition (and
    (at ?pkg ?loc)
    (at ?a ?loc)
    (slot-free-airplane ?a ?s))
  :effect (and
    (not (at ?pkg ?loc))
    (in-slot-airplane ?pkg ?a ?s)
    (in ?pkg ?a)
    (not (slot-free-airplane ?a ?s))))

(:action UNLOAD-AIRPLANE-SLOT
  :parameters (?pkg - package ?a - airplane ?loc - place ?s - slot)
  :precondition (and
    (at ?a ?loc)
    (in-slot-airplane ?pkg ?a ?s))
  :effect (and
    (not (in-slot-airplane ?pkg ?a ?s))
    (not (in ?pkg ?a))
    (at ?pkg ?loc)
    (slot-free-airplane ?a ?s)))

(:action DRIVE-TRUCK
  :parameters (?truck - truck ?loc-from - place ?loc-to - place ?city - city)
  :precondition
   (and (at ?truck ?loc-from) (not (at ?truck ?loc-to)) (in-city ?loc-from ?city) (in-city ?loc-to ?city))
  :effect
   (and (not (at ?truck ?loc-from)) (at ?truck ?loc-to)))

(:action FLY-AIRPLANE
  :parameters (?airplane - airplane ?loc-from - airport ?loc-to - airport)
  :precondition
   (and (at ?airplane ?loc-from) (not (at ?airplane ?loc-to)))
  :effect
   (and (not (at ?airplane ?loc-from)) (at ?airplane ?loc-to)))

(:action wait
 :parameters ()
 :precondition (and)
 :effect (and)
)

)