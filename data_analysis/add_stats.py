import pandas as pd

# Load main table with steps
df = pd.read_csv("output_final.csv")

# Load behaviour stats
stats = pd.read_csv("problem_behaviour_stats.csv")

# --- Normalize keys for joining ---
df["__js_Instance_map"] = df["__js_Instance_map"].astype(str).str.strip()
df["__js_Instance_behaviour"] = df["__js_Instance_behaviour"].astype(str).str.strip().str.lower()

stats["problem"] = stats["problem"].astype(str).str.strip()
stats["behaviour"] = stats["behaviour"].astype(str).str.strip().str.lower()

# Merge on (map, behaviour)
merged = df.merge(
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

# Save final result
merged.to_csv("output_with_stats.csv", index=False)

print("✅ Merged with problem_behaviour_stats and added 'actions until end' columns -> output_with_stats.csv")
