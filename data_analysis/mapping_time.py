import csv
import pandas as pd

# Load the main exploded table
df = pd.read_csv("output.csv")

# Load the ragged state times table
state_dict = {}
with open("state_times.csv", newline='') as f:
    reader = csv.reader(f)
    for row in reader:
        if len(row) < 3:
            continue  # skip rows with not enough data
        map_name = row[0].strip()
        behaviour = row[1].strip()
        times = [float(t) for t in row[2:] if str(t).strip() != ""]
        state_dict[(map_name, behaviour)] = times

# Function to calculate number of states passed
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

# Apply to Watch
df["Watch number steps"] = df.apply(
    lambda row: get_number_steps(row["__js_Instance_watch"], row["__js_Instance_map"], row["__js_Instance_behaviour"]),
    axis=1
)

# Apply to Stop
df["Stop number steps"] = df.apply(
    lambda row: get_number_steps(row["__js_Instance_stop"], row["__js_Instance_map"], row["__js_Instance_behaviour"]),
    axis=1
)

# Save
df.to_csv("output_with_watch_stop_steps.csv", index=False)

print("✅ Added 'Watch number steps' and 'Stop number steps' columns and saved to output_with_watch_stop_steps.csv")
