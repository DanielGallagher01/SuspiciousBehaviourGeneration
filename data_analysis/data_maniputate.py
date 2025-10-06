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

# Columns to KEEP
keep_cols = ["Prolific_ID"] + multi_cols

# Step 1: split into lists
for col in multi_cols:
    df[col] = df[col].fillna("").apply(lambda x: x.strip("|").split("|") if isinstance(x, str) else [""])

# Step 2: explode row by row, while tracking order
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

# Step 3: filter to only wanted columns
df_long = df_long[keep_cols + ["Order of showing"]]

# Step 4: Add G1–G7 columns
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

# Step 5: Clean watch/stop and add yes/no columns
def yes_no(val):
    return "yes" if str(val).isdigit() else "no"

df_long["__js_Instance_watch"] = df_long["__js_Instance_watch"].replace("null", "")
df_long["__js_Instance_stop"]  = df_long["__js_Instance_stop"].replace("null", "")

df_long["Watch - yes/no"] = df_long["__js_Instance_watch"].apply(lambda v: yes_no(v) if v != "" else "no")
df_long["Stop - yes/no"]  = df_long["__js_Instance_stop"].apply(lambda v: yes_no(v) if v != "" else "no")

# Save
df_long.to_csv("output.csv", index=False)

print("✅ Added Order of showing column and saved to output_with_order.csv")
