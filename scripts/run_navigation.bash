java -cp out:lib/*:. -Xmx10G com.suspiciousbehaviour.app.Main \
  --directed \
  --primary_goal 4 \
  --obfuscating \
  -o outputs/navigation \
  -d PDDL/navigation/domain.pddl \
  -p PDDL/navigation/maze-15/core-problem.pddl \
  PDDL/navigation/maze-15/goal-1.pddl \
  PDDL/navigation/maze-15/goal-2.pddl \
  PDDL/navigation/maze-15/goal-3.pddl \
  PDDL/navigation/maze-15/goal-4.pddl \
  PDDL/navigation/maze-15/goal-5.pddl
