(define (problem p01)
(:domain logistics)
(:objects
    plane1 - airplane
    pos1_4 pos2_1 pos3_2 pos4_1 - airport
    pos1_2 pos1_1 pos1_3   pos2_2   pos3_1  pos4_2 pos4_3 pos4_4 - location
    city1 city2 city3 city4  - city
    truck1 truck2 truck3 truck4 - truck
    pkg1 pkg2 pkg3 pkg4 pkg5 pkg6 - package
)
(:init 
    (at plane1 pos2_1)
    (at truck1 pos1_1)
    (at truck2 pos2_1)
    (at truck3 pos3_1)
    (at truck4 pos4_1)
    (at pkg1 pos2_2)
    (at pkg2 pos2_2)
    (at pkg3 pos3_1)
    (at pkg4 pos3_1)
    (at pkg5 pos4_4)
    (at pkg6 pos4_4)
    (in-city pos1_1 city1)
    (in-city pos1_2 city1)
    (in-city pos1_3 city1)
    (in-city pos1_4 city1)
    (in-city pos2_1 city2)
    (in-city pos2_2 city2)
    (in-city pos3_1 city3)
    (in-city pos3_2 city3)
    (in-city pos4_1 city4)
    (in-city pos4_2 city4)
    (in-city pos4_3 city4)
    (in-city pos4_4 city4)
    (iscity city1)
    (iscity city2)
    (iscity city3)
    (iscity city4)
)
)