package daa.graph;

import java.util.Arrays;

public class Graph {
    private final int n;
    private final boolean directed;
    private int m;
    private final int[] head;
    private int[] next = new int[16], to = new int[16], wt = new int[16];
    private int arcs;
    private int[] eu = new int[16], ev = new int[16], ew = new int[16];

    public Graph(int n, boolean directed) {
        this.n = n;
        this.directed = directed;
        head = new int[n];
        Arrays.fill(head, -1);
    }

    public void addEdge(int u, int v, int w) {
        addArc(u, v, w);
        if (!directed && u != v) addArc(v, u, w);
        if (m == eu.length) {
            int c = m * 2;
            eu = Arrays.copyOf(eu, c); ev = Arrays.copyOf(ev, c); ew = Arrays.copyOf(ew, c);
        }
        eu[m] = u; ev[m] = v; ew[m] = w;
        m++;
    }

    private void addArc(int u, int v, int w) {
        if (arcs == to.length) {
            int c = arcs * 2;
            next = Arrays.copyOf(next, c); to = Arrays.copyOf(to, c); wt = Arrays.copyOf(wt, c);
        }
        to[arcs] = v; wt[arcs] = w; next[arcs] = head[u]; head[u] = arcs++;
    }

    public int head(int u) { return head[u]; }
    public int next(int e) { return next[e]; }
    public int to(int e) { return to[e]; }
    public int weight(int e) { return wt[e]; }

    public int n() { return n; }
    public int m() { return m; }
    public boolean isDirected() { return directed; }

    public int edgeU(int i) { return eu[i]; }
    public int edgeV(int i) { return ev[i]; }
    public int edgeW(int i) { return ew[i]; }

    public Graph reversed() {
        Graph r = new Graph(n, true);
        for (int i = 0; i < m; i++) r.addEdge(ev[i], eu[i], ew[i]);
        return r;
    }
}