public class Edge {
    private final UnrootedNode u;
    private final UnrootedNode v;
    private double minLength;
    private double maxLength;

    public Edge(UnrootedNode u, UnrootedNode v, double minLength, double maxLength){
        this.u = u;
        this.v = v;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public UnrootedNode getU() {
        return u;
    }

    public UnrootedNode getV() {
        return v;
    }

    public double getMaxL() {
        return maxLength;
    }

    public double getMinLength() {
        return minLength;
    }

    public void setMaxL(double maxL) {
        this.maxLength = maxL;
    }

    public void setMinLength(double minLength) {
        this.minLength = minLength;
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