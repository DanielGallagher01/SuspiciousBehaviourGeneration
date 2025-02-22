out=$(javac -d out -sourcepath src/main/java src/main/java/com/suspiciousbehaviour/app/Main.java -cp classes:lib/pddl4j-4.0.0.jar 2>&1)

if echo "$out" | grep -q "error"; then
    echo "Compilation failed:"
    echo "$out"
    exit 1
fi

java -cp out:lib/pddl4j-4.0.0.jar com.suspiciousbehaviour.app.Main \
	src/test/resources/benchmarks/pddl/ipc2000/logistics/strips-typed/domain.pddl \
	src/test/resources/benchmarks/pddl/ipc2000/logistics/strips-typed/p01.pddl

