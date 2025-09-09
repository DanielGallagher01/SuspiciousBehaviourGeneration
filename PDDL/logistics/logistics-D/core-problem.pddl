(define (problem p01)
(:domain logistics)
(:objects
    plane1 - airplane
    pos1_1 pos2_1 pos3_1 - airport
    pos2_2 pos2_3 pos2_4 pos2_5   pos3_2 pos3_3 pos3_4 pos3_5 - location
    city1 city2 city3  - city
    truck1 truck2 truck3 - truck
    pkg1 pkg2 pkg3 pkg4 pkg5 pkg6 - package
    background
)
(:init 
    (at plane1 pos2_1)
    (at truck1 pos1_1)
    (at truck2 pos2_1)
    (at truck3 pos3_1)
    (at pkg1 pos1_1)
    (at pkg2 pos1_1)
    (at pkg3 pos2_3)
    (at pkg4 pos2_3)
    (at pkg5 pos3_4)
    (at pkg6 pos3_4)
    (in-city pos1_1 city1)
    (in-city pos2_1 city2)
    (in-city pos2_2 city2)
    (in-city pos2_3 city2)
    (in-city pos2_4 city2)
    (in-city pos2_5 city2)
    (in-city pos3_1 city3)
    (in-city pos3_2 city3)
    (in-city pos3_3 city3)
    (in-city pos3_4 city3)
    (in-city pos3_5 city3)
    (iscity city1)
    (iscity city2)
    (iscity city3)
)
)