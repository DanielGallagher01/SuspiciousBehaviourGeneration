(define (problem p01)
(:domain logistics)
(:objects
    plane1 - airplane
    pos1_2 pos2_2 - airport
    pos1_1   pos2_1  - location
    city1 city2 - city
    truck1 truck2 - truck
    pkg1 pkg2 pkg3 - package
    background
)
(:init 
    (at plane1 pos2_2)
    (at truck1 pos1_1)
    (at truck2 pos2_1)
    (at pkg1 pos1_1)
    (at pkg2 pos1_1)
    (at pkg3 pos2_1)
    (in-city pos1_1 city1)
    (in-city pos1_2 city1)
    (in-city pos2_1 city2)
    (in-city pos2_2 city2)
    (city city1)
    (city city2)
)
(:goal 
	(and (at pkg1 pos2_1) (at pkg2 pos2_1) (at pkg3 pos2_1))
)
)