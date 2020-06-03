class RootedExactNode extends Node {
    private double d;

    private RootedExactNode parent;
    private RootedExactNode left;
    private RootedExactNode right;

    public RootedExactNode(String name, double d){
        super(name);
        this.d = Reconciliator.round(d);
    }

    public double getD() {
        return d;
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

    public void setD(double d) {
        this.d = Reconciliator.round(d);
    }
}