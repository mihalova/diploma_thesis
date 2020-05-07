package rec;

import java.util.ArrayList;

public class UnrootedTree {

    private ArrayList<UnrootedNode> nodes;

    private ArrayList<Edge> edges;

    public UnrootedTree(){
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public boolean addNode(UnrootedNode node) {
        return nodes.add(node);
    }

    public boolean addEdge(Edge e) {
        return edges.add(e);
    }

    public void renameNodes(){}

}
