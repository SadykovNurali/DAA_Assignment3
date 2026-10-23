package daa.algorithm;

import daa.graph.Graph;
import daa.metrics.Metrics;
import java.util.*;

public class DagShortestPath {
    public static final long INF = Long.MAX_VALUE / 4;
    public long[] dist;
    public int[] parent;
    public long sum;
    public boolean hasCycle;

    public DagShortestPath run(Graph g, Metrics mt) {
        int n = g.n();
        Kahn.Result t = Kahn.run(g, new Metrics());
        if (t.hasCycle) { hasCycle = true; return this; }
        int[] indeg = new int[n];
        for (int u = 0; u < n; u++)
            for (int e = g.head(u); e != -1; e = g.next(e)) indeg[g.to(e)]++;
        dist = new long[n]; parent = new int[n];
        Arrays.fill(dist, INF); Arrays.fill(parent, -1);
        for (int v = 0; v < n; v++) if (indeg[v] == 0) dist[v] = 0;
        for (int i = 0; i < n; i++) {
            int u = t.order[i];
            for (int e = g.head(u); e != -1; e = g.next(e)) {
                mt.edgeScans++;
                int v = g.to(e);
                if (dist[u] != INF && dist[u] + g.weight(e) < dist[v]) {
                    mt.comparisons++;
                    dist[v] = dist[u] + g.weight(e);
                    parent[v] = u;
                }
            }
        }
        for (long d : dist) if (d != INF) sum += d;
        return this;
    }

    public List<Integer> path(int v) {
        LinkedList<Integer> p = new LinkedList<>();
        for (; v != -1; v = parent[v]) p.addFirst(v);
        return p;
    }
}