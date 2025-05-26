out=$(javac -d out -sourcepath src/main/java src/main/java/com/suspiciousbehaviour/app/*.java \
  -cp lib/*:. 2>&1)

if echo "$out" | grep -q "error"; then
  echo "Compilation failed:"
  echo "$out"
  exit 1
fi
