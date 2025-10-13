#! /bin/bash
problem='logistics-E'
domain='logistics'

mkdir outputs/$domain/$problem -p

java -cp out:lib/*:. -Xmx11G com.suspiciousbehaviour.app.Main \
  --obfuscating \
  --calculate_distances \
  \
  --primary_goal 5 \
  --secondary_goal 1 \
  \
  --directed_search_distance 6 \
  --directed_min_goal_distance 4 \
  --directed_goal_switch_radius 1 \
  \
  --purposefulE 0.4 \
  \
  --goaldanger="0000011" \
  \
  -o outputs/$domain/$problem \
  -d PDDL/$domain/domain.pddl \
  -p PDDL/$domain/$problem/core-problem.pddl \
  PDDL/$domain/$problem/goal-1.pddl \
  PDDL/$domain/$problem/goal-2.pddl \
  PDDL/$domain/$problem/goal-3.pddl \
  PDDL/$domain/$problem/goal-4.pddl \
  PDDL/$domain/$problem/goal-5.pddl \
  PDDL/$domain/$problem/goal-6.pddl \
  PDDL/$domain/$problem/goal-7.pddl 
