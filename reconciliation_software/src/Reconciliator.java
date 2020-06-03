import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

class Reconciliator {
    public static double EPSILON = 1e-6;  // epsilon used in interval comparison
    public static String dirPath;

    private static void writeStatus(File output, String message) {
        FileWriter fw;
        try {
            fw = new FileWriter(output);
            fw.write(message);
            fw.write(System.lineSeparator());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Double tolerance;
        if (args.length == 1) {
            dirPath = args[0];
            tolerance = null;
        } else if (args.length == 2) {
            dirPath = args[0];
            tolerance = Double.valueOf(args[1]);
        } else {
            throw new IllegalArgumentException("Start the program with arguments: [path] and [tolerance (optional)]");
        }


        File output = new File(dirPath + "/stats.txt");
        RootedExactTree Sreal = Parser.parseRootedTree(dirPath,
                "Sreal_newick_outgroupless.tree");
        RootedExactTree S;
        try {
            S = Parser.parseRootedTree(dirPath, "S_newick.tree");
        } catch (Exception exc) {
            writeStatus(output, "BAD S");
            return;
        }
        //rooted exact G
        RootedExactTree Greal = Parser.parseRootedTree(dirPath, "Greal_newick.tree");
        if (!S.checkEqualTopology(Sreal)) {
            writeStatus(output, "BAD S");
            return;
        }

        //unrooted G with leaf mapping
        File GtreeFile = new File(dirPath + "/G_newick.tree");
        UnrootedTree G = Parser.parseUnrootedTree(GtreeFile, tolerance);
        TreeMap<String, String> leafMap = G.getLeafMap();

        ArrayList<RootedIntervalNode> solutions = new ArrayList<>();
        DL minDL = new DL(Integer.MAX_VALUE/2, Integer.MAX_VALUE/2);
        for (Edge e : G.getEdges()) {
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


            // takto sa to robilo v experimentoch - spustil sa linearny program, ktory maximalizoval sucet novych hlbok u a v
/*
            // "najnizsie riesenie" - koren sa mapuje najnizsie ako sa da (do maxDR)
            //hladame najnizsie mapovania u a v:
            double maxDUfinal;
            double maxDVfinal;

            try {
                LpSolve solver = LpSolve.makeLp(0, 2);
                //solver.setOutputfile(dirPath + "/lpsolve2_output.txt");

                solver.strAddConstraint("1 0", LpSolve.GE, u.getMinD());
                solver.strAddConstraint("1 0", LpSolve.LE, u.getMaxD());

                solver.strAddConstraint("0 1", LpSolve.GE, v.getMinD());
                solver.strAddConstraint("0 1", LpSolve.LE, v.getMaxD());

                solver.strAddConstraint("1 1", LpSolve.GE, e.getMinL() + 2 * maxDR);
                solver.strAddConstraint("1 1", LpSolve.LE, e.getMaxL() + 2 * maxDR);

                solver.strAddConstraint("1 0", LpSolve.GE, maxDR);
                solver.strAddConstraint("0 1", LpSolve.GE, maxDR);

                solver.setBounds(1, -solver.getInfinite(), solver.getInfinite());
                solver.setBounds(2, -solver.getInfinite(), solver.getInfinite());

                solver.strSetObjFn("1 1");
                solver.setMaxim();

                solver.solve();

                double[] var = solver.getPtrVariables();

                maxDUfinal = var[0];
                maxDVfinal = var[1];

                solver.deleteLp();
            }

            u.setDepth(maxDUfinal);
            v.setDepth(maxDVfinal);
*/

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
            if(root.getTotalDL().getSum() < minDL.getSum()){
                minDL = new DL(root.getTotalDL().getDuplication(), root.getTotalDL().getLoss());
            }
            System.out.println("Total DL of solution: ("+ root.getTotalDL().getDuplication() +"," + root.getTotalDL().getLoss() + ")");

            solutions.add(root);
        }

        if (solutions.isEmpty()) {
            writeStatus(output, "No solution found");
            return;
        }

        //riesenia s najmensim DL
        ArrayList<RootedIntervalNode> parsimonySolutions = new ArrayList<>();
        for(RootedIntervalNode solution: solutions){
            DL solDL = solution.getTotalDL();
            if(solDL.getSum() == minDL.getSum()){
                parsimonySolutions.add(solution);
            }
        }

        System.out.println("Number of the most parsimonious solutions: " + parsimonySolutions.size());
        System.out.println("Minimal DL: ("+ minDL.getDuplication() +"," + minDL.getLoss() + ")");
        System.out.println();

        //ak chceme len riesenia s minimalnym DL:
        solutions = parsimonySolutions;

        for (int i = 0; i < solutions.size(); i++) {
            FileWriter fw;
            FileWriter fw2;
            try {
                fw = new FileWriter(new File(dirPath + "/solution_" + i + ".tree"));
                printToTreeFile(fw, solutions.get(i));
                fw2 = new FileWriter(new File(dirPath + "/solution_" + i + ".depths"));
                fw2.write(solutions.get(i).getLeft().getName() + " " +
                        solutions.get(i).getRight().getName());
                fw2.write(System.lineSeparator());
                printToDepthsFile(fw2, solutions.get(i));
                fw.close();
                fw2.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        boolean goodRoot = false;
        int i = 0;
        String filename = dirPath + "/solution_" + i + ".depths";
        File f = new File(filename);
        while (f.exists()) {
            try {
                Scanner s = new Scanner(f);
                String[] firstLine = s.nextLine().split("\\s+");
                String leftReal = Greal.getRoot().getLeft().getName();
                String rightReal = Greal.getRoot().getRight().getName();
                if ((leftReal.equals(firstLine[0]) && rightReal.equals(firstLine[1])) ||
                        (leftReal.equals(firstLine[1]) && rightReal.equals(firstLine[0])))
                {
                    goodRoot = true;
                    // mapovanie Greal na Sreal
                    /*
                    if (Greal.mapOn(Sreal) == null) {
                    	System.err.println("Run " + r + ": reconciliation of Greal and " +
                    		"Sreal is not possible");
                    	return;
                    }
                    */

                    int numberOfNodes = 0;
                    int matchedNodes = 0;
                    System.out.println("#[min depth] [Greal depth] [max depth]");
                    while (s.hasNextLine()) {
                        String[] line = s.nextLine().split("\\s+");
                        RootedExactNode realNode = Greal.findNode(line[0]);
                        double minD = Double.parseDouble(line[1]);
                        double maxD = Double.parseDouble(line[2]);
                        System.out.print(line[0] + " " + minD + " " + realNode.getDepth() + " " + maxD);
                        if (realNode.getLeft() != null) {
                            if (realNode.getDepth() >= minD - EPSILON && realNode.getDepth() <= maxD + EPSILON) {
                                matchedNodes++;
                                System.out.print(" GOOD");
                            } else {
                                System.out.print(" BAD");
                            }
                            numberOfNodes++;
                        }
                        System.out.println();
                    }
                    double percent = ((double)matchedNodes) * 100.0 / numberOfNodes;
                    writeStatus(output, "GOOD: " + matchedNodes + " / " + numberOfNodes + " = " + percent + " %; sol " + solutions.size());
                }
                s.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            i++;
            filename = dirPath + "/solution_" + i + ".depths";
            f = new File(filename);
        }
        if (!goodRoot) {
            writeStatus(output, "BAD ROOT");
        }
    }

    private static void printToTreeFile(FileWriter fw, RootedIntervalNode node) throws IOException {
        if (node.getLeft() == null) return;
        fw.write(node.getName() + " " + node.getLeft().getName() + " " +
                (node.getLeft().getDepth() - node.getDepth()));
        fw.write(System.lineSeparator());
        fw.write(node.getName() + " " + node.getRight().getName() + " " +
                (node.getRight().getDepth() - node.getDepth()));
        fw.write(System.lineSeparator());
        printToTreeFile(fw, node.getLeft());
        printToTreeFile(fw, node.getRight());
    }

    private static void printToDepthsFile(FileWriter fw, RootedIntervalNode node) throws IOException {
        if (node == null) return;
        fw.write(node.getName() + " " + node.getMinD() + " " + node.getMaxD());
        fw.write(System.lineSeparator());
        printToDepthsFile(fw, node.getLeft());
        printToDepthsFile(fw, node.getRight());
    }

    private static Double linear(Integer objective, boolean min, RootedIntervalNode u,
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
            solver.strAddConstraint("1 1 -2", LpSolve.LE, e.getMaxL());

            //hlbky u a v musia byt vacsie alebo rovne ako hlbka korena (plus epsilon aby sa algoritmy nekazili)
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
            }
            else {
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
        }
        catch (LpSolveException ex) {
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
            solver.strAddConstraint("1 1", LpSolve.GE, e.getMinLength() + 2*rootDepth);
            solver.strAddConstraint("1 1", LpSolve.LE, e.getMaxL() + 2*rootDepth);

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
            }
            else {
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
        }
        catch (LpSolveException ex) {
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
            solver.strAddConstraint("1", LpSolve.GE, e.getMinLength() - vDepth + 2*rootDepth);
            solver.strAddConstraint("1", LpSolve.LE, e.getMaxL() - vDepth + 2*rootDepth);

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
            }
            else {
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
        }
        catch (LpSolveException ex) {
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
}
