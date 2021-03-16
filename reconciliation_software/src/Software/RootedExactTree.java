package Software;

import java.util.ArrayList;
import java.util.TreeMap;

public class RootedExactTree {

    private RootedExactNode root;
    TreeMap<String, String> leafMap = new TreeMap<>();

    public RootedExactTree(){
        root = new RootedExactNode("root", 0, 0);
    }

    public RootedExactNode getRoot() {
        return root;
    }

    public void setRoot(RootedExactNode r){
        root = r;
    }

    public RootedExactNode findNode(String name){
        return findNodeInternal(root, name);
    }

    private RootedExactNode findNodeInternal(RootedExactNode node, String name){
        if(node==null) return null;
        if(node.getName().equals(name)) return node;
        RootedExactNode left = findNodeInternal(node.getLeft(), name);
        RootedExactNode right = findNodeInternal(node.getRight(), name);
        if(left != null) return left;
        if(right != null) return right;
        return null;
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

    //vrati dlzku cesty medzi dvoma vrcholmi, kde vieme, ze druhy vrchol je predok prveho
    public static int pathLengthToAncestor(RootedExactNode descendant, RootedExactNode ancestor){
        int result = 1;

        RootedExactNode temp = descendant;
        while(!temp.equals(ancestor)){
            result++;
            temp = temp.getParent();
        }

        return result;
    }

    //dlzka cesty medzi dvoma vrcholmi, ked nie je znamy ich vztah
    public static int pathLength(RootedExactNode node1, RootedExactNode node2){
        RootedExactNode lca = lca(node1, node2);

        if(!lca.equals(node1) && !lca.equals(node2)) {
            //ak ich lca nie je jeden z vrcholov, vrati dlzky ciest k lca -1 (lca sa zapocitalo dva krat)
            return pathLengthToAncestor(node1, lca) + pathLengthToAncestor(node2, lca) - 1;
        }
        if(lca.equals(node1)) {
            //ak je lca node1, node2 je jeho potomok
            return pathLengthToAncestor(node2, node1);
        }
        //inak je node1 potomok node2
        return pathLengthToAncestor(node1, node2);
    }

    public boolean checkEqualTopology(RootedExactTree rootedExactTree) {
        return checkEqualTopologyInternal(root, rootedExactTree.root);
    }

    private boolean checkEqualTopologyInternal(RootedExactNode u, RootedExactNode v) {
        if (u == null && v == null) return true;
        if (u == null || v == null) return false;
        return u.getName().equals(v.getName()) &&
                (checkEqualTopologyInternal(u.getLeft(), v.getLeft()) ||
                        checkEqualTopologyInternal(u.getLeft(), v.getRight()))
                &&
                (checkEqualTopologyInternal(u.getRight(), v.getRight()) ||
                        checkEqualTopologyInternal(u.getRight(), v.getLeft()));
    }

    public Double mapOn(RootedExactTree s) {
        return mapOnInternal(this.getRoot(), s);
    }

    private Double mapOnInternal(RootedExactNode node, RootedExactTree s) {
        if(node.getLeft() == null){
            double depthInS = s.findNode(leafMap.get(node.getName())).getDepth();
            double currentD = node.getDepth();
            node.setDepth(depthInS);
            return currentD - depthInS;
        }
        Double leftOffset = mapOnInternal(node.getLeft(), s);
        Double rightOffset = mapOnInternal(node.getRight(), s);
        if(leftOffset == null || rightOffset== null){
            return null;
        }
        if(!leftOffset.equals(rightOffset)){
            return null;
        }
        //Double offset = rightOffset;
        //if(leftOffset >= rightOffset){
        //	offset = leftOffset;
        //}
        node.setDepth(node.getDepth() - leftOffset);
        //node.setD(node.getD() - offset);

        return leftOffset;
        //return offset;
    }

    UnrootedTree unrootAndRename(){
        UnrootedTree unrooted = new UnrootedTree();

        addToUnrooted(unrooted, root.getLeft(), root.getLeft().getLeft());
        addToUnrooted(unrooted, root.getLeft(), root.getLeft().getRight());
        addToUnrooted(unrooted, root.getRight(), root.getRight().getLeft());
        addToUnrooted(unrooted, root.getRight(), root.getRight().getRight());

        UnrootedNode u = new UnrootedNode(root.getLeft().getName());
        int i = unrooted.indexOfNode(u);
        if(i > -1){
            u = unrooted.getNode(i);
        } else {
            unrooted.addNode(u);
        }

        UnrootedNode v = new UnrootedNode(root.getRight().getName());
        i = unrooted.indexOfNode(v);
        if(i > -1){
            v = unrooted.getNode(i);
        } else {
            unrooted.addNode(v);
        }

        double l = root.getLeft().getDepth() + root.getRight().getDepth() - 2*root.getDepth();
        Edge e = new Edge(u, v, l, l);
        u.addEdge(e);
        v.addEdge(e);
        unrooted.addEdge(e);

        unrooted.renameNodes();
        return unrooted;
    }

    private void addToUnrooted(UnrootedTree unrooted, RootedExactNode parent,
                               RootedExactNode child){
        if(child == null) return;
        String uName = parent.getName();
        String vName = child.getName();
        double l = child.getDepth() - parent.getDepth();

        UnrootedNode u = new UnrootedNode(uName);
        int i = unrooted.indexOfNode(u);
        if(i > -1){
            u = unrooted.getNode(i);
        } else {
            unrooted.addNode(u);
        }

        UnrootedNode v = new UnrootedNode(vName);
        i = unrooted.indexOfNode(v);
        if(i > -1){
            v = unrooted.getNode(i);
        } else {
            unrooted.addNode(v);
        }

        Edge e = new Edge(u, v, l, l);
        u.addEdge(e);
        v.addEdge(e);
        unrooted.addEdge(e);

        addToUnrooted(unrooted, child, child.getLeft());
        addToUnrooted(unrooted, child, child.getRight());
    }
}