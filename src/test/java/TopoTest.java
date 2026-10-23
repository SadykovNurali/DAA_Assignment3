
import daa.algorithm.Kahn;
import daa.benchmark.GraphGenerator;
import daa.graph.Graph;
import daa.metrics.Metrics;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TopoTest {
    @Test void validOnDag() {
        Graph g = GraphGenerator.dag(500, 2000);
        Kahn.Result r = Kahn.run(g, new Metrics());
        assertFalse(r.hasCycle);
        assertTrue(Kahn.validate(g, r));
    }
    @Test void detectsCycle() {
        Graph g = new Graph(3, true);
        g.addEdge(0, 1, 1); g.addEdge(1, 2, 1); g.addEdge(2, 0, 1);
        Kahn.Result r = Kahn.run(g, new Metrics());
        assertTrue(r.hasCycle);
        assertTrue(r.size < 3);
    }
}