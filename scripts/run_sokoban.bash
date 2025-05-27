java -cp out:lib/*:. -Xmx8G com.suspiciousbehaviour.app.Main \
  -a \
  -o outputs/sokoban_new -d PDDL/sokoban_new/domain.pddl \
  PDDL/sokoban_new/problem-1.pddl \
  \
  PDDL/sokoban_new/problem-3.pddl \
  PDDL/sokoban_new/problem-4.pddl \
  PDDL/sokoban_new/problem-5.pddl #PDDL/sokoban_new/problem-2.pddl \
