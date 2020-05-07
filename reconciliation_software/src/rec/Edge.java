package rec;

public class Edge {
    private final UnrootedNode u;
    private final UnrootedNode v;
    private final double minDepth;
    private final double maxDepth;

    public Edge(UnrootedNode u, UnrootedNode v, double minDepth, double maxDepth){
        this.u = u;
        this.v = v;
        this.minDepth = minDepth;
        this.maxDepth = maxDepth;
    }

    public UnrootedNode getU() {
        return u;
    }

    public UnrootedNode getV() {
        return v;
    }

    public double getMaxDepth() {
        return maxDepth;
    }

    public double getMinDepth() {
        return minDepth;
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
