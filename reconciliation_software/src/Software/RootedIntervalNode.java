package Software;

import java.util.ArrayList;

public class RootedIntervalNode extends Node {

    RootedIntervalNode parent;
    RootedIntervalNode left;
    RootedIntervalNode right;
    int level;

    private double minL;
    private double maxL;
    //minimalna, maximalna hlbka namapovania vrcholu
    private double minD;
    private double maxD;
    private RootedExactNode lcaS;
    private boolean mappedToLca;
    private int levelDistanceFromParent;
    private RootedExactNode speciesNodeBelow;

    public RootedIntervalNode(String name) {
        super(name);
    }

    public RootedIntervalNode(RootedIntervalNode root) {
        super(root.getName());
        right = root.getRight();
        left = root.getLeft();
        lcaS = root.getLcaS();
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int l) {
        this.level = l;
    }

    public RootedIntervalNode getParent() {
        return parent;
    }

    public void setParent(RootedIntervalNode parent) {
        this.parent = parent;
    }

    public RootedIntervalNode getLeft() {
        return left;
    }

    public void setLeft(RootedIntervalNode left) {
        this.left = left;
    }

    public RootedIntervalNode getRight() {
        return right;
    }

    public void setRight(RootedIntervalNode right) {
        this.right = right;
    }

    public double getMinD() {
        return minD;
    }

    public void setMinD(double minD) {
        this.minD = Reconciliator.round(minD);
    }

    public double getMaxD() {
        return maxD;
    }

    public void setMaxD(double maxD) {
        this.maxD = Reconciliator.round(maxD);
    }

    public double getMinL() {
        return minL;
    }

    public void setMinL(double minL) {
        this.minL = Reconciliator.round(minL);
    }

    public double getMaxL() {
        return maxL;
    }

    public void setMaxL(double maxL) {
        this.maxL = Reconciliator.round(maxL);
    }

    public RootedExactNode getLcaS() {
        return lcaS;
    }

    public void setLcaS(RootedExactNode lcaS) {
        this.lcaS = lcaS;
    }

    public int getMappedToLca_Integer() {
        return mappedToLca ? 1 : 0;
    }

    public boolean getMappedToLca() {
        return mappedToLca;
    }

    public void setMappedToLca(boolean m) {
        this.mappedToLca = m;
    }

    public int getLevelDistanceFromParent() {
        return levelDistanceFromParent;
    }

    public void setLevelDistanceFromParent(int l) {
        this.levelDistanceFromParent = l;
    }

    public RootedExactNode getSpeciesNodeBelow() {
        return speciesNodeBelow;
    }

    public void setSpeciesNodeBelow(RootedExactNode speciesNodeBelow){
        this.speciesNodeBelow = speciesNodeBelow;
    }

}