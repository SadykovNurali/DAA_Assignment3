# DAA Assignment 3 – Graphs: DSU, MST, SCC and Shortest Paths

Own graph implementation (adjacency list, directed and undirected in one class) and six algorithms: **DSU**, **Kruskal**, **Prim**, **Kosaraju**, **Kahn** and **shortest paths in a DAG**. Every algorithm counts its own operations (`dsuOps`, `edgeScans`, `comparisons`, `maxDepth`), and the benchmark checks all results against the reference values from the assignment.

## Requirements

* JDK 17 or newer
* Maven 3.8+
* Python 3 with `pandas`, `numpy`, `matplotlib` (only for the plots)

## Build

```bash
mvn clean package
```

## Run the tests (JUnit 5)

```bash
mvn clean test
```

Tests: DSU vs a naive reference, Kruskal = Prim on 100 random connected graphs (n − 1 edges), Kosaraju vs brute force (n ≤ 200), Kahn (valid order, cycle detected), DAG shortest paths vs Bellman-Ford (negative weights), stack safety (SCC on a path of 100 000 vertices), plus reading of the files in `data/`.

## Run the benchmark

The largest graph (n = 100 000, m = 2 000 000) needs memory:

```bash
# Linux / macOS
export MAVEN_OPTS=-Xmx4g
mvn compile exec:java

# Windows (cmd)
set MAVEN_OPTS=-Xmx4g
mvn compile exec:java
```

The benchmark uses `new Random(42)`, so one command reproduces every number in the report. It prints `<graph> OK` for each of the ten graphs and writes `results/results.csv` (50 rows, columns `algorithm,graph,n,m,time_ms,dsu_ops,edge_scans,comparisons,max_depth,result`). If a result differs from the reference table, Kruskal ≠ Prim, a topological order is invalid or a scan counter is wrong, the run fails with an `AssertionError`.

## Plots

```bash
pip install pandas numpy matplotlib
python results/plots.py
```

Writes three PNG files to `results/plots/` and prints the fitted slopes and the counter-to-formula ratios used in `REPORT.md`.

## Input format

```
n m directed|undirected
u v w        (m lines, vertices 0..n-1, int weights)
```

Sample graphs in `data/`: `connected.txt`, `disconnected.txt` (isolated vertex and self-loop), `cyclic.txt` (self-loop and parallel edge), `dag.txt` (negative weight).

## Project layout

```
src/main/java/daa/graph        Graph, GraphReader
src/main/java/daa/algorithms   DSU, Kruskal, Prim, Kosaraju, Kahn, DagShortestPath
src/main/java/daa/metrics      Metrics
src/main/java/daa/benchmark    GraphGenerator, Benchmark
src/test/java/daa              JUnit 5 tests
data/                          input graphs
results/                       results.csv, plots.py, plots/
REPORT.md                      complexity table, counters vs formulas, proofs, plots
```

## Design notes

* **Graph:** one class, forward-star adjacency lists (`head/next/to/weight` arrays) plus an edge list for Kruskal. Self-loops, parallel edges, isolated vertices and disconnected graphs are supported.
* **DSU:** union by size and path compression (iterative). Counts `find` calls and successful unions.
* **Kruskal / Prim:** both return a spanning forest and the number of components on a disconnected graph (no exception). Kruskal sorts with its own merge sort (counts comparisons) and stops after n − 1 edges.
* **Kosaraju / Kahn:** DFS is iterative with an explicit stack, `maxDepth` records its maximum size. Kahn reports a cycle instead of returning a wrong order.
* **DAG shortest paths:** one relaxation pass in topological order, every in-degree-0 vertex starts at distance 0 (virtual super-source), `parent[]` restores paths, negative weights allowed.

## Git workflow

Branches: `main` (working code, tag `v1.0`), `feature/dsu`, `feature/mst`, `feature/scc`, `feature/paths`, `feature/metrics`. Commit messages follow the form `feat(dsu): add path compression`, `test(mst): compare kruskal and prim weights`, `docs(report): add plots`.

## AI use acknowledgement

_Edit this section so that it is true for your own work._ I used an AI assistant (Claude, Anthropic) to help plan the project structure, to draft code for parts of the implementation and tests, to find a mismatch between my graph generator and the reference values, and to draft the plotting script and the report text. I ran and tested all code myself, checked the results against the reference table of the assignment, and I can explain every algorithm and every number in the report.