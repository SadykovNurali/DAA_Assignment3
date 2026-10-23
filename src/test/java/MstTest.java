
import daa.algorithm.*;
import daa.benchmark.GraphGenerator;
import daa.graph.Graph;
import daa.metrics.Metrics;
import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class MstTest {
    @Test void kruskalEqualsPrim() {
        Random rnd = new Random(7);
        for (int t = 0; t < 100; t++) {
            int n = 2 + rnd.nextInt(60), m = n - 1 + rnd.nextInt(3 * n);
            Graph g = GraphGenerator.undirected(n, m);
            Kruskal.Result a = Kruskal.run(g, new Metrics());
            Kruskal.Result b = Prim.run(g, new Metrics());
            assertEquals(a.weight, b.weight);
            assertEquals(n - 1, a.edges);
            assertEquals(n - 1, b.edges);
        }
    }

    @Test void disconnectedGivesForest() {
        Graph g = new Graph(5, false);
        g.addEdge(0, 1, 3); g.addEdge(1, 2, 4); g.addEdge(3, 3, 1);   // 3 с петлёй, 4 изолирована
        Kruskal.Result a = Kruskal.run(g, new Metrics()), b = Prim.run(g, new Metrics());
        assertEquals(3, a.components); assertEquals(3, b.components);
        assertEquals(7, a.weight); assertEquals(7, b.weight);
    }
}