java -cp out:lib/*:. com.suspiciousbehaviour.app.Main \
  -a \
  -o outputs/logistics \
  -d PDDL/logistics/domain.pddl \
  PDDL/logistics/problem-1.pddl \
  PDDL/logistics/problem-2.pddl \
  PDDL/logistics/problem-3.pddl \
  PDDL/logistics/problem-4.pddl \
  PDDL/logistics/problem-5.pddl
