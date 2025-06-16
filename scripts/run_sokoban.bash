java -cp out:lib/*:. -Xmx8G com.suspiciousbehaviour.app.Main \
  -o outputs/sokoban_new -d PDDL/sokoban/domain.pddl \
  PDDL/sokoban/sokoban-11/problem-1.pddl \
  PDDL/sokoban/sokoban-11/problem-2.pddl \
  PDDL/sokoban/sokoban-11/problem-3.pddl \
  PDDL/sokoban/sokoban-11/problem-4.pddl \
  PDDL/sokoban/sokoban-11/problem-5.pddl
