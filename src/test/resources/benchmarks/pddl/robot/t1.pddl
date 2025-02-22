(define (problem robot-move-example)
  (:domain robot-move)
  
  (:objects
    robot1 - robot
    block1 block2 - object
    loc1 loc2 loc3 - location
  )
  
  (:init
    (robot-at robot1 loc1)
    (object-at block1 loc1)
    (object-at block2 loc2)
    (clear loc1)
    (clear loc2)
    (clear loc3)
  )
  
  (:goal
    (and
      (object-at block1 loc3)
      (object-at block2 loc1)
    )
  )
)

