package daa.algorithm;

import daa.graph.Graph;
import daa.metrics.Metrics;

public class Kosaraju {
    public static class Result { public int count; public int[] comp; }

    public static Result run(Graph g, Metrics mt) {
        int n = g.n();
        int[] order = new int[n], stack = new int[n], it = new int[n];
        boolean[] vis = new boolean[n];
        int cnt = 0;

        for (int s = 0; s < n; s++) {
            if (vis[s]) continue;
            int sp = 0;
            stack[sp++] = s; vis[s] = true; it[s] = g.head(s);
            while (sp > 0) {
                if (sp > mt.maxDepth) mt.maxDepth = sp;
                int u = stack[sp - 1], e = it[u];
                if (e == -1) { order[cnt++] = u; sp--; }
                else {
                    it[u] = g.next(e);
                    mt.edgeScans++;
                    int v = g.to(e);
                    if (!vis[v]) { vis[v] = true; it[v] = g.head(v); stack[sp++] = v; }
                }
            }
        }

        Graph r = g.reversed();
        Result res = new Result();
        res.comp = new int[n];
        java.util.Arrays.fill(res.comp, -1);
        for (int k = n - 1; k >= 0; k--) {
            int s = order[k];
            if (res.comp[s] != -1) continue;
            int sp = 0;
            stack[sp++] = s; res.comp[s] = res.count; it[s] = r.head(s);
            while (sp > 0) {
                if (sp > mt.maxDepth) mt.maxDepth = sp;
                int u = stack[sp - 1], e = it[u];
                if (e == -1) sp--;
                else {
                    it[u] = r.next(e);
                    mt.edgeScans++;
                    int v = r.to(e);
                    if (res.comp[v] == -1) { res.comp[v] = res.count; it[v] = r.head(v); stack[sp++] = v; }
                }
            }
            res.count++;
        }
        return res;
    }
}