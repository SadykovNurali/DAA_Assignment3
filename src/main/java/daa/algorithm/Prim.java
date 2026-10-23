package daa.algorithm;

import daa.graph.Graph;
import daa.metrics.Metrics;
import java.util.PriorityQueue;

public class Prim {
    public static Kruskal.Result run(Graph g, Metrics mt) {
        int n = g.n();
        boolean[] in = new boolean[n];
        PriorityQueue<Long> pq = new PriorityQueue<>((a, b) -> { mt.comparisons++; return Long.compare(a, b); });
        Kruskal.Result r = new Kruskal.Result();
        for (int s = 0; s < n; s++) {
            if (in[s]) continue;
            r.components++;
            pq.add((long) s);
            while (!pq.isEmpty()) {
                long key = pq.poll();
                int u = (int) (key & 0xFFFFFFFFL);
                int w = (int) (key >>> 32);
                if (in[u]) continue;
                in[u] = true;
                r.weight += w;
                if (u != s) r.edges++;
                for (int e = g.head(u); e != -1; e = g.next(e)) {
                    mt.edgeScans++;
                    int v = g.to(e);
                    if (!in[v]) pq.add(((long) g.weight(e) << 32) | v);
                }
            }
        }
        return r;
    }
}