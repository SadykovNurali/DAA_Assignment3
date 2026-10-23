
import daa.algorithm.DagShortestPath;
import daa.graph.Graph;
import daa.metrics.Metrics;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class DagShortestPathTest {
    @Test void matchesBellmanFordWithNegativeWeights() {
        Random rnd = new Random(5);
        for (int t = 0; t < 100; t++) {
            int n = 2 + rnd.nextInt(30), m = rnd.nextInt(4 * n);
            int[] rank = new int[n];
            for (int i = 0; i < n; i++) rank[i] = i;
            for (int i = n - 1; i > 0; i--) { int j = rnd.nextInt(i + 1); int x = rank[i]; rank[i] = rank[j]; rank[j] = x; }
            Graph g = new Graph(n, true);
            int[] eu = new int[m], ev = new int[m], ew = new int[m];
            int cnt = 0;
            boolean[] hasIn = new boolean[n];
            for (int i = 0; i < m; i++) {
                int a = rnd.nextInt(n), b = rnd.nextInt(n);
                if (a == b) continue;
                if (rank[a] > rank[b]) { int x = a; a = b; b = x; }
                int w = rnd.nextInt(41) - 20;
                g.addEdge(a, b, w);
                eu[cnt] = a; ev[cnt] = b; ew[cnt] = w; cnt++; hasIn[b] = true;
            }
            long[] bf = new long[n];
            Arrays.fill(bf, DagShortestPath.INF);
            for (int v = 0; v < n; v++) if (!hasIn[v]) bf[v] = 0;
            for (int it = 0; it < n; it++)
                for (int i = 0; i < cnt; i++)
                    if (bf[eu[i]] != DagShortestPath.INF && bf[eu[i]] + ew[i] < bf[ev[i]]) bf[ev[i]] = bf[eu[i]] + ew[i];
            DagShortestPath sp = new DagShortestPath().run(g, new Metrics());
            assertArrayEquals(bf, sp.dist);
        }
    }
}