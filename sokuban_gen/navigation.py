def parse_maze(maze_str):
    maze_lines = [line for line in maze_str.strip().split("\n") if line]
    height = len(maze_lines)
    width = max(len(line) for line in maze_lines)
    grid = []
    start = None
    goals = []
    dangerous_goals = []

    for y, line in enumerate(maze_lines):
        row = []
        for x, char in enumerate(line.ljust(width)):
            if char == "#":
                row.append({"type": "wall"})
            elif char == " ":
                row.append({"type": "open"})
            elif char == "S":
                row.append({"type": "open"})
                start = (y, x)
            elif char == "G":
                row.append({"type": "open"})
                goals.append((y, x))
            elif char == "D":
                row.append({"type": "open"})
                dangerous_goals.append((y, x))
            else:
                row.append({"type": "wall"})  # Treat unknown characters as walls
        grid.append(row)
    return grid, start, goals, dangerous_goals, height, width


def generate_pddl(
    grid, start, goals, dangerous_goals, height, width, problem_name="maze-problem"
):
    def node_name(y, x):
        return f"node{y}-{x}"

    objects = []
    init = []
    places = []
    opens = []
    connections = []

    for y in range(height):
        for x in range(width):
            cell = grid[y][x]
            name = node_name(y, x)
            objects.append(name)
            places.append(f"(place {name})")
            if cell["type"] == "open":
                opens.append(f"(open {name})")
            # Add connections to adjacent open cells
            if cell["type"] == "open":
                for dy, dx in [(-1, 0), (1, 0), (0, -1), (0, 1)]:
                    ny, nx = y + dy, x + dx
                    if 0 <= ny < height and 0 <= nx < width:
                        neighbor = grid[ny][nx]
                        if neighbor["type"] == "open":
                            connections.append(f"(conn {name} {node_name(ny, nx)})")

    init.append(f"(at-robot {node_name(*start)} robot)")
    for goal in goals:
        init.append(f"(is-goal {node_name(*goal)})")
    for d_goal in dangerous_goals:
        init.append(f"(is-goal-dangerous {node_name(*d_goal)})")

    pddl = f"(define (problem {problem_name})\n"
    pddl += "  (:domain grid)\n"
    pddl += "  (:objects\n    robot\n"
    pddl += "    " + " ".join(objects) + "\n  )\n"
    pddl += "  (:init\n"
    pddl += "    " + "\n    ".join(init) + "\n"
    pddl += "    " + "\n    ".join(places) + "\n"
    pddl += "    " + "\n    ".join(opens) + "\n"
    pddl += "    " + "\n    ".join(connections) + "\n"
    pddl += "  )\n"
    pddl += f"  (:goal (and (at-robot {node_name(*goals[0])} robot)))\n"
    pddl += ")"
    return pddl



maze_input = """
#################
#               #
#  G         D  #
#               #
#               #
#               #
#               #
#               #
#               #
#               #
#               #
#       S       #
#               #
#               #
#               #
#               #
#################
"""


grid, start, goals, dangerous_goals, height, width = parse_maze(maze_input)
pddl_output = generate_pddl(grid, start, goals, dangerous_goals, height, width)
print(pddl_output)
