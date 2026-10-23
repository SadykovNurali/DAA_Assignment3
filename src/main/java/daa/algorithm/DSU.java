package daa.algorithm;

public class DSU {
    private int[] parent, size;
    private int components;
    public long findCalls, unionCalls;

    public DSU(int n) { makeSet(n); }

    public void makeSet(int n) {
        parent = new int[n]; size = new int[n];
        for (int i = 0; i < n; i++) { parent[i] = i; size[i] = 1; }
        components = n;
        findCalls = unionCalls = 0;
    }

    public int find(int x) {
        findCalls++;
        return root(x);
    }

    private int root(int x) {
        int r = x;
        while (parent[r] != r) r = parent[r];
        while (parent[x] != r) { int nx = parent[x]; parent[x] = r; x = nx; }
        return r;
    }

    public boolean union(int x, int y) {
        int a = root(x), b = root(y);
        if (a == b) return false;
        if (size[a] < size[b]) { int t = a; a = b; b = t; }
        parent[b] = a;
        size[a] += size[b];
        components--;
        unionCalls++;
        return true;
    }

    public int components() { return components; }
    public long ops() { return findCalls + unionCalls; }
}