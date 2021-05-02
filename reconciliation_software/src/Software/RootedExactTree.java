package Software;

import javafx.scene.shape.Box;

import java.util.ArrayList;
import java.util.TreeMap;

public class RootedExactTree {

    RootedExactNode root;
    TreeMap<String, RootedExactNode> nodes = new TreeMap<>();

    public RootedExactTree(){
        root = new RootedExactNode("root", 0, 0);
    }

    public void setRoot(RootedExactNode r){
        root = r;
    }

    public RootedExactNode getRoot() {
        return root;
    }

    public RootedExactNode findNode(String name){
        return nodes.get(name);
    }

    public static RootedExactNode lca(RootedExactNode node1, RootedExactNode node2) {
        ArrayList<RootedExactNode> path1 = pathToRoot(node1);
        ArrayList<RootedExactNode> path2 = pathToRoot(node2);

        int i = 0;
        while(!path2.contains(path1.get(i))){
            i++;
        }

        return path1.get(i);
    }

    public static ArrayList<RootedExactNode> pathToRoot(RootedExactNode node){
        ArrayList<RootedExactNode> path = new ArrayList<>();

        RootedExactNode temp = node;
        while(temp != null){
            path.add(temp);
            temp = temp.getParent();
        }

        return path;
    }

    public void setNodes(TreeMap<String, RootedExactNode> nodes) {
        this.nodes = nodes;
    }
}