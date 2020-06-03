import java.util.TreeMap;

public class RootedIntervalTree {
    private RootedExactTree speciesTree;
    private RootedIntervalNode root;
    TreeMap<String, String> leafMap;

    public RootedIntervalTree(UnrootedNode newRoot, Edge sourceEdge, RootedExactTree speciesTree,
                              TreeMap<String, String> leafMap){
        this.speciesTree = speciesTree;
        this.leafMap = leafMap;
        this.root = enroot(newRoot, sourceEdge);
    }

    // novy koren 'u' genoveho stromu sa mapuje do svojho maxD
    // v detoch 'v' a 'w' uz presiel upward-downward algoritmus a ich intervaly hlbky mapovania su urcene linearnym programovanim
    // obe deti sa nemusia namapovat do svojich maximalnych hlbok, lebo nemusi na to vystacit dlzka korenovej hrany
    // chceme najst hlbky namapovania deti, aby pocet udalosti bol co najmensi
    // nastavi celkovy DL count stromu
    public static void findMostParsimoniousDepths(RootedIntervalNode u, Edge rootEdge) {
        RootedIntervalNode v = u.getLeft();
        RootedIntervalNode w = u.getRight();

        //koren nema stanoveny lcaS
        u.setLcaS(RootedExactTree.lca(v.getLcaS(),w.getLcaS()));

        //detom sa orezu intervaly zhora kvoli nastavenej hlbke korena
        Double newVmin = Reconciliator.linear2(0, true, v, w, u.getMaxD(), rootEdge, Reconciliator.getDirPath());
        newVmin = Reconciliator.round(newVmin);
        Double newWmin = Reconciliator.linear2(1, true, v, w, u.getMaxD(), rootEdge, Reconciliator.getDirPath());
        newWmin = Reconciliator.round(newWmin);

        v.setMinD(newVmin);
        w.setMinD(newWmin);

        //detom sa vypocita DL pre kazdy podinterval
        v.countSubintervalDL();
        w.countSubintervalDL();

        double bestVdepth = Integer.MAX_VALUE;
        double bestWdepth = Integer.MAX_VALUE;
        DL bestDL = new DL(Integer.MAX_VALUE/2, Integer.MAX_VALUE/2);

        assert v.intervalsSize() > 0 && w.intervalsSize() > 0;

        for (int i = 0; i < v.intervalsSize(); i++) {
            Interval vInterval = v.getInterval(i);

            //mapovanie na vrch intervalu ma rovnako vela DL
            double vDepth = vInterval.getMinDepth();

            //hlbku w vypocitame z LP
            Double wDepth = Reconciliator.linearW(0, false, vDepth, w, u.getMaxD(), rootEdge, Reconciliator.getDirPath());
            wDepth = Reconciliator.round(wDepth);
            DL DLu = u.countDL(u.getMaxD(), vDepth, wDepth);

            //v praci binarne vyhladavanie, tu len prechod cez podintervaly
            for (int j = w.intervalsSize() - 1; j >= 0; j--) {
                Interval wInterval = w.getInterval(j);
                //ak sa rovna wInterval.getMaxD(), moze sa namapovat aj do nizsieho intervalu
                if(wInterval.getMaxDepth() == wDepth && j > 0) continue;
                //wDepth patri do intervalu
                if(wInterval.getMinDepth() <= wDepth && wInterval.getMaxDepth() >= wDepth){
                    DL minDL = new DL(DLu.getDuplication() + vInterval.getDl().getDuplication() + wInterval.getDl().getDuplication(),
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
        int lossesOnPathBetweenRoots = RootedExactTree.pathToRoot(u.getBearingNode(u.getMaxD())).size() - 1;

        //nastav celkove DL korena
        u.setTotalDL(new DL(bestDL.getDuplication(), bestDL.getLoss()+ lossesOnPathBetweenRoots));
    }

    private RootedIntervalNode enroot(UnrootedNode uNode, Edge sourceEdge){
        RootedIntervalNode rNode = new RootedIntervalNode(uNode.getName());

        if(uNode.getEdges().size() != 3 && uNode.getEdges().size() != 1){
            System.err.println("Zly pocet hran pre vrchol " + uNode.getEdges());
        }

        for (Edge e: uNode.getEdges()) {
            if(e.equals(sourceEdge)) continue;

            //assert rNode.getLeft() == null || rNode.getRight() == null;

            UnrootedNode otherNode = e.otherNode(uNode);
            if(otherNode == null){
                System.err.println("Chyba v subore G.tree");
            }

            RootedIntervalNode child = enroot(otherNode, e);
            child.setParent(rNode);
            child.setMinL(e.getMinLength());
            child.setMaxL(e.getMaxL());

            if(rNode.getLeft() == null){
                rNode.setLeft(child);
            } else if(rNode.getRight() == null){
                rNode.setRight(child);
            } else {
                System.err.println("Node " + uNode.getName() + " doesnt contain source edge.");
            }
        }

        if(rNode.getLeft() != null && rNode.getRight() == null){
            System.err.println("There are 2 edges for node " + rNode.getName());
        }

        if(rNode.getLeft() != null && rNode.getRight() != null){
            rNode.setLcaS(speciesTree.lca(rNode.getLeft().getLcaS(), rNode.getRight().getLcaS()));
            if(rNode.getLeft().getName().compareTo(rNode.getRight().getName()) < 0){
                rNode.setName(rNode.getLeft().getName() + rNode.getRight().getName());
            } else {
                rNode.setName(rNode.getRight().getName() + rNode.getLeft().getName());
            }
        } else {
            rNode.setLcaS(speciesTree.findNode(leafMap.get(rNode.getName())));
            if(rNode.getLcaS() == null){
                System.err.println("Zly nazov listu " + rNode.getName());
            }
        }

        return rNode;
    }

    public RootedIntervalNode getRoot() {
        return root;
    }

    static boolean upward(RootedIntervalNode v){
        if(v.getLeft() == null){
            v.setMinD(v.getLcaS().getDepth());
            v.setMaxD(v.getLcaS().getDepth());
            return true;
        }
        if(!upward(v.getLeft()) || !upward(v.getRight())) return false;
        double minD = Math.max(v.getLeft().getMinD() - v.getLeft().getMaxL(),
                v.getRight().getMinD() - v.getRight().getMaxL());
        double maxD = Math.min(v.getLeft().getMaxD() - v.getLeft().getMinL(),
                v.getRight().getMaxD() - v.getRight().getMinL());
        maxD = Math.min(maxD, v.getLcaS().getDepth());

        if(maxD < minD) {
            return false;
        }

        v.setMinD(minD);
        v.setMaxD(maxD);
        return true;
    }

    static void downward(RootedIntervalNode v){
        if(v.getLeft() == null) return;
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

    private static void downwardSetSolution(RootedIntervalNode v) {
        if(v.getLeft() == null) return;
        v.getLeft().setDepth(Math.min(v.getDepth() + v.getLeft().getMaxL(), v.getLeft().getMaxD()));
        v.getRight().setDepth(Math.min(v.getDepth() + v.getRight().getMaxL(), v.getRight().getMaxD()));
        downwardSetSolution(v.getLeft());
        downwardSetSolution(v.getRight());
    }
}