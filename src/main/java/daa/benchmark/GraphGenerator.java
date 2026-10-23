package daa.benchmark;

import daa.graph.Graph;
import java.util.Random;

public class GraphGenerator {
    private static int[] shuffled(int n, Random rnd) {
        int[] p = new int[n];
        for (int i = 0; i < n; i++) p[i] = i;
        for (int i = n - 1; i > 0; i--) { int j = rnd.nextInt(i + 1); int t = p[i]; p[i] = p[j]; p[j] = t; }
        return p;
    }
    public static Graph undirected(int n, int m) {
        Random rnd = new Random(42);
        int[] perm = shuffled(n, rnd);
        Graph g = new Graph(n, false);
        for (int i = 1; i < n; i++) {
            int a = perm[rnd.nextInt(i)], b = perm[i];
            g.addEdge(a, b, 1 + rnd.nextInt(1000));
        }
        for (int i = n - 1; i < m; i++) {
            int a = rnd.nextInt(n), b = rnd.nextInt(n);
            while (b == a) b = rnd.nextInt(n);
            g.addEdge(a, b, 1 + rnd.nextInt(1000));
        }
        return g;
    }

    public static Graph directed(int n, int m) {
        Random rnd = new Random(42);
        Graph g = new Graph(n, true);
        for (int i = 0; i < m; i++) {
            int a = rnd.nextInt(n), b = rnd.nextInt(n);
            while (b == a) b = rnd.nextInt(n);
            g.addEdge(a, b, 1 + rnd.nextInt(1000));
        }
        return g;
    }

    public static Graph dag(int n, int m) {
        Random rnd = new Random(42);
        int[] rank = shuffled(n, rnd);
        Graph g = new Graph(n, true);
        for (int i = 0; i < m; i++) {
            int a = rnd.nextInt(n), b = rnd.nextInt(n);
            while (b == a) b = rnd.nextInt(n);
            if (rank[a] > rank[b]) { int t = a; a = b; b = t; }
            g.addEdge(a, b, 1 + rnd.nextInt(1000));
        }
        return g;
    }
}