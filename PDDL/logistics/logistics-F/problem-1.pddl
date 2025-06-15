(define (problem p01)
(:domain logistics)
(:objects
    plane1 - airplane
    pos1_4 pos2_3 pos3_1 pos4_1 - airport
    pos1_1 pos1_2 pos1_3 pos2_1 pos2_2 pos2_4 pos3_2 pos3_3 pos4_2 pos4_3 - location
    city1 city2 city3 city4 - city
    truck1 truck2 truck3 truck4 - truck
    pkg1 pkg2 pkg3 - package
    background
)
(:init 
    (at plane1 pos1_4)
    (at truck1 pos1_1)
    (at truck2 pos2_1)
    (at truck3 pos3_1)
    (at truck4 pos4_1)
    (at pkg1 pos1_2)
    (at pkg2 pos2_2)
    (at pkg3 pos3_3)
    (in-city pos1_1 city1)
    (in-city pos1_2 city1)
    (in-city pos1_3 city1)
    (in-city pos1_4 city1)
    (in-city pos2_1 city2)
    (in-city pos2_2 city2)
    (in-city pos2_3 city2)
    (in-city pos2_4 city2)
    (in-city pos3_1 city3)
    (in-city pos3_2 city3)
    (in-city pos3_3 city3)
    (in-city pos4_1 city4)
    (in-city pos4_2 city4)
    (in-city pos4_3 city4)
    (city city1)
    (city city2)
    (city city3)
    (city city4)
)
(:goal 
	(and (at pkg3 pos2_1))
)
)
