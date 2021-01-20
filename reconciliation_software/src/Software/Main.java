package Software;

public class Main {

    public static String dirPath;
    public static Double tolerance, step;

    public static void main(String[] args) {
        loadArguments(args);

        Reconciliator rec = new Reconciliator(dirPath, tolerance, step);
        rec.runReconciliation();

        /*Software.Printer print = new Software.Printer(dirPath, rec.solutions);
        print.runPrinter();*/
    }

    private static void loadArguments(String[] args) {
        if (args.length == 1) {
            dirPath = args[0];
        } else if (args.length == 2) {
            dirPath = args[0];
            tolerance = Double.valueOf(args[1]);
        } else if (args.length == 3) {
            dirPath = args[0];
            tolerance = Double.valueOf(args[1]);
            step = Double.valueOf(args[2]);
        } else {
            throw new IllegalArgumentException("Start the program with arguments: [path] and [tolerance (optional)]");
        }
    }

}

class Pair<X, Y> {
    public final X x;
    public final Y y;
    public Pair(X x, Y y) {
        this.x = x;
        this.y = y;
    }
    public X getFirst(){
        return x;
    }
    public Y getSecond(){
        return y;
    }
    /*public void setFirst(X x){
        this.x = x;
    }
    public void setSecond(Y y){
        this.y = y;
    }*/
}


