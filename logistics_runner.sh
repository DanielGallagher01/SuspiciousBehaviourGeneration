#!/bin/bash

# Check if directory path is provided
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <path_to_logistics_problem_directory>"
    echo "Example: $0 src/test/resources/benchmarks/pddl/logistics/p01/"
    exit 1
fi

PROBLEM_DIR="$1"

# Check if the directory exists
if [ ! -d "$PROBLEM_DIR" ]; then
    echo "Error: Directory $PROBLEM_DIR does not exist"
    exit 1
fi

# Check if necessary files exist
DOMAIN_FILE="$PROBLEM_DIR/domain.pddl"
INIT_STATE_FILE="$PROBLEM_DIR/initial_state.pddl"
GOALS_FILE="$PROBLEM_DIR/goals.txt"

for file in "$DOMAIN_FILE" "$INIT_STATE_FILE" "$GOALS_FILE"; do
    if [ ! -f "$file" ]; then
        echo "Error: Required file $file does not exist"
        exit 1
    fi
done

# Compile the Java code
echo "Compiling Java code..."
mkdir -p out
javac -d out -sourcepath src/main/java src/main/java/com/suspiciousbehaviour/app/*.java -cp classes:lib/pddl4j-4.0.0.jar

# Check if compilation was successful
if [ $? -ne 0 ]; then
    echo "Error: Compilation failed"
    exit 1
fi
echo "Compilation successful"

# Create temporary directory for problem files
TEMP_DIR=$(mktemp -d)
echo "Created temporary directory: $TEMP_DIR"

# Extract problem name from initial_state.pddl
PROBLEM_NAME=$(grep -o '(define (problem [^)]*' "$INIT_STATE_FILE" | cut -d' ' -f3)
if [ -z "$PROBLEM_NAME" ]; then
    PROBLEM_NAME="logistics-problem"
fi

# Process each goal from goals.txt
GOAL_NUM=1
PROBLEM_FILES=""

while IFS= read -r goal || [ -n "$goal" ]; do
    # Skip empty lines
    if [ -z "$(echo "$goal" | tr -d '[:space:]')" ]; then
        continue
    fi
    
    # Create temp problem file
    TEMP_PROBLEM_FILE="$TEMP_DIR/problem_${GOAL_NUM}.pddl"
    
    # Read the initial state file and replace the <GOAL> placeholder with the actual goal
    sed "s/<GOAL>/$goal/g" "$INIT_STATE_FILE" > "$TEMP_PROBLEM_FILE"
    
    PROBLEM_FILES="$PROBLEM_FILES $TEMP_PROBLEM_FILE"
    echo "Created problem file for goal $GOAL_NUM"
    
    GOAL_NUM=$((GOAL_NUM + 1))
done < "$GOALS_FILE"

if [ -z "$PROBLEM_FILES" ]; then
    echo "Error: No valid goals found in $GOALS_FILE"
    rm -rf "$TEMP_DIR"
    exit 1
fi

# Run the Java program
echo "Running Java program with domain and problem files..."
java -cp out:lib/pddl4j-4.0.0.jar com.suspiciousbehaviour.app.Main "$DOMAIN_FILE" $PROBLEM_FILES

# Clean up
echo "Cleaning up temporary files..."
rm -rf "$TEMP_DIR"
echo "Done!"

