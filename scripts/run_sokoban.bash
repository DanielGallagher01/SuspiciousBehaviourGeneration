#! /bin/bash
problem='sokoban-1-10-C'
domain='sokoban'

mkdir outputs/$domain/$problem -p

java -cp out:lib/*:. -Xmx11G com.suspiciousbehaviour.app.Main \
  --loitering \
  \
  --primary_goal 3 \
  --secondary_goal 1 \
  \
  --directed_search_distance 14 \
  --directed_min_goal_distance 10 \
  --directed_goal_switch_radius 3 \
  \
  --purposefulE 0.9 \
  \
  --goaldanger="00011" \
  \
  -o outputs/$domain/$problem \
  -d PDDL/$domain/domain.pddl \
  -p PDDL/$domain/$problem/core-problem.pddl \
  PDDL/$domain/$problem/goal-1.pddl \
  PDDL/$domain/$problem/goal-2.pddl \
  PDDL/$domain/$problem/goal-3.pddl \
  PDDL/$domain/$problem/goal-4.pddl \
  PDDL/$domain/$problem/goal-5.pddl
