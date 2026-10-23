# Assignment 3 – Graphs: DSU, MST, SCC and Shortest Paths

Design and Analysis of Algorithms · Student: _ Nurali Sadykov, GROUP SE-2509

All numbers below come from `results/results.csv` (seed 42, produced by `mvn compile exec:java`). Counters are incremented **inside** the algorithms, so every number is reproducible on any machine. Operation count used in plot 1: `ops = edgeScans + dsuOps + comparisons`.

The benchmark reproduces every reference value of the assignment exactly (MST weights, SCC counts, DAG distance sums), checks Kruskal = Prim, validates every topological order, and requires `edgeScans = 2m` for Kosaraju and `m` for Kahn and the DAG pass. A mismatch aborts the run.

## 1. Complexity table

| Algorithm | Time | Extra space | Justification |
|---|---|---|---|
| DSU `find` / `union` | O(α(n)) amortized | O(n) | union by size + path compression; α(n) ≤ 4 for any practical n |
| Kruskal | O(E log E) | O(E + V) | sorting E edges dominates; E·α(V) for the DSU part; index arrays + DSU |
| Prim (lazy, binary heap) | O(E log E) = O(E log V) | O(E + V) | every arc may be pushed once (up to 2E entries), each push/pop O(log E) |
| Kosaraju | O(V + E) | O(V + E) | two iterative DFS passes, 2E arc scans; the reversed graph costs O(V + E) memory |
| Kahn | O(V + E) | O(V) | in-degree array + queue; every arc is scanned once |
| DAG shortest path | O(V + E) | O(V) | one topological order, then each arc relaxed once; `dist[]`, `parent[]` |

## 2. Counters vs formulas

A ratio that stays constant while n grows by a factor of 100 is the empirical confirmation of the complexity class. Sizes: n = 1 000 … 100 000 (steps of √10).

| Counter / formula | sparse (m = 4n) | dense (m = 20n) |
|---|---|---|
| Kosaraju `edgeScans / (V+E)` | 1.600, 1.600, 1.600, 1.600, 1.600 | 1.905 (all five sizes) |
| Kosaraju `edgeScans / m` | 2.000 (exactly) | 2.000 (exactly) |
| Kahn `edgeScans / (V+E)` | 0.800 (all five sizes) | 0.952 (all five sizes) |
| DAG shortest path `edgeScans / (V+E)` | 0.800 (all five sizes) | 0.952 (all five sizes) |
| Prim `edgeScans / (2E)` | 1.000 (exactly) | 1.000 (exactly) |
| Prim `comparisons / (E log₂E)` | 1.83, 1.87, 1.90, 1.93, 1.95 | 1.83, 1.85, 1.87, 1.88, 1.89 |
| Kruskal `comparisons / (E log₂E)` | 0.89, 0.91, 0.92, 0.93, 0.93 | 0.91, 0.92, 0.93, 0.94, 0.94 |
| Kruskal `dsuOps / (2·edgeScans + V − 1)` | 1.000 (exactly) | 1.000 (exactly) |
| Kruskal `dsuOps / E` | 1.79, 2.02, 2.18, 2.23, 2.25 | 0.48, 0.51, 0.47, 0.65, 0.79 |

Notes:

* The traversal ratios are **exactly** constant: each arc is scanned a fixed number of times, independent of n.
* Kruskal's `dsuOps / E` is **not** constant, and that is expected, not a bug. Kruskal stops as soon as n − 1 edges are in the tree, so it scans only a share of the edges (see section 6) and that share changes with n. The DSU work itself is exactly `2 × edgeScans` finds plus `n − 1` successful unions, which gives the exact ratio 1.000 in the row above, i.e. DSU work is linear in the number of edges actually examined.
* The sorting term of Kruskal and the heap term of Prim follow `E log E` with a slowly drifting constant (the sort of `E` items needs about `E log₂E − 1.25E` comparisons, so the ratio approaches 1 from below as E grows).

DFS stack depth (`maxDepth`, Kosaraju): sparse 623, 1 869, 5 965, 18 805, 59 773; dense 934, 2 913, 9 243, 29 061, 91 750. At n = 100 000 the stack reaches 59 773 and 91 750 frames, which is why a recursive DFS would overflow the JVM stack and the explicit stack is required.

## 3. Slope check (log-log, fitted on the five sizes)

| Curve | sparse | dense |
|---|---|---|
| Kosaraju, Kahn, DAG, Prim: slope of `edgeScans` | 1.000 | 1.000 |
| Kruskal: slope of `edgeScans` | 1.055 | 1.119 |
| Kruskal: slope of `ops` (scans + DSU + comparisons) | 1.095 | 1.090 |

The traversals give slope 1.00: linear work. Kruskal is steeper than 1. Its total work `ops` has slope about 1.09, which is the log factor of `E log E` (over two decades, a factor `log E` grows by roughly 1.2–1.3×, which corresponds to a slope contribution of about 0.1). The slope of Kruskal's `edgeScans` alone (1.06 / 1.12) is higher still because the examined share of the edges itself grows with n.

## 4. Correctness arguments

**Cut property.** Let G be a connected weighted graph, let S be any proper subset of vertices, and let e be a lightest edge crossing the cut (S, V∖S). Claim: some minimum spanning tree contains e. Take any MST T. If e ∉ T, then T + e contains a cycle, and that cycle must cross the cut a second time through another edge f ≠ e. Since e is lightest across the cut, w(e) ≤ w(f), so T − f + e is again a spanning tree with weight ≤ w(T), hence also minimal, and it contains e. Kruskal uses the property like this: when it examines the lightest remaining edge (u, v) with find(u) ≠ find(v), take S = the component of u in the current forest. No forest edge crosses this cut, and every remaining lighter edge has been rejected as an internal edge, so (u, v) is lightest across the cut and safe. Prim uses it directly: the cut is (tree, rest), and the heap always returns the lightest edge crossing it. Both algorithms add only safe edges n − 1 times, so both end with an MST; the trees may differ when weights tie, but the total weight is the same, which the benchmark checks automatically.

**Induction on the topological order (DAG shortest paths).** Let v₁, …, vₙ be a topological order, so every edge goes from an earlier to a later vertex. Claim: when the pass reaches vᵢ, `dist[vᵢ]` is final. Base: v₁ has no incoming edges, so it is a source with `dist = 0` (the virtual super-source gives distance 0 to every in-degree-0 vertex). Step: suppose `dist[v₁..v_{i-1}]` are final. Every incoming edge (u, vᵢ) has u among v₁..v_{i-1}, and all of those edges have already been relaxed when their tail was processed. The shortest path to vᵢ ends with some such edge (u, vᵢ) and its prefix is a shortest path to u; by the induction hypothesis `dist[u]` is exact, so the relaxation of (u, vᵢ) produced the exact optimum. Therefore one pass, O(V + E), is enough. Negative weights are safe because the argument never uses non-negativity: each vertex is finalized once and never revisited. In a general graph a cycle can bring the path back to an already processed vertex, and a negative cycle makes distances unbounded; this is why Dijkstra needs non-negative weights and Bellman-Ford needs V − 1 passes.

## 5. Which algorithm when

| Situation | Choice | Why |
|---|---|---|
| MST, edges already in an **edge list**, sparse graph | Kruskal | sorting E edges is cheap, DSU needs no adjacency structure, stops after n − 1 tree edges |
| MST, graph stored as **adjacency list**, dense graph | Prim | no global sort, grows from one vertex; with a decrease-key (indexed) heap it costs O(E + V log V) and wins on dense graphs; our lazy version pays O(E log E) |
| MST of a **disconnected** graph | either, restarted per component | both return a spanning forest and the component count, no exception |
| Edges arrive as a **stream** / changing connectivity | DSU (Kruskal style) | incremental connectivity in almost constant time per edge |
| Cycles **possible**, need groups of mutually reachable nodes (deadlocks) | Kosaraju (or Tarjan) | O(V + E), iterative DFS safe at 100 000 vertices |
| Need an execution order, cycle possible → must detect it | Kahn | returns fewer than n vertices exactly when a cycle exists, no wrong order |
| Graph is a **DAG**, shortest/longest path, negative weights | topological order + one relaxation pass | O(V + E), handles negative weights, simpler and faster than Dijkstra or Bellman-Ford |
| Graph with cycles and negative weights | Bellman-Ford (not implemented here) | the one-pass argument no longer holds |

Counter evidence from our runs: on dense graphs Kruskal examines only 21–37 % of the edges, but its sort still needs about 0.9·E·log₂E comparisons; the lazy Prim here needs about 1.9·E·log₂E heap comparisons and always scans 2E arcs. With these counters Kruskal does less work for both densities. Prim's advantage appears only with a better heap (decrease-key) on dense graphs.

## 6. Plots

1. `results/plots/ops_vs_n.png` – operations vs n, one line per algorithm and density, log-log axes (ticks are powers of ten). Parallel straight lines of slope ≈ 1 confirm near-linear work.
2. `results/plots/scans_per_edge.png` – `edgeScans / m` vs n. Flat lines at 2.0 (Kosaraju, Prim) and 1.0 (Kahn, DAG) confirm linear work; Kruskal lies below.
3. `results/plots/kruskal_share.png` – share of edges Kruskal examines: sparse 0.77 → 1.00, dense 0.22 → 0.37.

Why plot 3 looks like this: Kruskal stops as soon as the lightest edges connect the graph. A random graph becomes connected after roughly (n/2)·ln n edges, i.e. a share of about ln n / (2·m/n) of all edges: ≈ ln n / 8 on sparse graphs (it reaches 1, so almost every edge is examined) and ≈ ln n / 40 on dense graphs (0.17 at n = 1 000, 0.29 at n = 100 000; measured 0.22 and 0.37). This is where each MST algorithm wins: the denser the graph, the more edges Kruskal never has to look at, while Prim always scans every arc.

## 7. Time

_Fill in with your own measurements from `results.csv` (median of 5 runs)._ For example: on my machine (CPU, JDK version) the largest graph (n = 100 000, m = 2 000 000) took about _X_ ms for Kruskal, _Y_ ms for Prim, _Z_ ms for Kosaraju. The two smallest graphs run in a few milliseconds and are dominated by JIT warm-up and noise. Times are not the main evidence: they depend on the hardware, the JVM version and its JIT state, memory/GC behaviour and background load, so a single run on one laptop cannot show that one algorithm is faster. The operation counters are identical on every machine, which is why all conclusions in this report are drawn from them.