package Software;

import java.util.*;

public class Main {

    public static double EPSILON;
    public static void main(String[] args) {
            Loader load = new Loader(args);
            Object[] loadArgs = load.getLoadArgs();
            if (loadArgs == null) {
                System.err.println("Start the program with correct gene tree and species tree.");
            } else {
                EPSILON = (Double) loadArgs[7];
                Reconciliator rec = new Reconciliator((RootedExactTree) loadArgs[0],
                        (RootedIntervalTree) loadArgs[1], (UnrootedTree) loadArgs[2],
                        (Double) loadArgs[3], (Boolean) loadArgs[4]);
                List<RootedIntervalTree> sol = rec.getSolutions();
                if (!sol.isEmpty()) {
                    Printer print = new Printer((String) loadArgs[5], (String) loadArgs[6], sol);
                } else {
                    System.out.println("No solution for given gene tree.");
                }
            }
        }
}


