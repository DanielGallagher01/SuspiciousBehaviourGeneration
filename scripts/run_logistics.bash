#! /bin/bash
problem='logistics-A'
domain='logistics'

mkdir outputs/$domain/$problem -p

java -cp out:lib/*:. -Xmx11G com.suspiciousbehaviour.app.Main \
  --primary_goal 3 \
  --obfuscating \
  --purposefulE 0.4 \
  --unexpected \
  --secondary_goal 1 \
  --loitering \
  --shoe_tie \
  -o outputs/$domain/$problem \
  -d PDDL/$domain/domain.pddl \
  -p PDDL/$domain/$problem/core-problem.pddl \
  PDDL/$domain/$problem/goal-1.pddl \
  PDDL/$domain/$problem/goal-2.pddl \
  PDDL/$domain/$problem/goal-3.pddl \
  PDDL/$domain/$problem/goal-4.pddl
