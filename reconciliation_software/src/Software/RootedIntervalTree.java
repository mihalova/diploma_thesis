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
        setLcas(root);
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
            if (u.getMaxD() < u.getLcaS().getDepth() && nodesDifference > EPSILON) { //not mapped to lca
                if (rightS.getDepth() > leftS.getDepth())
                    speciesNode = computeSpeciesNodeBelow(u, leftS);
                else
                    speciesNode = computeSpeciesNodeBelow(u, rightS);
                u.setMappedToLca(false);
                u.setSpeciesNodeBelow(speciesNode);
                u.setLevel(u.getSpeciesNodeBelow().getLevel());
            } else { //mapped to lca
                //more gene nodes can mapped to root, only first one is speciation
                if (rightS == u.getLcaS() && leftS == u.getLcaS()) {
                    //if both parents have the same speciesNodeBelow and both are mapped and are not leaves,
                    // we need to set one of them to unmapped
                    if (u.getRight().getMappedToLca() && u.getLeft().getMappedToLca()
                    && u.getRight().getRight() != null && u.getLeft().getRight() != null){
                        RootedIntervalNode child;
                        if (u.getRight().getMaxD() > u.getLeft().getMaxD())
                            child = u.getLeft();
                        else
                            child = u.getRight();
                        levelDistanceFromChildren(child);
                    }
                     u.setMappedToLca(false);
                } else if (rightS == u.getLcaS() || leftS == u.getLcaS()) {
                    //if at least one parent have the same speciesNodeBelow
                    u.setMappedToLca(false);
                } else
                    u.setMappedToLca(true);
                u.setSpeciesNodeBelow(u.getLcaS());
                u.setLevel(u.getSpeciesNodeBelow().getLevel());
            }
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
        //System.out.println("Node: " + u.getName());
        //RootedExactNode lca = u.getLcaS();

        int loss = 0;
        int duplication = 0;

        loss += u.getLevelDistanceFromParent();
        if (u.getName().equals("root") && !u.getLcaS().getName().equals("root") && countLossesAboveRoot)
            loss += u.getLevel();
        //System.out.println("\tLoss: " + losses);

        if (!u.getMappedToLca()) {
            //System.out.println("\tDuplication: " + 1);
            duplication += 1;
        }
        dl.sum(dl, new DL(duplication, loss));
        return dl;
    }


    // novy koren 'u' genoveho stromu sa mapuje do svojho maxD
    // v detoch 'v' a 'w' uz presiel upward-downward algoritmus a ich intervaly hlbky mapovania su urcene linearnym programovanim
    // obe deti sa nemusia namapovat do svojich maximalnych hlbok, lebo nemusi na to vystacit dlzka korenovej hrany
    // chceme najst hlbky namapovania deti, aby pocet udalosti bol co najmensi
    // nastavi celkovy Software.DL count stromu
    /*public static void findMostParsimoniousDepths(Software.RootedIntervalNode u, Software.Edge rootEdge) {
        Software.RootedIntervalNode v = u.getLeft();
        Software.RootedIntervalNode w = u.getRight();

        //koren nema stanoveny lcaS
        u.setLcaS(Software.RootedExactTree.lca(v.getLcaS(),w.getLcaS()));

        //detom sa orezu intervaly zhora kvoli nastavenej hlbke korena
        Double newVmin = Software.Reconciliator.linear2(0, true, v, w, u.getMaxD(), rootEdge, Software.Reconciliator.getDirPath());
        newVmin = Software.Reconciliator.round(newVmin);
        Double newWmin = Software.Reconciliator.linear2(1, true, v, w, u.getMaxD(), rootEdge, Software.Reconciliator.getDirPath());
        newWmin = Software.Reconciliator.round(newWmin);

        v.setMinD(newVmin);
        w.setMinD(newWmin);

        //detom sa vypocita Software.DL pre kazdy podinterval
        v.countSubintervalDL();
        w.countSubintervalDL();

        double bestVdepth = Integer.MAX_VALUE;
        double bestWdepth = Integer.MAX_VALUE;
        Software.DL bestDL = new Software.DL(Integer.MAX_VALUE/2, Integer.MAX_VALUE/2);

        assert v.intervalsSize() > 0 && w.intervalsSize() > 0;

        for (int i = 0; i < v.intervalsSize(); i++) {
            Software.Interval vInterval = v.getInterval(i);

            //mapovanie na vrch intervalu ma rovnako vela Software.DL
            double vDepth = vInterval.getMinDepth();

            //hlbku w vypocitame z LP
            Double wDepth = Software.Reconciliator.linearW(0, false, vDepth, w, u.getMaxD(), rootEdge, Software.Reconciliator.getDirPath());
            wDepth = Software.Reconciliator.round(wDepth);
            Software.DL DLu = u.countDL(u.getMaxD(), vDepth, wDepth);

            //v praci binarne vyhladavanie, tu len prechod cez podintervaly
            for (int j = w.intervalsSize() - 1; j >= 0; j--) {
                Software.Interval wInterval = w.getInterval(j);
                //ak sa rovna wInterval.getMaxD(), moze sa namapovat aj do nizsieho intervalu
                if(wInterval.getMaxDepth() == wDepth && j > 0) continue;
                //wDepth patri do intervalu
                if(wInterval.getMinDepth() <= wDepth && wInterval.getMaxDepth() >= wDepth){
                    Software.DL minDL = new Software.DL(DLu.getDuplication() + vInterval.getDl().getDuplication() + wInterval.getDl().getDuplication(),
                            DLu.getLoss() + vInterval.getDl().getLoss() + wInterval.getDl().getLoss());
                    if(minDL.getSum() < bestDL.getSum()){
                        bestDL = minDL;
                        bestVdepth = vDepth;
                        bestWdepth = wDepth;
                        break;
                    }
                }
            }
        }

        //nastavim v podstromoch v a w konkretne hlbky mapovania najuspornejsieho riesenia
        v.setDepth(bestVdepth);
        w.setDepth(bestWdepth);
        downwardSetSolution(v);
        downwardSetSolution(w);


        // ostava este vypocitat straty, ku ktorym dojde po ceste z korena S do mapovania korena G
        // do korena S musi vstupovat jeden gen, ten sa musi stratit vo vrcholoch na spominanej ceste
        int lossesOnPathBetweenRoots = Software.RootedExactTree.pathToRoot(u.getBearingNode(u.getMaxD())).size() - 1;

        //nastav celkove Software.DL korena
        u.setTotalDL(new Software.DL(bestDL.getDuplication(), bestDL.getLoss()+ lossesOnPathBetweenRoots));
    }
*/
    private RootedIntervalNode enroot(UnrootedNode uNode, Edge sourceEdge) {
        RootedIntervalNode rNode = new RootedIntervalNode(uNode.getName());

        if (uNode.getEdges().size() != 3 && uNode.getEdges().size() != 1) {
            System.err.println("Wrong number of edges for node " + uNode.getEdges());
        }

        for (Edge e : uNode.getEdges()) {
            if (e.equals(sourceEdge)) continue;

            //assert rNode.getLeft() == null || rNode.getRight() == null;

            UnrootedNode otherNode = e.otherNode(uNode);
            if (otherNode == null) {
                System.err.println("Mistake in gene tree file.");
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
            }
        }
        //skontroluje, ci su dobre nastavene deti
        if (rNode.getLeft() != null && rNode.getRight() == null) {
            System.err.println("There are 2 edges for node " + rNode.getName());
        }

        return rNode;
    }

    private void setLcas(RootedIntervalNode node) {
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
            if (minD - maxD > EPSILON)
                return false;
            else {
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
        //System.out.println(v.getName() + " " + minD +" "+maxD);
        downward(v.getLeft());
        downward(v.getRight());
    }

   /* private static void downwardSetSolution(Software.RootedIntervalNode v) {
        if(v.getLeft() == null) return;
        v.getLeft().setDepth(Math.min(v.getDepth() + v.getLeft().getMaxL(), v.getLeft().getMaxD()));
        v.getRight().setDepth(Math.min(v.getDepth() + v.getRight().getMaxL(), v.getRight().getMaxD()));
        downwardSetSolution(v.getLeft());
        downwardSetSolution(v.getRight());
    }*/
}