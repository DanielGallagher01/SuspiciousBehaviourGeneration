out=$(javac -d out -sourcepath src/main/java src/main/java/com/suspiciousbehaviour/app/*.java -cp classes:lib/pddl4j-4.0.0.jar 2>&1)

if echo "$out" | grep -q "error"; then
    echo "Compilation failed:"
    echo "$out"
    exit 1
fi

java -cp out:lib/pddl4j-4.0.0.jar com.suspiciousbehaviour.app.Main \
	src/test/resources/benchmarks/pddl/blocks/domain.pddl \
	src/test/resources/benchmarks/pddl/blocks/p001-a1.pddl \
	src/test/resources/benchmarks/pddl/blocks/p001-a2.pddl \
	src/test/resources/benchmarks/pddl/blocks/p001-a3.pddl 

