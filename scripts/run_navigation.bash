java -cp out:lib/*:. -Xmx8G com.suspiciousbehaviour.app.Main \
  -o outputs/navigation \
  -d PDDL/navigation/domain.pddl \
  PDDL/navigation/navigation-15/problem-1.pddl \
  PDDL/navigation/navigation-15/problem-2.pddl \
  PDDL/navigation/navigation-15/problem-3.pddl \
  PDDL/navigation/navigation-15/problem-4.pddl \
  PDDL/navigation/navigation-15/problem-5.pddl
