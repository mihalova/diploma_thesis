import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.TreeMap;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

class Reconciliator {
    private static String dirPath;
    private Double tolerance, step;
    private RootedExactTree S;
    private UnrootedTree G;
    private TreeMap<String, String> leafMap;
    //ArrayList<RootedIntervalNode> solutions = new ArrayList<>();


    public Reconciliator(String dirPath, Double tolerance, Double step) {
        this.dirPath = dirPath;
        this.tolerance = tolerance;
        if (step != null)
            this.step = step;
        else
            this.step = 0.1;
    }

    public void runReconciliation() {
        loadTreesFromFiles();
        reconcile();
    }

    public static String getDirPath() {
        return dirPath;
    }

    private void loadTreesFromFiles() {
        //File output = new File(dirPath + "/stats.txt");
        /*RootedExactTree Sreal = Parser.parseRootedTree(dirPath,
                "Sreal_newick_outgroupless.tree");*/
        try {
            S = Parser.parseRootedTree(dirPath, "S_newick.tree");
        } catch (Exception exc) {
            System.err.println("BAD S");
            return;
        }
        //rooted exact G
        /*RootedExactTree Greal = Parser.parseRootedTree(dirPath, "Greal_newick.tree");
        if (!S.checkEqualTopology(Sreal)) {
            System.err.println("BAD S");
            return;
        }*/

        //unrooted G
        try {
            File GtreeFile = new File(dirPath + "/G_newick.tree");
            G = Parser.parseUnrootedTree(GtreeFile, tolerance);
        } catch (Exception exc) {
            System.err.println("BAD G");
            return;
        }
        //leaf mapping
        leafMap = G.getLeafMap();
    }

    int i = 0;

    private void reconcile() {
        for (Edge e : G.getEdges()) {
            //vytvorim dva podstromy od hrany, na ktorej zakorenujem
            RootedIntervalTree uTree = new RootedIntervalTree(e.getU(), e, S, leafMap);
            RootedIntervalTree vTree = new RootedIntervalTree(e.getV(), e, S, leafMap);
            RootedIntervalNode u = uTree.getRoot();
            RootedIntervalNode v = vTree.getRoot();
            double intervalDifference = (e.getMaxLength() - e.getMinLength()) / 2;
            //vytvorim root na hrane s krokom
            for (double intervalNode1 = 0; intervalNode1 <= e.getMinLength(); intervalNode1 += step) { //TODO krajne pripady - root v node?
                double intervalNode2 = e.getMinLength() - intervalNode1;
                u.setMinL(intervalNode1);
                u.setMaxL(intervalNode1 + intervalDifference);
                v.setMinL(intervalNode2);
                v.setMaxL(intervalNode2 + intervalDifference);
                RootedIntervalNode root = new RootedIntervalNode("root");
                root.setLeft(u);
                root.setRight(v);
                root.setLcaS(RootedExactTree.lca(u.getLcaS(), v.getLcaS()));

                RootedIntervalTree tree = new RootedIntervalTree(root, S, leafMap);
                tree.upward(root);
                tree.downward(root);

                result = "";
                System.out.println(getTree(root));

                tree.countDL(root);
                System.out.println("Duplications: " + tree.getTotalDL().getDuplication());
                System.out.println("Deletions: " + tree.getTotalDL().getLoss());
            }
        }
    }

    String result;

    private String getTree(RootedIntervalNode n) {
        if (n.getLeft() != null) {
            getTree(n.getLeft());
            getTree(n.getRight());
        }
        result += n.getName() + " " + n.getMinL() + " " + n.getMaxL() + " " + n.getMinD() + " " + n.getMaxD() + "\n";
        return result;
    }

    /* private void reconcileOLD() {
         DL minDL = new DL(Integer.MAX_VALUE / 2, Integer.MAX_VALUE / 2);
         for (Edge e : G.getEdges()) {
             //vytvorim dva podstromy od noveho root
             RootedIntervalTree uTree = new RootedIntervalTree(e.getU(), e, S, leafMap);
             RootedIntervalTree vTree = new RootedIntervalTree(e.getV(), e, S, leafMap);
             if (!RootedIntervalTree.upward(uTree.getRoot())) {
                 //System.err.println("Zakorenenie na hrane " + e.getU().getName() + "," +
                 //	e.getV().getName() + " nie je mozne");
                 continue;
             }
             if (!RootedIntervalTree.upward(vTree.getRoot())) {
                 //System.err.println("Zakorenenie na hrane " + e.getU().getName() + "," +
                 //e.getV().getName() + " nie je mozne");
                 continue;
             }

             RootedIntervalNode u = uTree.getRoot();
             RootedIntervalNode v = vTree.getRoot();
             RootedExactNode lcaS = RootedExactTree.lca(u.getLcaS(), v.getLcaS());

             Double minDR = linear(2, true, u, v, lcaS, e, dirPath);
             if (minDR == null) continue;
             minDR = round(minDR);
             Double maxDR = round(linear(2, false, u, v, lcaS, e, dirPath));
             Double minDU = round(linear(0, true, u, v, lcaS, e, dirPath));
             Double maxDU = round(linear(0, false, u, v, lcaS, e, dirPath));
             Double minDV = round(linear(1, true, u, v, lcaS, e, dirPath));
             Double maxDV = round(linear(1, false, u, v, lcaS, e, dirPath));

             RootedIntervalNode root = new RootedIntervalNode("root");
             root.setMinD(minDR);
             root.setMaxD(maxDR);
             root.setDepth(maxDR);
             u.setMinD(minDU);
             u.setMaxD(maxDU);
             v.setMinD(minDV);
             v.setMaxD(maxDV);
             root.setLeft(u);
             root.setRight(v);
             RootedIntervalTree.downward(u);
             RootedIntervalTree.downward(v);

             RootedIntervalTree.findMostParsimoniousDepths(root, e);
             if (root.getTotalDL().getSum() < minDL.getSum()) {
                 minDL = new DL(root.getTotalDL().getDuplication(), root.getTotalDL().getLoss());
             }
             System.out.println("Total DL of solution: (" + root.getTotalDL().getDuplication() + "," + root.getTotalDL().getLoss() + ")");

             solutions.add(root);
         }

         if (solutions.isEmpty()) {
             System.err.println("No solution found");
             return;
         }

         //riesenia s najmensim DL
         ArrayList<RootedIntervalNode> parsimonySolutions = new ArrayList<>();
         for (RootedIntervalNode solution : solutions) {
             DL solDL = solution.getTotalDL();
             if (solDL.getSum() == minDL.getSum()) {
                 parsimonySolutions.add(solution);
             }
         }

         System.out.println("Number of the most parsimonious solutions: " + parsimonySolutions.size());
         System.out.println("Minimal DL: (" + minDL.getDuplication() + "," + minDL.getLoss() + ")");
         System.out.println();

         //ak chceme len riesenia s minimalnym DL:
         solutions = parsimonySolutions;
     }
 */
    private Double linear(Integer objective, boolean min, RootedIntervalNode u,
                          RootedIntervalNode v, RootedExactNode lcaS, Edge e, String path) {
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
    public static Double linear2(Integer objective, boolean min, RootedIntervalNode v,
                                 RootedIntervalNode w, double rootDepth, Edge e, String path) {
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
                                 RootedIntervalNode w, double rootDepth, Edge e, String path) {
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
