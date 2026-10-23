
import daa.algorithm.Kosaraju;
import daa.graph.Graph;
import daa.metrics.Metrics;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StackSafetyTest {
    @Test void pathOf100000() {
        int n = 100_000;
        Graph g = new Graph(n, true);
        for (int i = 0; i + 1 < n; i++) g.addEdge(i, i + 1, 1);
        Metrics mt = new Metrics();
        Kosaraju.Result r = assertDoesNotThrow(() -> Kosaraju.run(g, mt));
        assertEquals(n, r.count);
        assertEquals(n, mt.maxDepth);
    }
}