package Test;

import Software.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReconciliatorTest {

    @Test
    public void getIntervalsTest1() {
        Loader load = new Loader(new String[]{"-S", "data_test/find_root/tree_1/S_newick.tree",
                "-G", "data_test/find_root/tree_1/G_newick.tree",
                "-M", "data_test/find_root/tree_1/S_map.smap"});
        Object[] loadArgs = load.getLoadArgs();
        Reconciliator rec = new Reconciliator((RootedExactTree) loadArgs[0],
                (RootedIntervalTree) loadArgs[1], (UnrootedTree) loadArgs[2],
                (Double) loadArgs[3], (Boolean) loadArgs[4]);
        Main.EPSILON = (Double) loadArgs[7];
        System.out.println("Step: 1; Step is bigger than difference/2");
        intervals(rec, (UnrootedTree) loadArgs[2]);
    }

    @Test
    public void getIntervalsTest2() {
        Loader load = new Loader(new String[]{"-S", "data_test/find_root/tree_2/S_newick.tree",
                "-G", "data_test/find_root/tree_2/G_newick.tree",
                "-M", "data_test/find_root/tree_2/S_map.smap"});
        Object[] loadArgs = load.getLoadArgs();
        Reconciliator rec = new Reconciliator((RootedExactTree) loadArgs[0],
                (RootedIntervalTree) loadArgs[1], (UnrootedTree) loadArgs[2],
                (Double) loadArgs[3], (Boolean) loadArgs[4]);
        Main.EPSILON = (Double) loadArgs[7];
        System.out.println("Step: 1; Step is bigger that difference/2");
        intervals(rec, (UnrootedTree) loadArgs[2]);
    }

    @Test
    public void getIntervalsTest3() {
        Loader load = new Loader(new String[]{"-S", "data_test/find_root/tree_3/S_newick.tree",
                "-G", "data_test/find_root/tree_3/G_newick.tree",
                "-M", "data_test/find_root/tree_3/S_map.smap",});
        Object[] loadArgs = load.getLoadArgs();
        Reconciliator rec = new Reconciliator((RootedExactTree) loadArgs[0],
                (RootedIntervalTree) loadArgs[1], (UnrootedTree) loadArgs[2],
                (Double) loadArgs[3], (Boolean) loadArgs[4]);
        Main.EPSILON = (Double) loadArgs[7];
        System.out.println("Step: 1; Difference is smaller than step");
        intervals(rec, (UnrootedTree) loadArgs[2]);
    }

    public void intervals(Reconciliator rec, UnrootedTree G) {
        for (Edge e : G.getEdges()) {
            List<Four<Double, Double, Double, Double>> intervals = rec.getIntervalsForTesting(e, 1.0);
            System.out.println("Edge: " + e.getU().getName() + ", " + e.getV().getName());
            for (Four t : intervals) {
                System.out.println("Interval 1: " + t.getFirst() + " " + t.getSecond() + "; Interval 2: " + t.getThird() + " " + t.getFourth());
                assertTrue((double) t.getFirst() + (double) t.getThird() >= e.getMinLength());
                assertTrue((double) t.getSecond() + (double) t.getFourth() <= e.getMaxLength());
            }
        }
    }
}