
import daa.algorithm.Kosaraju;
import daa.graph.Graph;
import daa.metrics.Metrics;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class SccTest {
    static boolean[][] reach(Graph g, boolean rev) {
        int n = g.n();
        boolean[][] r = new boolean[n][n];
        for (int s = 0; s < n; s++) {
            Deque<Integer> q = new ArrayDeque<>(); q.add(s); r[s][s] = true;
            while (!q.isEmpty()) {
                int u = q.poll();
                for (int v = 0; v < n; v++) { /* ищем рёбра вручную */ }
                for (int e = g.head(u); e != -1; e = g.next(e)) if (!rev && !r[s][g.to(e)]) { r[s][g.to(e)] = true; q.add(g.to(e)); }
            }
        }
        return r;
    }

    @Test void matchesBruteForce() {
        Random rnd = new Random(3);
        for (int t = 0; t < 50; t++) {
            int n = 1 + rnd.nextInt(200), m = rnd.nextInt(3 * n);
            Graph g = new Graph(n, true);
            for (int i = 0; i < m; i++) g.addEdge(rnd.nextInt(n), rnd.nextInt(n), 1);
            boolean[][] r = reach(g, false);
            Kosaraju.Result res = Kosaraju.run(g, new Metrics());
            int comps = 0;
            for (int i = 0; i < n; i++) {
                boolean first = true;
                for (int j = 0; j < i; j++) if (r[i][j] && r[j][i]) first = false;
                if (first) comps++;
                for (int j = 0; j < n; j++)
                    assertEquals(r[i][j] && r[j][i], res.comp[i] == res.comp[j]);
            }
            assertEquals(comps, res.count);
        }
    }
}