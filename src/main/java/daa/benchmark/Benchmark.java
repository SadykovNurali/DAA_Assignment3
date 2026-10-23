package daa.benchmark;

import daa.algorithm.*;
import daa.graph.Graph;
import daa.metrics.Metrics;
import java.io.*;
import java.util.*;
import java.util.function.Supplier;

public class Benchmark {
    static final int[] N = {1000, 3162, 10000, 31623, 100000};
    static final int[] K = {4, 20};
    static final long[][] MST = {{147970, 30363}, {468911, 97589}, {1486139, 309550}, {4715515, 968517}, {15012094, 3056681}};
    static final long[][] SCC = {{27, 1}, {137, 1}, {373, 1}, {1188, 1}, {3923, 1}};
    static final long[][] DAG = {{396274, 252411}, {1271911, 723157}, {3941828, 2381342}, {12551891, 7376612}, {40096539, 23512087}};

    static void check(boolean ok, String msg) { if (!ok) throw new AssertionError("FAIL: " + msg); }

    static double time(Supplier<Long> run, Metrics mt, long[] out) {
        double[] t = new double[5];
        for (int i = 0; i < 5; i++) {
            mt.reset();
            long s = System.nanoTime();
            out[0] = run.get();
            t[i] = (System.nanoTime() - s) / 1e6;
        }
        Arrays.sort(t);
        return t[2];
    }

    public static void main(String[] args) throws IOException {
        new File("results").mkdirs();
        try (PrintWriter csv = new PrintWriter("results/results.csv")) {
            csv.println("algorithm,graph,n,m,time_ms,dsu_ops,edge_scans,comparisons,max_depth,result");
            for (int si = 0; si < N.length; si++) for (int di = 0; di < K.length; di++) {
                int n = N[si], m = K[di] * n;
                String name = "n" + n + "_m" + m;
                Graph u = GraphGenerator.undirected(n, m);
                Graph d = GraphGenerator.directed(n, m);
                Graph a = GraphGenerator.dag(n, m);
                Metrics mt = new Metrics();
                long[] out = new long[1];

                double tk = time(() -> Kruskal.run(u, mt).weight, mt, out);
                row(csv, "Kruskal", name, n, m, tk, mt, out[0]);
                long kw = out[0];
                check(kw == MST[si][di], name + " Kruskal weight " + kw);

                double tp = time(() -> Prim.run(u, mt).weight, mt, out);
                row(csv, "Prim", name, n, m, tp, mt, out[0]);
                check(out[0] == kw, name + " Kruskal != Prim");

                double ts = time(() -> (long) Kosaraju.run(d, mt).count, mt, out);
                check(mt.edgeScans == 2L * m, name + " Kosaraju scans");
                check(out[0] == SCC[si][di], name + " SCC count " + out[0]);
                row(csv, "Kosaraju", name, n, m, ts, mt, out[0]);

                double tn = time(() -> (long) Kahn.run(a, mt).size, mt, out);
                check(mt.edgeScans == m && out[0] == n, name + " Kahn");
                check(Kahn.validate(a, Kahn.run(a, new Metrics())), name + " topo order invalid");
                row(csv, "Kahn", name, n, m, tn, mt, out[0]);

                double tdp = time(() -> new DagShortestPath().run(a, mt).sum, mt, out);
                check(mt.edgeScans == m, name + " DAG scans");
                check(out[0] == DAG[si][di], name + " DAG sum " + out[0]);
                row(csv, "DagShortestPath", name, n, m, tdp, mt, out[0]);

                System.out.println(name + " OK");
            }
        }
        System.out.println("All reference values match. results/results.csv written.");
    }

    static void row(PrintWriter w, String alg, String g, int n, int m, double t, Metrics mt, long res) {
        w.printf(Locale.US, "%s,%s,%d,%d,%.3f,%d,%d,%d,%d,%d%n", alg, g, n, m, t, mt.dsuOps, mt.edgeScans, mt.comparisons, mt.maxDepth, res);
    }
}