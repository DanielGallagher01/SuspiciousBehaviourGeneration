import csv
import pandas as pd

# ------------------------------
# Step 1: Load CSV
# ------------------------------
df = pd.read_csv("input.csv")

# Columns with repeated | values
multi_cols = [
    "__js_Instance_domain",
    "__js_Instance_watch",
    "__js_Instance_behaviour",
    "__js_Instance_map",
    "__js_Instance_stop",
    "__js_Instance_userinput_watch",
    "__js_Instance_userinput_stop"
]

# Columns to KEEP
keep_cols = ["Prolific_ID"] + multi_cols

# ------------------------------
# Step 2: split into lists
# ------------------------------
for col in multi_cols:
    df[col] = df[col].fillna("").apply(
        lambda x: x.strip("|").split("|") if isinstance(x, str) else [""]
    )

# ------------------------------
# Step 3: explode row by row, while tracking order
# ------------------------------
rows = []
for _, row in df.iterrows():
    max_len = max(len(row[col]) for col in multi_cols)
    for i in range(max_len):
        new_row = row.copy()
        for col in multi_cols:
            values = row[col]
            new_row[col] = values[i] if i < len(values) else ""
        # order is 1-based
        new_row["Order of showing"] = i + 1
        rows.append(new_row)

df_long = pd.DataFrame(rows)

# ------------------------------
# Step 4: filter to only wanted columns
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

df_long["Watch - yes/no"] = df_long["__js_Instance_watch"].apply(
    lambda v: yes_no(v) if v != "" else "no"
)
df_long["Stop - yes/no"]  = df_long["__js_Instance_stop"].apply(
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
# Step 8: Function to calculate number of states passed
# ------------------------------
def get_number_steps(time_value, map_name, behaviour):
    if time_value == "" or pd.isna(time_value):
        return ""
    try:
        time_sec = float(time_value) / 100 * 0.8  # adjust for 80% speed
    except ValueError:
        return ""
    key = (str(map_name).strip(), str(behaviour).strip())
    if key not in state_dict:
        return ""
    state_times = state_dict[key]
    steps = sum(1 for t in state_times if t <= time_sec)
    return steps

# ------------------------------
# Step 9: Apply Watch/Stop step counts
# ------------------------------
df_long["Watch number steps"] = df_long.apply(
    lambda row: get_number_steps(row["__js_Instance_watch"], row["__js_Instance_map"], row["__js_Instance_behaviour"]),
    axis=1
)

df_long["Stop number steps"] = df_long.apply(
    lambda row: get_number_steps(row["__js_Instance_stop"], row["__js_Instance_map"], row["__js_Instance_behaviour"]),
    axis=1
)

# ------------------------------
# Step 10: Merge with problem_behaviour_stats
# ------------------------------
stats = pd.read_csv("problem_behaviour_stats.csv")

# Normalize keys for joining
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

# Function to calculate actions until end
def actions_until_end(steps, total):
    if steps == "" or pd.isna(steps) or pd.isna(total):
        return ""
    try:
        return int(total) - int(steps)
    except Exception:
        return ""

# Add new columns
merged["Watch actions until end"] = merged.apply(
    lambda row: actions_until_end(row["Watch number steps"], row["total_number_of_actions"]),
    axis=1
)

merged["Stop actions until end"] = merged.apply(
    lambda row: actions_until_end(row["Stop number steps"], row["total_number_of_actions"]),
    axis=1
)

# ------------------------------
# Step 11: Save final file
# ------------------------------
merged.to_csv("output.csv", index=False)

print("✅ Final dataset with stats saved to output.csv")
