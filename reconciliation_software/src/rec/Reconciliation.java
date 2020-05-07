package rec;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Reconciliation {
    public static String directoryPath;

    public static void main(String[] args){
        if (args.length != 3 || args[1].length()!=1 || "pdra".indexOf(args[1].charAt(0))<0) {
            throw new IllegalArgumentException("Start the program with 3 arguments: [path] " +
                    "[interval_method p/d/r/a] [method_param]");
        }

        directoryPath = args[0];
        String method = args[1];
        double methodParam = Double.parseDouble(args[2]);

        RootedTree S;
        try {
            S = Parser.parseRootedTree(directoryPath, "S_newick.tree");
        } catch (Exception exc) {
            System.out.println("Bad S");
            return;
        }

        UnrootedTree G;
        try {
            G = Parser.parseUnrootedTree(directoryPath, "G_newick.tree");
        } catch (Exception exc) {
            System.out.println("Bad G");
            return;
        }




    }

    // zaokruhlenie double na 7 desatinnych miest
    static Double round(Double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(7, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
