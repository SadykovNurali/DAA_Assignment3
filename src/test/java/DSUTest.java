
import daa.algorithm.DSU;
import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class DSUTest {
    @Test
    void agreesWithNaive() {
        Random rnd = new Random(1);
        int n = 200;
        DSU d = new DSU(n);
        int[] label = new int[n];
        for (int i = 0; i < n; i++) label[i] = i;
        for (int k = 0; k < 300; k++) {
            int a = rnd.nextInt(n), b = rnd.nextInt(n);
            d.union(a, b);
            int la = label[a], lb = label[b];
            for (int i = 0; i < n; i++) if (label[i] == lb) label[i] = la;
        }
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                assertEquals(label[i] == label[j], d.find(i) == d.find(j));
        assertEquals(java.util.Arrays.stream(label).distinct().count(), d.components());
    }
}