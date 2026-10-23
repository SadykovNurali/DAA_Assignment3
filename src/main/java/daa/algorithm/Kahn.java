package daa.algorithm;

import daa.graph.Graph;
import daa.metrics.Metrics;

public class Kahn {
    public static class Result { public int[] order; public int size; public boolean hasCycle; }

    public static Result run(Graph g, Metrics mt) {
        int n = g.n();
        int[] indeg = new int[n];
        for (int u = 0; u < n; u++)
            for (int e = g.head(u); e != -1; e = g.next(e)) indeg[g.to(e)]++;
        int[] q = new int[n];
        int qh = 0, qt = 0;
        for (int v = 0; v < n; v++) if (indeg[v] == 0) q[qt++] = v;
        while (qh < qt) {
            int u = q[qh++];
            for (int e = g.head(u); e != -1; e = g.next(e)) {
                mt.edgeScans++;
                if (--indeg[g.to(e)] == 0) q[qt++] = g.to(e);
            }
        }
        Result r = new Result();
        r.order = q; r.size = qt; r.hasCycle = qt < n;
        return r;
    }

    public static boolean validate(Graph g, Kahn.Result r) {
        if (r.hasCycle) return false;
        int[] pos = new int[g.n()];
        for (int i = 0; i < r.size; i++) pos[r.order[i]] = i;
        for (int u = 0; u < g.n(); u++)
            for (int e = g.head(u); e != -1; e = g.next(e))
                if (pos[u] >= pos[g.to(e)]) return false;
        return true;
    }
}