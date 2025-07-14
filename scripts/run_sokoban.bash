#! /bin/bash
problem='sokoban-11'
domain='sokoban'

mkdir outputs/$domain/$problem -p

java -cp out:lib/*:. -Xmx11G com.suspiciousbehaviour.app.Main \
  --obfuscating \
  --directed \
  --optimal \
  --unexpected \
  --shoe_tie \
  --loitering \
  \
  --primary_goal 4 \
  --secondary_goal 1 \
  \
  --directed_search_distance 14 \
  --directed_min_goal_distance 10 \
  --directed_goal_switch_radius 3 \
  \
  --purposefulE 0.6 \
  \
  -o outputs/$domain/$problem \
  -d PDDL/$domain/domain.pddl \
  -p PDDL/$domain/$problem/core-problem.pddl \
  PDDL/$domain/$problem/goal-1.pddl \
  PDDL/$domain/$problem/goal-2.pddl \
  PDDL/$domain/$problem/goal-3.pddl \
  PDDL/$domain/$problem/goal-4.pddl \
  PDDL/$domain/$problem/goal-5.pddl
