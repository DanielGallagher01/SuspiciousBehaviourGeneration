(define (problem p01)
(:domain logistics)
(:objects
plane1 - airplane
apt1_1 apt1_2 - airport
pos1_1 pos2_1 pos3_1 pos1_2 pos2_2 pos3_2 - location
city2 city1 - city
truck2 truck1 - truck
pkg1 pkg2 pkg3 pkg4 pkg5 pkg6 - package
)
(:init 
(at plane1 apt1_2)
(at truck1 pos1_1)
(at truck2 pos2_2)
(at pkg1 pos1_1)
(at pkg2 pos2_1)
(at pkg3 pos3_1)
(at pkg4 pos1_2)
(at pkg5 pos2_2)
(at pkg6 pos3_2)
(in-city apt1_1 city1)
(in-city pos1_1 city1)
(in-city pos2_1 city1)
(in-city pos3_1 city1)
(in-city apt1_2 city2)
(in-city pos1_2 city2)
(in-city pos2_2 city2)
(in-city pos3_2 city2)
)
(:goal 
	(and (at pkg1 pos3_1))
)
)
