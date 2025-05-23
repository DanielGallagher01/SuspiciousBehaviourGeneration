(define (problem p02)
(:domain logistics)
(:objects
plane1 - airplane
apt1_1 apt1_2 apt1_3 - airport
pos1_1 pos2_1 pos3_1 pos1_2 pos2_2 pos3_2 pos1_3 pos2_3 pos3_3 - location
city1 city2 city3 - city
truck1 truck2 truck3 - truck
pkg1 pkg2 pkg3 pkg4 pkg5 - package
)
(:init 
(at plane1 apt1_2)
(at truck1 pos1_1)
(at truck2 pos2_2)
(at truck2 pos1_3)

(at pkg1 pos2_1)
(at pkg2 pos3_1)
(at pkg3 pos2_2)
(at pkg4 pos3_2)
(at pkg5 pos3_3)

(in-city apt1_1 city1)
(in-city pos1_1 city1)
(in-city pos2_1 city1)
(in-city pos3_1 city1)

(in-city apt1_2 city2)
(in-city pos1_2 city2)
(in-city pos2_2 city2)
(in-city pos3_2 city2)

(in-city apt1_3 city3)
(in-city pos1_3 city3)
(in-city pos2_3 city3)
(in-city pos3_3 city3)
)
(:goal 
	<GOAL>
)
)
