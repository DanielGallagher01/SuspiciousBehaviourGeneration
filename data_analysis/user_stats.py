import pandas as pd

# Load full survey CSV
df = pd.read_csv("input.csv")

# ------------------------------
# Step 1: Basic participant info
# ------------------------------
summary = df[["Prolific_ID", "EndDate", "Duration (in seconds)", "survey_version"]].copy()

# ------------------------------
# Step 2: Average Risk Taking (Q36, Q39, Q40, Q35)
# ------------------------------
risk_cols = ["Q36", "Q39", "Q40", "Q35"]
# Convert to numeric (in case strings) and calculate mean
summary["Risk Taking"] = df[risk_cols].apply(pd.to_numeric, errors="coerce").mean(axis=1)

# ------------------------------
# Step 3: Later Q2 and Q3 answers
# ------------------------------
# Multiple Q2/Q3 columns may exist (like Q2, Q2_4_TEXT etc.)
# We will take the last numeric column named starting with Q2 / Q3
q2_cols = [c for c in df.columns if c.startswith("Q2")]
q3_cols = [c for c in df.columns if c.startswith("Q3")]

# Take the last column
summary["Q2_later"] = df[q2_cols[-1]].apply(lambda x: "correct" if str(x).strip() == "3" else "incorrect")
summary["Q3_later"] = df[q3_cols[-1]].apply(lambda x: "correct" if str(x).strip() == "4" else "incorrect")

# ------------------------------
# Step 4: Save summary table
# ------------------------------
summary.to_csv("participant_summary.csv", index=False)

print("✅ Participant summary saved to participant_summary.csv")
