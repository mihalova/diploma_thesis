import java.util.ArrayList;

public class Main {

    public static String dirPath;
    public static Double tolerance;

    public static void main(String[] args) {
        loadArguments(args);

        Reconciliator rec = new Reconciliator(dirPath, tolerance);
        rec.runReconciliation();

        Printer print = new Printer(dirPath, rec.solutions);
        print.runPrinter();
    }

    private static void loadArguments(String[] args) {
        if (args.length == 1) {
            dirPath = args[0];
            tolerance = null;
        } else if (args.length == 2) {
            dirPath = args[0];
            tolerance = Double.valueOf(args[1]);
        } else {
            throw new IllegalArgumentException("Start the program with arguments: [path] and [tolerance (optional)]");
        }
    }

}
