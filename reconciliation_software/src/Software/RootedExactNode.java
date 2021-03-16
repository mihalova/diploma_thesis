package Software;

public class RootedExactNode extends Node {
    private double depth;
    private int level;

    private RootedExactNode parent;
    private RootedExactNode left;
    private RootedExactNode right;

    public RootedExactNode(String name, double depth, int level){
        super(name);
        this.depth = Reconciliator.round(depth);
        this.level = level;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double d) {
        this.depth = Reconciliator.round(d);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int l) {
        this.level = l;
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

    public RootedExactNode getParent() {
        return parent;
    }

    public void setParent(RootedExactNode parent) {
        this.parent = parent;
    }

}