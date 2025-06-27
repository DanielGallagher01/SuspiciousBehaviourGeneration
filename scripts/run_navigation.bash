java -cp out:lib/*:. -Xmx10G com.suspiciousbehaviour.app.Main \
  -o outputs/navigation \
  -d PDDL/navigation/domain.pddl \
  PDDL/navigation/bank-16/problem-1.pddl \
  PDDL/navigation/bank-16/problem-2.pddl \
  PDDL/navigation/bank-16/problem-3.pddl \
  PDDL/navigation/bank-16/problem-4.pddl \
  PDDL/navigation/bank-16/problem-5.pddl
