package Software;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Main {

    public static void main(String[] args) {
        Loader load = new Loader(args);
        Four<RootedExactTree, UnrootedTree, Double, String> loadArgs = load.getLoadArgs();

        if(load.exit()){
            System.err.println("Start the program with correct gene tree and species tree.");
        } else {
        Reconciliator rec = new Reconciliator(loadArgs.getFirst(),
                loadArgs.getSecond(),
                loadArgs.getThird());

        Printer print = new Printer(loadArgs.getFourth(), rec.getSolutions());
        }
    }


}


