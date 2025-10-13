#! /bin/bash
problem='logistics-B'
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


problem='logistics-C'
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


problem='logistics-D'
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


problem='bank-16-B'
domain='navigation'

mkdir outputs/$domain/$problem -p

java -cp out:lib/*:. -Xmx11G com.suspiciousbehaviour.app.Main \
  --obfuscating \
  --calculate_distances \
  \
  --primary_goal 3 \
  --secondary_goal 1 \
  \
  --directed_search_distance 10 \
  --directed_min_goal_distance 6 \
  --directed_goal_switch_radius 3 \
  \
  --purposefulE 0.5 \
  \
  -o outputs/$domain/$problem \
  -d PDDL/$domain/domain.pddl \
  -p PDDL/$domain/$problem/core-problem.pddl \
  PDDL/$domain/$problem/goal-1.pddl \
  PDDL/$domain/$problem/goal-2.pddl \
  PDDL/$domain/$problem/goal-3.pddl \
  PDDL/$domain/$problem/goal-4.pddl \
  PDDL/$domain/$problem/goal-5.pddl



problem='bank-16-C'
domain='navigation'

mkdir outputs/$domain/$problem -p

java -cp out:lib/*:. -Xmx11G com.suspiciousbehaviour.app.Main \
  --obfuscating \
  --calculate_distances \
  \
  --primary_goal 3 \
  --secondary_goal 1 \
  \
  --directed_search_distance 10 \
  --directed_min_goal_distance 6 \
  --directed_goal_switch_radius 3 \
  \
  --purposefulE 0.5 \
  \
  -o outputs/$domain/$problem \
  -d PDDL/$domain/domain.pddl \
  -p PDDL/$domain/$problem/core-problem.pddl \
  PDDL/$domain/$problem/goal-1.pddl \
  PDDL/$domain/$problem/goal-2.pddl \
  PDDL/$domain/$problem/goal-3.pddl \
  PDDL/$domain/$problem/goal-4.pddl \
  PDDL/$domain/$problem/goal-5.pddl



problem='bank-16-D'
domain='navigation'

mkdir outputs/$domain/$problem -p

java -cp out:lib/*:. -Xmx11G com.suspiciousbehaviour.app.Main \
  --obfuscating \
  --calculate_distances \
  \
  --primary_goal 3 \
  --secondary_goal 1 \
  \
  --directed_search_distance 10 \
  --directed_min_goal_distance 6 \
  --directed_goal_switch_radius 3 \
  \
  --purposefulE 0.5 \
  \
  -o outputs/$domain/$problem \
  -d PDDL/$domain/domain.pddl \
  -p PDDL/$domain/$problem/core-problem.pddl \
  PDDL/$domain/$problem/goal-1.pddl \
  PDDL/$domain/$problem/goal-2.pddl \
  PDDL/$domain/$problem/goal-3.pddl \
  PDDL/$domain/$problem/goal-4.pddl \
  PDDL/$domain/$problem/goal-5.pddl



problem='bank-16-E'
domain='navigation'

mkdir outputs/$domain/$problem -p

java -cp out:lib/*:. -Xmx11G com.suspiciousbehaviour.app.Main \
  --obfuscating \
  --calculate_distances \
  \
  --primary_goal 3 \
  --secondary_goal 1 \
  \
  --directed_search_distance 10 \
  --directed_min_goal_distance 6 \
  --directed_goal_switch_radius 3 \
  \
  --purposefulE 0.5 \
  \
  -o outputs/$domain/$problem \
  -d PDDL/$domain/domain.pddl \
  -p PDDL/$domain/$problem/core-problem.pddl \
  PDDL/$domain/$problem/goal-1.pddl \
  PDDL/$domain/$problem/goal-2.pddl \
  PDDL/$domain/$problem/goal-3.pddl \
  PDDL/$domain/$problem/goal-4.pddl \
  PDDL/$domain/$problem/goal-5.pddl


