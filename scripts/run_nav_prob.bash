java -cp out:lib/*:. -Xmx11G com.suspiciousbehaviour.app.Main \
  -o outputs/navigation \
  -d PDDL/navigation/domain.pddl \
  PDDL/navigation/bank-16/core-problem.pddl \
  PDDL/navigation/bank-16/goal-1.pddl \
  PDDL/navigation/bank-16/goal-2.pddl
