#! /bin/bash
problem='maze-11'
domain='navigation'

mkdir outputs/$domain/$problem -p

java -cp out:lib/*:. -Xmx11G com.suspiciousbehaviour.app.Main \
  --directed \
  --optimal \
  --obfuscating \
  --unexpected \
  --shoe_tie \
  --loitering \
  \
  --primary_goal 4 \
  --secondary_goal 1 \
  \
  --directed_search_distance 10 \
  --directed_min_goal_distance 8 \
  --directed_goal_switch_radius 3 \
  \
  --purposefulE 0.4 \
  \
  -o outputs/$domain/$problem \
  -d PDDL/$domain/domain.pddl \
  -p PDDL/$domain/$problem/core-problem.pddl \
  PDDL/$domain/$problem/goal-1.pddl \
  PDDL/$domain/$problem/goal-2.pddl \
  PDDL/$domain/$problem/goal-3.pddl \
  PDDL/$domain/$problem/goal-4.pddl \
  PDDL/$domain/$problem/goal-5.pddl


problem='maze-13'
domain='navigation'

mkdir outputs/$domain/$problem -p

java -cp out:lib/*:. -Xmx11G com.suspiciousbehaviour.app.Main \
  --directed \
  --optimal \
  --obfuscating \
  --unexpected \
  --shoe_tie \
  --loitering \
  \
  --primary_goal 4 \
  --secondary_goal 1 \
  \
  --directed_search_distance 11 \
  --directed_min_goal_distance 8 \
  --directed_goal_switch_radius 3 \
  \
  --purposefulE 0.4 \
  \
  -o outputs/$domain/$problem \
  -d PDDL/$domain/domain.pddl \
  -p PDDL/$domain/$problem/core-problem.pddl \
  PDDL/$domain/$problem/goal-1.pddl \
  PDDL/$domain/$problem/goal-2.pddl \
  PDDL/$domain/$problem/goal-3.pddl \
  PDDL/$domain/$problem/goal-4.pddl \
  PDDL/$domain/$problem/goal-5.pddl

problem='maze-15'
domain='navigation'

mkdir outputs/$domain/$problem -p

java -cp out:lib/*:. -Xmx11G com.suspiciousbehaviour.app.Main \
  --directed \
  --optimal \
  --obfuscating \
  --unexpected \
  --shoe_tie \
  --loitering \
  \
  --primary_goal 4 \
  --secondary_goal 1 \
  \
  --directed_search_distance 12 \
  --directed_min_goal_distance 9 \
  --directed_goal_switch_radius 3 \
  \
  --purposefulE 0.4 \
  \
  -o outputs/$domain/$problem \
  -d PDDL/$domain/domain.pddl \
  -p PDDL/$domain/$problem/core-problem.pddl \
  PDDL/$domain/$problem/goal-1.pddl \
  PDDL/$domain/$problem/goal-2.pddl \
  PDDL/$domain/$problem/goal-3.pddl \
  PDDL/$domain/$problem/goal-4.pddl \
  PDDL/$domain/$problem/goal-5.pddl

problem='bank-16'
domain='navigation'

mkdir outputs/$domain/$problem -p

java -cp out:lib/*:. -Xmx11G com.suspiciousbehaviour.app.Main \
  --directed \
  --optimal \
  --obfuscating \
  --unexpected \
  --shoe_tie \
  --loitering \
  \
  --primary_goal 4 \
  --secondary_goal 1 \
  \
  --directed_search_distance 13 \
  --directed_min_goal_distance 9 \
  --directed_goal_switch_radius 3 \
  \
  --purposefulE 0.3 \
  \
  -o outputs/$domain/$problem \
  -d PDDL/$domain/domain.pddl \
  -p PDDL/$domain/$problem/core-problem.pddl \
  PDDL/$domain/$problem/goal-1.pddl \
  PDDL/$domain/$problem/goal-2.pddl \
  PDDL/$domain/$problem/goal-3.pddl \
  PDDL/$domain/$problem/goal-4.pddl \
  PDDL/$domain/$problem/goal-5.pddl





problem='blockworld-3'
domain='blockworld'

mkdir outputs/$domain/$problem -p

java -cp out:lib/*:. -Xmx11G com.suspiciousbehaviour.app.Main \
  --directed \
  --optimal \
  --obfuscating \
  --unexpected \
  --shoe_tie \
  --loitering \
  \
  --primary_goal 4 \
  --secondary_goal 1 \
  \
  --directed_search_distance 8 \
  --directed_min_goal_distance 4 \
  --directed_goal_switch_radius 2 \
  \
  --purposefulE 0.4 \
  \
  -o outputs/$domain/$problem \
  -d PDDL/$domain/domain.pddl \
  -p PDDL/$domain/$problem/core-problem.pddl \
  PDDL/$domain/$problem/goal-1.pddl \
  PDDL/$domain/$problem/goal-2.pddl \
  PDDL/$domain/$problem/goal-3.pddl \
  PDDL/$domain/$problem/goal-4.pddl \
  PDDL/$domain/$problem/goal-5.pddl


problem='blockworld-4'
domain='blockworld'

mkdir outputs/$domain/$problem -p

java -cp out:lib/*:. -Xmx11G com.suspiciousbehaviour.app.Main \
  --directed \
  --optimal \
  --obfuscating \
  --unexpected \
  --shoe_tie \
  --loitering \
  \
  --primary_goal 4 \
  --secondary_goal 1 \
  \
  --directed_search_distance 8 \
  --directed_min_goal_distance 6 \
  --directed_goal_switch_radius 2 \
  \
  --purposefulE 0.4 \
  \
  -o outputs/$domain/$problem \
  -d PDDL/$domain/domain.pddl \
  -p PDDL/$domain/$problem/core-problem.pddl \
  PDDL/$domain/$problem/goal-1.pddl \
  PDDL/$domain/$problem/goal-2.pddl \
  PDDL/$domain/$problem/goal-3.pddl \
  PDDL/$domain/$problem/goal-4.pddl \
  PDDL/$domain/$problem/goal-5.pddl


problem='blockworld-5'
domain='blockworld'

mkdir outputs/$domain/$problem -p

java -cp out:lib/*:. -Xmx11G com.suspiciousbehaviour.app.Main \
  --directed \
  --optimal \
  --obfuscating \
  --unexpected \
  --shoe_tie \
  --loitering \
  \
  --primary_goal 4 \
  --secondary_goal 1 \
  \
  --directed_search_distance 8 \
  --directed_min_goal_distance 6 \
  --directed_goal_switch_radius 2 \
  \
  --purposefulE 0.4 \
  \
  -o outputs/$domain/$problem \
  -d PDDL/$domain/domain.pddl \
  -p PDDL/$domain/$problem/core-problem.pddl \
  PDDL/$domain/$problem/goal-1.pddl \
  PDDL/$domain/$problem/goal-2.pddl \
  PDDL/$domain/$problem/goal-3.pddl \
  PDDL/$domain/$problem/goal-4.pddl \
  PDDL/$domain/$problem/goal-5.pddl


problem='logistics-A'
domain='logistics'

mkdir outputs/$domain/$problem -p

java -cp out:lib/*:. -Xmx11G com.suspiciousbehaviour.app.Main \
  --directed \
  --optimal \
  --obfuscating \
  --unexpected \
  --shoe_tie \
  --loitering \
  \
  --primary_goal 3 \
  --secondary_goal 1 \
  \
  --directed_search_distance 8 \
  --directed_min_goal_distance 6 \
  --directed_goal_switch_radius 2 \
  \
  --purposefulE 0.4 \
  \
  -o outputs/$domain/$problem \
  -d PDDL/$domain/domain.pddl \
  -p PDDL/$domain/$problem/core-problem.pddl \
  PDDL/$domain/$problem/goal-1.pddl \
  PDDL/$domain/$problem/goal-2.pddl \
  PDDL/$domain/$problem/goal-3.pddl \
  PDDL/$domain/$problem/goal-4.pddl


problem='logistics-F'
domain='logistics'

mkdir outputs/$domain/$problem -p

java -cp out:lib/*:. -Xmx11G com.suspiciousbehaviour.app.Main \
  --directed \
  --optimal \
  --obfuscating \
  --unexpected \
  --shoe_tie \
  --loitering \
  \
  --primary_goal 3 \
  --secondary_goal 1 \
  \
  --directed_search_distance 8 \
  --directed_min_goal_distance 6 \
  --directed_goal_switch_radius 2 \
  \
  --purposefulE 0.4 \
  \
  -o outputs/$domain/$problem \
  -d PDDL/$domain/domain.pddl \
  -p PDDL/$domain/$problem/core-problem.pddl \
  PDDL/$domain/$problem/goal-1.pddl \
  PDDL/$domain/$problem/goal-2.pddl \
  PDDL/$domain/$problem/goal-3.pddl \
  PDDL/$domain/$problem/goal-4.pddl \
  PDDL/$domain/$problem/goal-5.pddl 




problem='sokoban-9'
domain='sokoban'

mkdir outputs/$domain/$problem -p

java -cp out:lib/*:. -Xmx11G com.suspiciousbehaviour.app.Main \
  --directed \
  --optimal \
  --obfuscating \
  --unexpected \
  --shoe_tie \
  --loitering \
  \
  --primary_goal 3 \
  --secondary_goal 1 \
  \
  --directed_search_distance 14 \
  --directed_min_goal_distance 12 \
  --directed_goal_switch_radius 2 \
  \
  --purposefulE 0.4 \
  \
  -o outputs/$domain/$problem \
  -d PDDL/$domain/domain.pddl \
  -p PDDL/$domain/$problem/core-problem.pddl \
  PDDL/$domain/$problem/goal-1.pddl \
  PDDL/$domain/$problem/goal-2.pddl \
  PDDL/$domain/$problem/goal-3.pddl \
  PDDL/$domain/$problem/goal-4.pddl \
  PDDL/$domain/$problem/goal-5.pddl 


problem='sokoban-10'
domain='sokoban'

mkdir outputs/$domain/$problem -p

java -cp out:lib/*:. -Xmx11G com.suspiciousbehaviour.app.Main \
  --directed \
  --optimal \
  --obfuscating \
  --unexpected \
  --shoe_tie \
  --loitering \
  \
  --primary_goal 3 \
  --secondary_goal 1 \
  \
  --directed_search_distance 14 \
  --directed_min_goal_distance 12 \
  --directed_goal_switch_radius 2 \
  \
  --purposefulE 0.4 \
  \
  -o outputs/$domain/$problem \
  -d PDDL/$domain/domain.pddl \
  -p PDDL/$domain/$problem/core-problem.pddl \
  PDDL/$domain/$problem/goal-1.pddl \
  PDDL/$domain/$problem/goal-2.pddl \
  PDDL/$domain/$problem/goal-3.pddl \
  PDDL/$domain/$problem/goal-4.pddl \
  PDDL/$domain/$problem/goal-5.pddl 

problem='sokoban-11'
domain='sokoban'

mkdir outputs/$domain/$problem -p

java -cp out:lib/*:. -Xmx11G com.suspiciousbehaviour.app.Main \
  --directed \
  --optimal \
  --obfuscating \
  --unexpected \
  --shoe_tie \
  --loitering \
  \
  --primary_goal 3 \
  --secondary_goal 1 \
  \
  --directed_search_distance 15 \
  --directed_min_goal_distance 13 \
  --directed_goal_switch_radius 2 \
  \
  --purposefulE 0.4 \
  \
  -o outputs/$domain/$problem \
  -d PDDL/$domain/domain.pddl \
  -p PDDL/$domain/$problem/core-problem.pddl \
  PDDL/$domain/$problem/goal-1.pddl \
  PDDL/$domain/$problem/goal-2.pddl \
  PDDL/$domain/$problem/goal-3.pddl \
  PDDL/$domain/$problem/goal-4.pddl \
  PDDL/$domain/$problem/goal-5.pddl 