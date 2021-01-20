package Test;

import Software.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReconciliatorTest {

    @Test
    public void getIntervalsTest() {
        for (int i = 1; i < 3; i++) {
            Reconciliator rec = new Reconciliator("data_test/find_root/tree_" + i, 0.5, 1.0);
            System.out.println("\nTree "+i);
            intervals(rec);
        }
    }

    public void intervals(Reconciliator rec) {
        UnrootedTree G = rec.getGtreeForTesting();
        for (Edge e : G.getEdges()) {
            List<Four<Double, Double, Double, Double>> intervals = rec.getIntervalsForTesting(e);
            System.out.println("Edge: " + e.getU().getName() + ", " + e.getV().getName());
            for (Four t : intervals) {
                System.out.println("Interval 1: " + t.getFirst() + " " + t.getSecond() + "; Interval 2: " + t.getThird() + " " + t.getFourth());
                assertEquals(e.getMinLength(), (double) t.getFirst() + (double) t.getThird());
                assertEquals(e.getMaxLength(), (double) t.getSecond() + (double) t.getFourth());
            }
        }
    }
}