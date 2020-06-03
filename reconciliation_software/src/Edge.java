public class Edge {
    private final UnrootedNode u;
    private final UnrootedNode v;
    private final double minL;
    private final double maxL;

    public Edge(UnrootedNode u, UnrootedNode v, double minL, double maxL){
        this.u = u;
        this.v = v;
        this.minL = minL;
        this.maxL = maxL;
    }

    public UnrootedNode getU() {
        return u;
    }

    public UnrootedNode getV() {
        return v;
    }

    public double getMaxL() {
        return maxL;
    }

    public double getMinL() {
        return minL;
    }

    @Override public boolean equals(Object obj) {
        Edge e = (Edge) obj;
        return e != null && ((e.u.equals(u) && e.v.equals(v)) || (e.v.equals(u) && (e.u.equals(v))));
    }

    public UnrootedNode otherNode(UnrootedNode node) {
        UnrootedNode otherNode = null;
        if(u.equals(node)){
            otherNode = v;
        } else if(v.equals(node)){
            otherNode = u;
        }
        return otherNode;
    }
}