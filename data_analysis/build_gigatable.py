import csv
import pandas as pd

# ------------------------------
# Step 1: Load main CSV
# ------------------------------
df = pd.read_csv("input.csv")

# Columns with repeated | values
multi_cols = [
    "__js_Instance_domain",
    "__js_Instance_watch",
    "__js_Instance_behaviour",
    "__js_Instance_map",
    "__js_Instance_stop",
    "__js_Instance_safe",
    "__js_Instance_userinput_watch",
    "__js_Instance_userinput_stop",  
    "__js_Instance_userinput_notwatch",
    "__js_Instance_userinput_notstop",
    "__js_Instance_userinput_safe"
]

# Columns to KEEP
keep_cols = ["Q_User_ID"] + multi_cols

# ------------------------------
# Step 2: split into lists
# ------------------------------
for col in multi_cols:
    df[col] = df[col].fillna("").apply(
        lambda x: x[1:].split("|") if isinstance(x, str) else [""]
    )


# ------------------------------
# Step 3: explode rows
# ------------------------------
rows = []
for _, row in df.iterrows():
    max_len = max(len(row[col]) for col in multi_cols)
    for i in range(max_len):
        new_row = row.copy()
        for col in multi_cols:
            values = row[col]
            new_row[col] = values[i] if i < len(values) else ""
        new_row["Order of showing"] = i + 1
        rows.append(new_row)

df_long = pd.DataFrame(rows)

# ------------------------------
# Step 4: keep only wanted columns
# ------------------------------
df_long = df_long[keep_cols + ["Order of showing"]]

# ------------------------------
# Step 5: Add G1–G7 columns
# ------------------------------
mapping = {
    "navigation": [1, 1, 1, 0, 0],
    "sokoban":    [1, 1, 1, 0, 0],
    "logistics":  [1, 1, 1, 1, 1, 0, 0]
}
for j in range(1, 8):
    df_long[f"G{j}-Good"] = ""
for domain, values in mapping.items():
    mask = df_long["__js_Instance_domain"] == domain
    for j, val in enumerate(values, start=1):
        df_long.loc[mask, f"G{j}-Good"] = val

# ------------------------------
# Step 6: Clean watch/stop and add yes/no columns
# ------------------------------
def yes_no(val):
    return "yes" if str(val).isdigit() else "no"

df_long["__js_Instance_watch"] = df_long["__js_Instance_watch"].replace("null", "")
df_long["__js_Instance_stop"]  = df_long["__js_Instance_stop"].replace("null", "")
df_long["__js_Instance_safe"]  = df_long["__js_Instance_safe"].replace("null", "")

df_long["Watch - yes/no"] = df_long["__js_Instance_watch"].apply(
    lambda v: yes_no(v) if v != "" else "no"
)
df_long["Stop - yes/no"]  = df_long["__js_Instance_stop"].apply(
    lambda v: yes_no(v) if v != "" else "no"
)

df_long["Safe - yes/no"]  = df_long["__js_Instance_safe"].apply(
    lambda v: yes_no(v) if v != "" else "no"
)

# ------------------------------
# Step 7: Load ragged state times table
# ------------------------------
state_dict = {}
with open("state_times.csv", newline='') as f:
    reader = csv.reader(f)
    for row in reader:
        if len(row) < 3:
            continue
        map_name = row[0].strip()
        behaviour = row[1].strip()
        times = [float(t) for t in row[2:] if str(t).strip() != ""]
        state_dict[(map_name, behaviour)] = times

# ------------------------------
# Step 8: Calculate number of states passed
# ------------------------------
def get_number_steps(time_value, map_name, behaviour):
    if time_value == "" or pd.isna(time_value):
        return ""
    try:
        time_sec = float(time_value) / 100
    except ValueError:
        return ""
    key = (str(map_name).strip(), str(behaviour).strip())
    if key not in state_dict:
        return ""
    state_times = state_dict[key]
    steps = sum(1 for t in state_times if t <= time_sec)
    return steps-1

df_long["Watch number steps"] = df_long.apply(
    lambda row: get_number_steps(row["__js_Instance_watch"], row["__js_Instance_map"], row["__js_Instance_behaviour"]),
    axis=1
)
df_long["Stop number steps"] = df_long.apply(
    lambda row: get_number_steps(row["__js_Instance_stop"], row["__js_Instance_map"], row["__js_Instance_behaviour"]),
    axis=1
)
df_long["Safe number steps"] = df_long.apply(
    lambda row: get_number_steps(row["__js_Instance_safe"], row["__js_Instance_map"], row["__js_Instance_behaviour"]),
    axis=1
)

# ------------------------------
# Step 9: Merge with problem_behaviour_stats
# ------------------------------
stats = pd.read_csv("problem_behaviour_stats.csv")
df_long["__js_Instance_map"] = df_long["__js_Instance_map"].astype(str).str.strip()
df_long["__js_Instance_behaviour"] = df_long["__js_Instance_behaviour"].astype(str).str.strip().str.lower()
stats["problem"] = stats["problem"].astype(str).str.strip()
stats["behaviour"] = stats["behaviour"].astype(str).str.strip().str.lower()

merged = df_long.merge(
    stats,
    how="left",
    left_on=["__js_Instance_map", "__js_Instance_behaviour"],
    right_on=["problem", "behaviour"]
)

# ------------------------------
# Step 10: Cap steps at total_number_of_actions
# ------------------------------
# def cap_steps(step, total):
#     if step == "" or pd.isna(step) or pd.isna(total):
#         return ""
#     return min(int(step), int(total))

# merged["Watch number steps"] = merged.apply(
#     lambda row: cap_steps(row["Watch number steps"], row["total_number_of_actions"]), axis=1
# )
# merged["Stop number steps"] = merged.apply(
#     lambda row: cap_steps(row["Stop number steps"], row["total_number_of_actions"]), axis=1
# )

# ------------------------------
# Step 11: Difference columns
# ------------------------------
merged["Stop - Watch (time)"] = merged.apply(
    lambda row: float(row["__js_Instance_stop"])/100 - float(row["__js_Instance_watch"])/100
    if str(row["__js_Instance_stop"]).isdigit() and str(row["__js_Instance_watch"]).isdigit() else "",
    axis=1
)
merged["Stop - Watch (actions)"] = merged.apply(
    lambda row: int(row["Stop number steps"])-int(row["Watch number steps"])
    if str(row["Stop number steps"]).isdigit() and str(row["Watch number steps"]).isdigit() else "",
    axis=1
)

# Behaviour start time
def behaviour_start_to_time(start_step, state_times_key):
    if pd.isna(start_step) or start_step == "":
        return ""
    if state_times_key not in state_dict:
        return ""
    state_times = state_dict[state_times_key]
    idx = int(start_step) - 1
    if idx < 0 or idx >= len(state_times):
        return ""
    return state_times[idx]

merged["behaviour_start_time"] = merged.apply(
    lambda row: behaviour_start_to_time(
        row["behaviour_start"],
        (str(row["__js_Instance_map"]).strip(), str(row["__js_Instance_behaviour"]).strip())
    ),
    axis=1
)

# ------------------------------
# Step 12: Merge distances
# ------------------------------
distances = pd.read_csv("distances.csv")
distances["problem"] = distances["problem"].astype(str).str.strip()
distances["behaviour"] = distances["behaviour"].astype(str).str.strip().str.lower()

# Watch distances
watch_distances = distances.rename(columns={"state":"Watch number steps"})
merged = merged.merge(
    watch_distances,
    how="left",
    left_on=["__js_Instance_map", "__js_Instance_behaviour", "Watch number steps"],
    right_on=["problem", "behaviour", "Watch number steps"],
    suffixes=("", "_watch_dist")
)

# Stop distances
stop_distances = distances.rename(columns={"state":"Stop number steps"})
merged = merged.merge(
    stop_distances,
    how="left",
    left_on=["__js_Instance_map", "__js_Instance_behaviour", "Stop number steps"],
    right_on=["problem", "behaviour", "Stop number steps"],
    suffixes=("", "_stop_dist")
)

# Safe distances
safe_distances = distances.rename(columns={"state":"Safe number steps"})
merged = merged.merge(
    safe_distances,
    how="left",
    left_on=["__js_Instance_map", "__js_Instance_behaviour", "Safe number steps"],
    right_on=["problem", "behaviour", "Safe number steps"],
    suffixes=("", "_safe_dist")
)

# Drop redundant columns from distances merges
merged.drop(columns=[
    "problem_watch_dist", "behaviour_watch_dist", "target_goal_watch_dist",
    "problem_stop_dist", "behaviour_stop_dist", "target_goal_stop_dist",
    "problem_safe_dist", "behaviour_safe_dist", "target_goal_safe_dist"
], inplace=True, errors="ignore")

# ------------------------------
# Step 13: Save final file
# ------------------------------
merged.to_csv("output.csv", index=False)

print("✅ Final dataset saved to output.csv with capped steps and separate watch/stop distances")
