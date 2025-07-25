def sokoban_to_pddl(map_str, problem_name="p146-microban-sequential"):
    lines = [line for line in map_str.strip().split("\n")]
    height, width = len(lines), max(len(line) for line in lines)

    walls = set()
    goals = set()
    stones = []
    player_pos = None

    def pos_name(r, c):
        return f"pos-{r + 1}-{c + 1}"

    locations = []
    for r, line in enumerate(lines):
        for c, ch in enumerate(line.ljust(width)):
            locations.append((r, c))
            if ch == "#":
                walls.add((r, c))
            elif ch == ".":
                goals.add((r, c))
            elif ch == "$":
                stones.append((r, c))
            elif ch == "@":
                player_pos = (r, c)
            elif ch == "*":  # stone on goal
                stones.append((r, c))
                goals.add((r, c))
            elif ch == "+":  # player on goal
                player_pos = (r, c)
                goals.add((r, c))
            elif ch == "?":  # alternate for block
                stones.append((r, c))

    location_names = {pos: pos_name(*pos) for pos in locations}
    output = []

    output.append(f"(define (problem {problem_name})")
    output.append("  (:domain sokoban-sequential)")
    output.append("  (:objects")
    output.append("    dir-down - direction")
    output.append("    dir-left - direction")
    output.append("    dir-right - direction")
    output.append("    dir-up - direction")
    output.append("    player-01 - player")

    for pos in sorted(locations):
        output.append(f"    {location_names[pos]} - location")
    for i in range(len(stones)):
        output.append(f"    stone-{i + 1:02d} - stone")
    output.append("  )")

    output.append("  (:init")
    for goal in goals:
        output.append(f"    (IS-GOAL {location_names[goal]})")
    for pos in locations:
        if pos not in goals:
            output.append(f"    (IS-NONGOAL {location_names[pos]})")

    for r, c in locations:
        for dr, dc, dname in [
            (-1, 0, "dir-up"),
            (1, 0, "dir-down"),
            (0, -1, "dir-left"),
            (0, 1, "dir-right"),
        ]:
            nr, nc = r + dr, c + dc
            if (nr, nc) in locations:
                output.append(
                    f"    (MOVE-DIR {location_names[(r, c)]} {location_names[(nr, nc)]} {dname})"
                )

    output.append(f"    (at player-01 {location_names[player_pos]})")

    for i, pos in enumerate(stones):
        output.append(f"    (at stone-{i + 1:02d} {location_names[pos]})")

    for pos in locations:
        if pos not in stones and pos != player_pos and pos not in walls:
            output.append(f"    (clear {location_names[pos]})")

    output.append("  )")

    output.append("  (:goal (and")
    for i in range(len(stones)):
        output.append(f"    (at-goal stone-{i + 1:02d})")
    output.append("  ))")

    output.append(")")

    return "\n".join(output)


# sokoban_input = """
# #@########
# #  #     #
# #        #
# #  ### # #
# # $ #. #.#
# #   #   ##
# ###      #
# #.       #
# #  #      
# ##########
# """

sokoban_input = """
##########
#.#.  #   
#@       #
# ##  #  #
# ### $  #
#       ##
# ##     #
#     ####
#       .#
# ########
"""

pddl_output = sokoban_to_pddl(sokoban_input)
print(pddl_output)
