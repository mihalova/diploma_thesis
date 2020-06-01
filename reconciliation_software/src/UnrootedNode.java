import java.util.ArrayList;

class UnrootedNode extends Node {
    private ArrayList<Edge> edges;

    public UnrootedNode(String name){
        super(name);
        edges = new ArrayList<>();
    }

    public boolean addEdge(Edge edge){
        return edges.add(edge);
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }
}
