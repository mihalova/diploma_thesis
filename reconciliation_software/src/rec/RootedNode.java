package rec;

public class RootedNode extends Node{
    private double d;

    private RootedNode parent;
    private RootedNode left;
    private RootedNode right;

    RootedNode(String name, double d) {
        super(name);
        this.d = Reconciliation.round(d);
    }

    public double getDepth() {
        return d;
    }

    public void setDepth(double d){
        this.d = Reconciliation.round(d);
    }

    public RootedNode getParent() {
        return parent;
    }

    public void setParent(RootedNode parent) {
        this.parent = parent;
    }

    public RootedNode getLeft() {
        return left;
    }

    public void setLeft(RootedNode left) {
        this.left = left;
    }

    public RootedNode getRight() {
        return right;
    }

    public void setRight(RootedNode right) {
        this.right = right;
    }

}
