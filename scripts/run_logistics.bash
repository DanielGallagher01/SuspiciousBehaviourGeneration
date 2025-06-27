java -cp out:lib/*:. com.suspiciousbehaviour.app.Main \
  -a \
  -o outputs/logistics \
  -d PDDL/logistics/domain.pddl \
  PDDL/logistics/logistics-F/problem-1.pddl \
  PDDL/logistics/logistics-F/problem-2.pddl \
  PDDL/logistics/logistics-F/problem-3.pddl \
  PDDL/logistics/logistics-F/problem-4.pddl \
  PDDL/logistics/logistics-F/problem-5.pddl
