package Software;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static Software.Main.EPSILON;

public class Reconciliator {
    private List<RootedIntervalTree> solutions = new ArrayList<>();

    public Reconciliator(RootedExactTree S, RootedIntervalTree G_rooted, UnrootedTree G_unrooted, Double step, boolean countLossesAboveRoot) {
        prepareForReconciliation(S, G_rooted, G_unrooted, step, countLossesAboveRoot);
    }

    public List<RootedIntervalTree> getSolutions() {
        return solutions;
    }

    //for testing
    public List<Four<Double, Double, Double, Double>> getIntervalsForTesting(Edge e, Double step) {
        return getIntervals(e, step);
    }


    private void prepareForReconciliation(RootedExactTree S, RootedIntervalTree G_rooted, UnrootedTree G_unrooted, Double step, boolean countLossesAboveRoot) {
        if (G_rooted == null) {
            for (Edge e : G_unrooted.getEdges()) {
                //vytvorim dva podstromy od hrany, na ktorej zakorenujem
                List<Four<Double, Double, Double, Double>> intervals = getIntervals(e, step);
                for (Four<Double, Double, Double, Double> t : intervals) {
                    //ak nad for, zle uklada do listu
                    RootedIntervalTree uTree = new RootedIntervalTree(e.getU(), e, S, G_unrooted.getLeafMap());
                    RootedIntervalTree vTree = new RootedIntervalTree(e.getV(), e, S, G_unrooted.getLeafMap());
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
                    RootedIntervalTree tree = new RootedIntervalTree(root, S, G_unrooted.getLeafMap());
                    reconcile(tree, countLossesAboveRoot);
                }
            }
        } else {
            reconcile(G_rooted, countLossesAboveRoot);
        }
    }


    private void reconcile(RootedIntervalTree tree, boolean countLossesAboveRoot) {
        RootedIntervalNode root = tree.getRoot();
        boolean hasSolution = tree.upward(root);
        if (hasSolution) {
            tree.downward(root);
            tree.computeLevel(root);
            tree.setCountLossesAboveRoot(countLossesAboveRoot);
            //System.out.println("\nTree:");
            tree.setScore(tree.countDL(root));
            if (!solutions.isEmpty()) {
                if (solutions.get(0).getScore().getSum() == tree.getScore().getSum()) {
                    solutions.add(tree);
                } else if (solutions.get(0).getScore().getSum() > tree.getScore().getSum()) {
                    solutions.clear();
                    solutions.add(tree);
                }
            } else {
               solutions.add(tree);
            }
        }
    }

    List<Four<Double, Double, Double, Double>> getIntervals(Edge e, double step) {
        //prerequsities
        List<Four<Double, Double, Double, Double>> intervals = new ArrayList<>();
        double totalMaxLength = e.getMaxLength();
        double totalMinLength = e.getMinLength();
        double difference = totalMaxLength - totalMinLength;
        double intervalSize;
        if(step > difference/2)
            intervalSize = difference/2;
        else
            intervalSize = step;
        double minLengthLeft = totalMinLength;
        double maxLengthLeft = totalMaxLength - intervalSize;
        double minLengthRight = 0.0;
        double maxLengthRight = intervalSize;


        //krajne moznosti (root tesne nad node)
        intervals.add(new Four<>(EPSILON, EPSILON, totalMinLength-EPSILON, totalMaxLength - EPSILON));
        intervals.add(new Four<>(totalMinLength-EPSILON, totalMaxLength - EPSILON, EPSILON, EPSILON));
        //intervaly s krokom
        while (maxLengthRight < totalMaxLength && maxLengthLeft > 0.0 && step != 0.0) {
            intervals.add(new Four<>(minLengthLeft, maxLengthLeft, minLengthRight == 0.0 ? EPSILON : minLengthRight, maxLengthRight));
            minLengthLeft -= step;
            if (minLengthLeft <= 0.0)
                minLengthLeft = EPSILON;
            maxLengthLeft -= step;
            minLengthRight += step;
            maxLengthRight += step;
        }
        return intervals;
    }

    // zaokruhlenie double na 7 desatinnych miest
    static Double round(Double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(6, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
