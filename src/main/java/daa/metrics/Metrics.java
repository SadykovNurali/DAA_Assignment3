package daa.metrics;

public class Metrics {
    public long dsuOps, edgeScans, comparisons;
    public int maxDepth;

    public void reset() { dsuOps = edgeScans = comparisons = 0; maxDepth = 0; }
}