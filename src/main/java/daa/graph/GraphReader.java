package daa.graph;

import java.io.*;
import java.util.StringTokenizer;

public class GraphReader {
    public static Graph read(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            int n = Integer.parseInt(st.nextToken());
            int m = Integer.parseInt(st.nextToken());
            boolean directed = st.nextToken().equals("directed");
            Graph g = new Graph(n, directed);
            for (int i = 0; i < m; i++) {
                st = new StringTokenizer(br.readLine());
                g.addEdge(Integer.parseInt(st.nextToken()),
                        Integer.parseInt(st.nextToken()),
                        Integer.parseInt(st.nextToken()));
            }
            return g;
        }
    }
}