package Software;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Reconciliator {
    private Double step;
    private RootedExactTree S;
    private UnrootedTree G;
    private TreeMap<String, String> leafMap;
    private List<Pair<DL, RootedIntervalNode>> solutions = new ArrayList<>();
    DL minDL = new DL(Integer.MAX_VALUE / 2, Integer.MAX_VALUE / 2);

    public Reconciliator(RootedExactTree S, UnrootedTree G, Double step) {
        this.S = S;
        this.G = G;
        if (step != null)
            this.step = step;
        else
            this.step = 0.5;
        leafMap = G.getLeafMap();
    }

    public List<Pair<DL, RootedIntervalNode>> getSolutions(){
        reconcile();
        return solutions;
    }

    //for testing
    public UnrootedTree getGtree(){
        return G;
    }
/*
    //for testing
    public List<Pair<String, String>> getSmappingFromFile(){
        loadSpeciesMappingFromFile();
        return Smapping;
    }

    //for testing
    public RootedExactTree getStreeFromFile(){
        loadSpeciesTreeFromFile();
        return S;
    }*/


    public List<Four<Double, Double, Double, Double>>getIntervalsForTesting(Edge e){
        return getIntervals(e);
    }

    private void reconcile() {
        for (Edge e : G.getEdges()) {
            //vytvorim dva podstromy od hrany, na ktorej zakorenujem
            List<Four<Double, Double, Double, Double>> intervals = getIntervals(e);
            for (Four<Double, Double, Double, Double> t : intervals) {
                //ak nad for, zle uklada do listu
                RootedIntervalTree uTree = new RootedIntervalTree(e.getU(), e, S, leafMap);
                RootedIntervalTree vTree = new RootedIntervalTree(e.getV(), e, S, leafMap);
                RootedIntervalNode u = uTree.getRoot();
                RootedIntervalNode v = vTree.getRoot();
                //
                u.setMinL(t.getFirst());
                u.setMaxL(t.getSecond());
                v.setMinL(t.getThird());
                v.setMaxL(t.getFourth());
                RootedIntervalNode root = new RootedIntervalNode("root");
                root.setLeft(u);
                root.setRight(v);
                root.setLcaS(RootedExactTree.lca(u.getLcaS(), v.getLcaS()));
                RootedIntervalTree tree = new RootedIntervalTree(new RootedIntervalNode("root"), S, leafMap);
                tree.upward(root);
                tree.downward(root);
                tree.computeLevel(root);
                System.out.println("\nTree:");
                DL score = tree.countDL(root);
                if (!solutions.isEmpty()) {
                    if (solutions.get(0).getFirst().getSum() == score.getSum()) {
                        solutions.add(new Pair<>(score, root));
                    } else if (solutions.get(0).getFirst().getSum() > score.getSum()) {
                        solutions.clear();
                        solutions.add(new Pair<>(score, root));
                    }
                } else {
                    solutions.add(new Pair<>(score, root));
                }
            }
        }

        RootedIntervalTree tree = new RootedIntervalTree(new RootedIntervalNode("root"), S, leafMap);
        System.out.println("\n\n\n");
        DL score = tree.countDL(solutions.get(0).getSecond());
    }

    List<Four<Double, Double, Double, Double>> getIntervals(Edge e) {
        //prerequsities
        List<Four<Double, Double, Double, Double>> intervals = new ArrayList<>();
        double totalMaxDepth = e.getMaxLength();
        double totalMinDepth = e.getMinLength();
        double minDepthLeft = totalMinDepth;
        double maxDepthLeft = totalMaxDepth - step;
        double minDepthRight = 0.0;
        double maxDepthRight = step;
        double difference = totalMaxDepth - totalMinDepth;
        double differenceDividedByStep = difference / step;
        boolean differenceIsDivisibleByStep = differenceDividedByStep == Math.floor(differenceDividedByStep) && !Double.isInfinite(differenceDividedByStep);

        //krajne moznosti (root tesne nad node)
        intervals.add(new Four<>(0.0, 0.0, totalMinDepth, totalMaxDepth));
        intervals.add(new Four<>(totalMinDepth, totalMaxDepth, 0.0, 0.0));
        //intervaly s krokom
        while (maxDepthRight < totalMaxDepth && maxDepthLeft > 0.0) {
            intervals.add(new Four<>(minDepthLeft, maxDepthLeft, minDepthRight, maxDepthRight));
            if (difference == step || !differenceIsDivisibleByStep)
                intervals.add(new Four<>(minDepthRight, maxDepthRight, minDepthLeft, maxDepthLeft));
            minDepthLeft -= step;
            if (minDepthLeft < 0.0)
                minDepthLeft = 0.0;
            maxDepthLeft -= step;
            minDepthRight += step;
            maxDepthRight += step;
        }
        return intervals;
    }

    private String printTree(RootedIntervalNode n) {
        String result ="";
        if (n.getLeft() != null) {
            result = printTree(n.getLeft()) + printTree(n.getRight());
        }
        result += n.getName() + " " + n.getMinL() + " " + n.getMaxL() + " " + n.getMinD() + " " + n.getMaxD() + "\n";
        return result;
    }

    /* private void reconcileOLD() {
         Software.DL minDL = new Software.DL(Integer.MAX_VALUE / 2, Integer.MAX_VALUE / 2);
         for (Software.Edge e : G.getEdges()) {
             //vytvorim dva podstromy od noveho root
             Software.RootedIntervalTree uTree = new Software.RootedIntervalTree(e.getU(), e, S, leafMap);
             Software.RootedIntervalTree vTree = new Software.RootedIntervalTree(e.getV(), e, S, leafMap);
             if (!Software.RootedIntervalTree.upward(uTree.getRoot())) {
                 //System.err.println("Zakorenenie na hrane " + e.getU().getName() + "," +
                 //	e.getV().getName() + " nie je mozne");
                 continue;
             }
             if (!Software.RootedIntervalTree.upward(vTree.getRoot())) {
                 //System.err.println("Zakorenenie na hrane " + e.getU().getName() + "," +
                 //e.getV().getName() + " nie je mozne");
                 continue;
             }

             Software.RootedIntervalNode u = uTree.getRoot();
             Software.RootedIntervalNode v = vTree.getRoot();
             Software.RootedExactNode lcaS = Software.RootedExactTree.lca(u.getLcaS(), v.getLcaS());

             Double minDR = linear(2, true, u, v, lcaS, e, dirPath);
             if (minDR == null) continue;
             minDR = round(minDR);
             Double maxDR = round(linear(2, false, u, v, lcaS, e, dirPath));
             Double minDU = round(linear(0, true, u, v, lcaS, e, dirPath));
             Double maxDU = round(linear(0, false, u, v, lcaS, e, dirPath));
             Double minDV = round(linear(1, true, u, v, lcaS, e, dirPath));
             Double maxDV = round(linear(1, false, u, v, lcaS, e, dirPath));

             Software.RootedIntervalNode root = new Software.RootedIntervalNode("root");
             root.setMinD(minDR);
             root.setMaxD(maxDR);
             root.setDepth(maxDR);
             u.setMinD(minDU);
             u.setMaxD(maxDU);
             v.setMinD(minDV);
             v.setMaxD(maxDV);
             root.setLeft(u);
             root.setRight(v);
             Software.RootedIntervalTree.downward(u);
             Software.RootedIntervalTree.downward(v);

             Software.RootedIntervalTree.findMostParsimoniousDepths(root, e);
             if (root.getTotalDL().getSum() < minDL.getSum()) {
                 minDL = new Software.DL(root.getTotalDL().getDuplication(), root.getTotalDL().getLoss());
             }
             System.out.println("Total Software.DL of solution: (" + root.getTotalDL().getDuplication() + "," + root.getTotalDL().getLoss() + ")");

             solutions.add(root);
         }

         if (solutions.isEmpty()) {
             System.err.println("No solution found");
             return;
         }

         //riesenia s najmensim Software.DL
         ArrayList<Software.RootedIntervalNode> parsimonySolutions = new ArrayList<>();
         for (Software.RootedIntervalNode solution : solutions) {
             Software.DL solDL = solution.getTotalDL();
             if (solDL.getSum() == minDL.getSum()) {
                 parsimonySolutions.add(solution);
             }
         }

         System.out.println("Number of the most parsimonious solutions: " + parsimonySolutions.size());
         System.out.println("Minimal Software.DL: (" + minDL.getDuplication() + "," + minDL.getLoss() + ")");
         System.out.println();

         //ak chceme len riesenia s minimalnym Software.DL:
         solutions = parsimonySolutions;
     }
 */
    /*private Double linear(Integer objective, boolean min, Software.RootedIntervalNode u,
                          Software.RootedIntervalNode v, Software.RootedExactNode lcaS, Software.Edge e, String path) {
        try {
            //prvy stlpec je hlbka u, druhy je hlbka v, treti je hlbka root
            LpSolve solver = LpSolve.makeLp(0, 3);
            solver.setOutputfile(path + "/lpsolve_output.txt");

            //hlbky u a v patria do ich mozneho intervalu
            solver.strAddConstraint("1 0 0", LpSolve.GE, u.getMinD());
            solver.strAddConstraint("1 0 0", LpSolve.LE, u.getMaxD());
            solver.strAddConstraint("0 1 0", LpSolve.GE, v.getMinD());
            solver.strAddConstraint("0 1 0", LpSolve.LE, v.getMaxD());

            //hlbka korena je mensia ako hlbka lcaS
            solver.strAddConstraint("0 0 1", LpSolve.LE, lcaS.getDepth());

            //sucet vzdialenosti u a v od korena patri do intervalu dlzky korenovej hrany
            solver.strAddConstraint("1 1 -2", LpSolve.GE, e.getMinLength());
            solver.strAddConstraint("1 1 -2", LpSolve.LE, e.getMaxLength());

            //hlbky u a v musia byt vacsie alebo rovne ako hlbka korena (plus epsilon aby sa algoritmy nekazili)

            double EPSILON = 1e-6; // epsilon used in interval comparison
            solver.strAddConstraint("1 0 -1", LpSolve.GE, EPSILON);
            solver.strAddConstraint("0 1 -1", LpSolve.GE, EPSILON);

            //parametrom objective povieme, ktoru hlbku prave optimalizujeme
            int[] objectives = new int[3];
            objectives[objective] = 1;
            solver.strSetObjFn(objectives[0] + " " + objectives[1] + " " + objectives[2]);

            //nastavenie aby vsetky tri premenne dosahovali aj zaporne hodnoty (defaultne su kladne)
            solver.setBounds(1, -solver.getInfinite(), solver.getInfinite());
            solver.setBounds(2, -solver.getInfinite(), solver.getInfinite());
            solver.setBounds(3, -solver.getInfinite(), solver.getInfinite());

            //ci maximalizujeme alebo minimalizujeme podla boolean parametra min
            if (min) {
                solver.setMinim();
            } else {
                solver.setMaxim();
            }

            //vyriesenie rovnice, ak neexistuje riesenie, vrati sa null
            solver.solve();
            if (solver.getSolutioncount() == 0) {
                return null;
            }

            // inak vrati hodnotu hlbky, ktoru mame ako objective
            double[] var = solver.getPtrVariables();
            solver.deleteLp();
            return var[objective];
        } catch (LpSolveException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    //pouziva sa len v poslednom algorithme na orezavanie intervalov X[v] X[w] zhora
    public static Double linear2(Integer objective, boolean min, Software.RootedIntervalNode v,
                                 Software.RootedIntervalNode w, double rootDepth, Software.Edge e, String path) {
        try {
            //prvy stlpec je hlbka u, druhy je hlbka v, treti je hlbka root
            LpSolve solver = LpSolve.makeLp(0, 2);
            solver.setOutputfile(path + "/lpsolve2_output.txt");

            //hlbky u a v patria do ich mozneho intervalu
            solver.strAddConstraint("1 0", LpSolve.GE, v.getMinD());
            solver.strAddConstraint("1 0", LpSolve.LE, v.getMaxD());
            solver.strAddConstraint("0 1", LpSolve.GE, w.getMinD());
            solver.strAddConstraint("0 1", LpSolve.LE, w.getMaxD());


            //sucet vzdialenosti u a v od korena patri do intervalu dlzky korenovej hrany
            solver.strAddConstraint("1 1", LpSolve.GE, e.getMinLength() + 2 * rootDepth);
            solver.strAddConstraint("1 1", LpSolve.LE, e.getMaxLength() + 2 * rootDepth);

            //hlbky u a v musia byt vacsie alebo rovne ako hlbka korena (uz plati z predosleho volania LP)

            //parametrom objective povieme, ktoru hlbku prave optimalizujeme
            int[] objectives = new int[2];
            objectives[objective] = 1;
            solver.strSetObjFn(objectives[0] + " " + objectives[1]);

            //nastavenie aby vsetky premenne dosahovali aj zaporne hodnoty (defaultne su kladne)
            solver.setBounds(1, -solver.getInfinite(), solver.getInfinite());
            solver.setBounds(2, -solver.getInfinite(), solver.getInfinite());

            //ci maximalizujeme alebo minimalizujeme podla boolean parametra min
            if (min) {
                solver.setMinim();
            } else {
                solver.setMaxim();
            }

            //vyriesenie rovnice, ak neexistuje riesenie, vrati sa null
            solver.solve();
            if (solver.getSolutioncount() == 0) {
                return null;
            }

            // inak vrati hodnotu hlbky, ktoru mame ako objective
            double[] var = solver.getPtrVariables();
            solver.deleteLp();
            return var[objective];
        } catch (LpSolveException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    //pouziva sa len v poslednom algoritme na najdenie najhlbsieho namapovania vrchola w
    public static Double linearW(Integer objective, boolean min, double vDepth,
                                 Software.RootedIntervalNode w, double rootDepth, Software.Edge e, String path) {
        try {
            LpSolve solver = LpSolve.makeLp(0, 1);
            solver.setOutputfile(path + "/lpsolveV_output.txt");

            //hlbky w patria do ich mozneho intervalu
            solver.strAddConstraint("1", LpSolve.GE, w.getMinD());
            solver.strAddConstraint("1", LpSolve.LE, w.getMaxD());


            //sucet vzdialenosti v a w od korena patri do intervalu dlzky korenovej hrany
            solver.strAddConstraint("1", LpSolve.GE, e.getMinLength() - vDepth + 2 * rootDepth);
            solver.strAddConstraint("1", LpSolve.LE, e.getMaxLength() - vDepth + 2 * rootDepth);

            //hlbka v musi byt vacsia ako hlbka korena (malo by platit)
            solver.strAddConstraint("1", LpSolve.GE, rootDepth);

            //parametrom objective povieme, ktoru hlbku prave optimalizujeme
            int[] objectives = new int[1];
            objectives[0] = 1;
            solver.strSetObjFn(objectives[0] + "");

            //nastavenie aby premenne dosahovali aj zaporne hodnoty (defaultne su kladne)
            solver.setBounds(1, -solver.getInfinite(), solver.getInfinite());

            //ci maximalizujeme alebo minimalizujeme podla boolean parametra min
            if (min) {
                solver.setMinim();
            } else {
                solver.setMaxim();
            }

            //vyriesenie rovnice, ak neexistuje riesenie, vrati sa null
            solver.solve();
            if (solver.getSolutioncount() == 0) {
                return null;
            }

            // inak vrati hodnotu hlbky, ktoru mame ako objective
            double[] var = solver.getPtrVariables();
            solver.deleteLp();
            return var[objective];
        } catch (LpSolveException ex) {
            ex.printStackTrace();
        }
        return null;
    }
*/
    // zaokruhlenie double na 7 desatinnych miest
    static Double round(Double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(7, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /*private void writeStatus(File output, String message) {
        FileWriter fw;
        try {
            fw = new FileWriter(output);
            fw.write(message);
            fw.write(System.lineSeparator());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
