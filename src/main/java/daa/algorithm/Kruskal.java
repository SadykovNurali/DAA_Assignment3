package daa.algorithm;

import daa.graph.Graph;
import daa.metrics.Metrics;

public class Kruskal {
    public static class Result { public long weight; public int edges, components; }

    public static Result run(Graph g, Metrics mt) {
        int n = g.n(), m = g.m();
        int[] idx = new int[m], tmp = new int[m];
        for (int i = 0; i < m; i++) idx[i] = i;
        sort(g, idx, tmp, 0, m, mt);

        DSU d = new DSU(n);
        Result r = new Result();
        for (int i = 0; i < m && r.edges < n - 1; i++) {   // ранний выход
            int e = idx[i];
            mt.edgeScans++;
            if (d.find(g.edgeU(e)) != d.find(g.edgeV(e))) {
                d.union(g.edgeU(e), g.edgeV(e));
                r.weight += g.edgeW(e);
                r.edges++;
            }
        }
        mt.dsuOps += d.ops();
        r.components = n - r.edges;
        return r;
    }

    private static void sort(Graph g, int[] a, int[] t, int lo, int hi, Metrics mt) {
        if (hi - lo < 2) return;
        int mid = (lo + hi) >>> 1;
        sort(g, a, t, lo, mid, mt);
        sort(g, a, t, mid, hi, mt);
        int i = lo, j = mid, k = lo;
        while (i < mid && j < hi) {
            mt.comparisons++;
            t[k++] = g.edgeW(a[i]) <= g.edgeW(a[j]) ? a[i++] : a[j++];
        }
        while (i < mid) t[k++] = a[i++];
        while (j < hi) t[k++] = a[j++];
        System.arraycopy(t, lo, a, lo, hi - lo);
    }
}