package Software;

public class RootedExactNode extends Node {

    RootedExactNode parent;
    RootedExactNode right;
    RootedExactNode left;
    double depth;
    int level;

    public RootedExactNode(String name) {
        super(name);
    }

    public RootedExactNode(String name, double depth, int level){
        super(name);
        this.depth = Reconciliator.round(depth);
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int l) {
        this.level = l;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = Reconciliator.round(depth);    }

    public RootedExactNode getParent() {
        return parent;
    }

    public void setParent(RootedExactNode parent) {
        this.parent = parent;
    }

    public RootedExactNode getLeft() {
        return left;
    }

    public void setLeft(RootedExactNode left) {
        this.left = left;
    }

    public RootedExactNode getRight() {
        return right;
    }

    public void setRight(RootedExactNode right) {
        this.right = right;
    }

}