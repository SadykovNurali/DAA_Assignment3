import os
import numpy as np
import pandas as pd
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
from matplotlib.ticker import LogLocator, LogFormatterMathtext, NullFormatter

os.makedirs("results/plots", exist_ok=True)
df = pd.read_csv("results/results.csv")
df["dens"] = np.where(df.m / df.n < 10, "sparse (m=4n)", "dense (m=20n)")
df["ops"] = df.edge_scans + df.dsu_ops + df.comparisons

STYLES = ["o-", "s--", "^-.", "d:"]


def powers_of_ten(axis):
    axis.set_major_locator(LogLocator(base=10, numticks=20))
    axis.set_major_formatter(LogFormatterMathtext(base=10))
    axis.set_minor_formatter(NullFormatter())


def log_axes(ax, xlim=(10**2.9, 10**5.1), ylim=None):
    ax.set_xscale("log")
    ax.set_yscale("log")
    ax.set_xlim(*xlim)
    if ylim:
        ax.set_ylim(*ylim)
    powers_of_ten(ax.xaxis)
    powers_of_ten(ax.yaxis)
    ax.grid(True, which="major", alpha=0.5)
    ax.grid(True, which="minor", alpha=0.15)


def draw(ax, value):
    for i, ((a, d), g) in enumerate(df.groupby(["algorithm", "dens"])):
        g = g.sort_values("n")
        ax.plot(g.n, value(g), STYLES[i % 4], markersize=6 + (i % 2) * 3, label=f"{a}, {d}")


# 1. Operations vs n
fig, ax = plt.subplots(figsize=(9, 6))
draw(ax, lambda g: g.ops)
log_axes(ax)
ax.set_xlabel("n (vertices)")
ax.set_ylabel("operations (edgeScans + dsuOps + comparisons)")
ax.set_title("Operations vs n")
ax.legend(fontsize=7, bbox_to_anchor=(1.02, 1), loc="upper left")
fig.tight_layout()
fig.savefig("results/plots/ops_vs_n.png", dpi=150)

# 2. Scans per edge  (y: 10^-1 ... 10^1)
fig, ax = plt.subplots(figsize=(9, 6))
draw(ax, lambda g: g.edge_scans / g.m)
log_axes(ax, ylim=(10**-1, 10**1))
ax.set_xlabel("n (vertices)")
ax.set_ylabel("edgeScans / m")
ax.set_title("Scans per edge")
ax.legend(fontsize=7, bbox_to_anchor=(1.02, 1), loc="upper left")
fig.tight_layout()
fig.savefig("results/plots/scans_per_edge.png", dpi=150)

# 3. Share of edges Kruskal examines  (y: 10^-1 ... 10^0)
fig, ax = plt.subplots(figsize=(7, 5))
for d, g in df[df.algorithm == "Kruskal"].groupby("dens"):
    g = g.sort_values("n")
    ax.plot(g.n, g.edge_scans / g.m, marker="o", label=d)
log_axes(ax, ylim=(10**-1, 10**0 * 1.2))
ax.set_xlabel("n (vertices)")
ax.set_ylabel("Kruskal edgeScans / m")
ax.set_title("Share of edges Kruskal examines")
ax.legend()
fig.tight_layout()
fig.savefig("results/plots/kruskal_share.png", dpi=150)

# Numbers for REPORT.md
print("Slope of log(edgeScans) vs log(n):")
for (a, d), g in df.groupby(["algorithm", "dens"]):
    s = np.polyfit(np.log(g.n), np.log(g.edge_scans), 1)[0]
    print(f"  {a:16s} {d:14s} {s:.3f}")

print("\nSlope of log(ops) vs log(n), Kruskal:")
for d, g in df[df.algorithm == "Kruskal"].groupby("dens"):
    print(f"  {d:14s} {np.polyfit(np.log(g.n), np.log(g.ops), 1)[0]:.3f}")

print("\nCounter / formula:")
for (a, d), g in df.groupby(["algorithm", "dens"]):
    g = g.sort_values("n")
    if a == "Kruskal":
        r, name = g.dsu_ops / g.m, "dsuOps / E"
    elif a in ("Kosaraju", "Kahn", "DagShortestPath"):
        r, name = g.edge_scans / (g.n + g.m), "edgeScans / (V+E)"
    else:
        r, name = g.edge_scans / g.m, "edgeScans / E"
    print(f"  {a:16s} {d:14s} {name:18s}", [round(x, 3) for x in r])