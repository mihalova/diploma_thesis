package Software;
import static Software.Main.EPSILON;
import java.util.TreeMap;

public class RootedIntervalTree {
    private RootedIntervalNode root;
    private RootedExactTree speciesTree;
    TreeMap<String, String> leafMap;
    private DL score;
    boolean countLossesAboveRoot = false;

    public RootedIntervalTree(UnrootedNode newRoot, Edge sourceEdge, RootedExactTree speciesTree,
                              TreeMap<String, String> leafMap) {
        this.speciesTree = speciesTree;
        this.leafMap = leafMap;
        this.root = enroot(newRoot, sourceEdge);
    }

    public RootedIntervalTree(RootedIntervalNode root, RootedExactTree speciesTree, TreeMap<String, String> leafMap) {
        this.speciesTree = speciesTree;
        this.leafMap = leafMap;
        this.root = root;
    }

    public RootedIntervalTree() {
        root = new RootedIntervalNode("root");
        leafMap = new TreeMap<>();
    }

    public void setCountLossesAboveRoot(boolean countLossesAboveRoot) {
        this.countLossesAboveRoot = countLossesAboveRoot;
    }

    public void setScore(DL score){
        this.score = score;
    }

    public DL getScore(){
        return score;
    }

    public void addMapping(String geneNode, String speciesNode) {
        leafMap.put(geneNode, speciesNode);
    }

    public TreeMap<String, String> getLeafMap() {
        return leafMap;
    }

    public RootedIntervalNode getRoot() {
        return root;
    }

    public void computeLevel(RootedIntervalNode u) {
        if (u.getLeft() == null) {
            u.setMappedToLca(true);
            u.setSpeciesNodeBelow(u.getLcaS());
            u.setLevel(u.getSpeciesNodeBelow().getLevel());
        } else {
            computeLevel(u.getRight());
            computeLevel(u.getLeft());
            RootedExactNode rightS = u.getRight().getSpeciesNodeBelow();
            RootedExactNode leftS = u.getLeft().getSpeciesNodeBelow();
            RootedExactNode speciesNode;
            double nodesDifference = u.getLcaS().getDepth() - u.getMaxD();
            if (nodesDifference > EPSILON) { //not mapped to lca
                if (rightS.getDepth() > leftS.getDepth())
                    speciesNode = computeSpeciesNodeBelow(u, leftS);
                else
                    speciesNode = computeSpeciesNodeBelow(u, rightS);
                u.setMappedToLca(false);
                u.setSpeciesNodeBelow(speciesNode);
            } else { //mapped to lca
               if (rightS == u.getLcaS() || leftS == u.getLcaS()) {
                    //if at least one parent have the same speciesNodeBelow
                    u.setMappedToLca(false);
                } else
                    u.setMappedToLca(true);
                u.setSpeciesNodeBelow(u.getLcaS());
            }
            u.setLevel(u.getSpeciesNodeBelow().getLevel());
            levelDistanceFromChildren(u);
        }
    }

    public void levelDistanceFromChildren(RootedIntervalNode u) {
        int rightLevelDistance = u.getRight().getLevel() - u.getLevel();
        int leftLevelDistance = u.getLeft().getLevel() - u.getLevel();
        u.getRight().setLevelDistanceFromParent(rightLevelDistance - u.getMappedToLca_Integer());
        u.getLeft().setLevelDistanceFromParent(leftLevelDistance - u.getMappedToLca_Integer());
    }

    private RootedExactNode computeSpeciesNodeBelow(RootedIntervalNode u, RootedExactNode speciesNode) {
        RootedExactNode actualSpeciesNode = speciesNode;
        while (speciesNode.getDepth() > u.getMaxD()) {
            actualSpeciesNode = speciesNode;
            if (speciesNode.getParent() == null)
                break;
            else
                speciesNode = speciesNode.getParent();
        }
        return actualSpeciesNode;
    }

    public DL countDL(RootedIntervalNode u) {
        DL dl = new DL(0,0);
        if (u.getLeft() != null) {
            dl.sum(countDL(u.getRight()), countDL(u.getLeft()));
        }

        int loss = 0;
        int duplication = 0;

        loss += u.getLevelDistanceFromParent();
        if (u.getName().equals("root") && !u.getLcaS().getName().equals("root") && countLossesAboveRoot)
            loss += u.getLevel();

        if (!u.getMappedToLca()) {
            duplication += 1;
        }
        dl.sum(dl, new DL(duplication, loss));
        return dl;
    }

    private RootedIntervalNode enroot(UnrootedNode uNode, Edge sourceEdge) {
        RootedIntervalNode rNode = new RootedIntervalNode(uNode.getName());

        if (uNode.getEdges().size() != 3 && uNode.getEdges().size() != 1) {
            System.err.println("Wrong number of edges for node " + uNode.getEdges());
            System.exit(1);
        }

        for (Edge e : uNode.getEdges()) {
            if (e.equals(sourceEdge)) continue;

            //assert rNode.getLeft() == null || rNode.getRight() == null;

            UnrootedNode otherNode = e.otherNode(uNode);
            if (otherNode == null) {
                System.err.println("Mistake in gene tree file.");
                System.exit(1);
            }

            RootedIntervalNode child = enroot(otherNode, e); //zakorenim strom od noveho root smerom dole
            child.setParent(rNode);
            child.setMinL(e.getMinLength());
            child.setMaxL(e.getMaxLength());

            //nastavi postupne deti root
            if (rNode.getLeft() == null) {
                rNode.setLeft(child);
            } else if (rNode.getRight() == null) {
                rNode.setRight(child);
            } else {
                System.err.println("Node " + uNode.getName() + " doesn't contain source edge.");
                System.exit(1);
            }
        }
        //skontroluje, ci su dobre nastavene deti
        if (rNode.getLeft() != null && rNode.getRight() == null) {
            System.err.println("There are 2 edges for node " + rNode.getName());
            System.exit(1);
        }

        return rNode;
    }

    public void setLcas(RootedIntervalNode node) {
        if (node.getRight() != null) {
            setLcas(node.getRight());
            setLcas(node.getLeft());
            node.setLcaS(speciesTree.lca(node.getLeft().getLcaS(), node.getRight().getLcaS()));
            if (node.getLeft().getName().compareTo(node.getRight().getName()) < 0) {
                node.setName(node.getLeft().getName() + "," + node.getRight().getName());
            } else {
                node.setName(node.getRight().getName() + "," + node.getLeft().getName());
            }
        } else {
            node.setLcaS(speciesTree.findNode(leafMap.get(node.getName())));
            if (node.getLcaS() == null) {
                System.err.println("Wrong name of leaf " + node.getName());
            }
        }
    }

    boolean upward(RootedIntervalNode v) {
        //leaves
        if (v.getLeft() == null) {
            v.setMinD(v.getLcaS().getDepth());
            v.setMaxD(v.getLcaS().getDepth());
            return true;
        }
        //pre kazde dieta sprav upward
        if (!upward(v.getLeft()) || !upward(v.getRight())) return false;
        //else vetva v algoritme
        double minD = Math.max(v.getLeft().getMinD() - v.getLeft().getMaxL(),
                v.getRight().getMinD() - v.getRight().getMaxL());
        double maxD = Math.min(v.getLeft().getMaxD() - v.getLeft().getMinL(),
                v.getRight().getMaxD() - v.getRight().getMinL());
        maxD = Math.min(maxD, v.getLcaS().getDepth());
        //no solution
        if (maxD < minD) {
            if (minD - maxD > EPSILON) {
                return false;
            } else {
                maxD = minD;
            }
        }
        v.setMinD(minD);
        v.setMaxD(maxD);
        return true;
    }

    void downward(RootedIntervalNode v) {
        if (v.getLeft() == null) return;
        double minDepthL = Math.max(v.getMinD() + v.getLeft().getMinL(), v.getLeft().getMinD());
        double maxDepthL = Math.min(v.getMaxD() + v.getLeft().getMaxL(), v.getLeft().getMaxD());
        double minDepthR = Math.max(v.getMinD() + v.getRight().getMinL(), v.getRight().getMinD());
        double maxDepthR = Math.min(v.getMaxD() + v.getRight().getMaxL(), v.getRight().getMaxD());
        v.getLeft().setMinD(minDepthL);
        v.getLeft().setMaxD(maxDepthL);
        v.getRight().setMinD(minDepthR);
        v.getRight().setMaxD(maxDepthR);
        downward(v.getLeft());
        downward(v.getRight());
    }


}