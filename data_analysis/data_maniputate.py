import pandas as pd

# Load CSV
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

# Columns to KEEP in final output
keep_cols = ["Prolific_ID"] + multi_cols  # add others you want to preserve

# Step 1: split
for col in multi_cols:
    df[col] = df[col].fillna("").apply(lambda x: x.strip("|").split("|") if isinstance(x, str) else [""])

# Step 2: explode row by row
rows = []
for _, row in df.iterrows():
    max_len = max(len(row[col]) for col in multi_cols)
    for i in range(max_len):
        new_row = row.copy()
        for col in multi_cols:
            values = row[col]
            new_row[col] = values[i] if i < len(values) else ""
        rows.append(new_row)

df_long = pd.DataFrame(rows)

# Step 3: filter to only wanted columns
df_long = df_long[keep_cols]

# Save
df_long.to_csv("output_filtered.csv", index=False)

print("✅ Filtered + pivoted data saved to output_filtered.csv")
