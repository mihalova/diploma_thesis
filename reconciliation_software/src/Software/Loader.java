package Software;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Loader {

    String[] args;
    private String dirPathSpecies, dirPathGene, dirPathMap;
    private Double tolerance, step;
    private RootedExactTree S;
    private UnrootedTree G;
    private List<Pair<String, String>> Smapping = new ArrayList<>();
    private boolean wrongArguments;

    public Loader(String[] args) {
        this.args = args;
        wrongArguments = false;
    }

    public Four<RootedExactTree, UnrootedTree, Double, String> getLoadArgs(){
        loadArguments(args);
        if (tolerance == null)
            tolerance = 0.5;
        loadTrees();
        return new Four<>(S, G, step, dirPathGene);
    }

    private void loadTrees() {
        if (dirPathSpecies == null) {
            wrongArguments = true;
            return;
        } else
            loadSpeciesTreeFromFile();
        loadSpeciesMappingFromFile();
        if (dirPathGene == null) {
            wrongArguments = true;
            return;
        } else
            loadGeneTreeFromFile();
    }


    public boolean exit(){
        return wrongArguments;
    }

    /*
     * -h = help, print all posiible settings
     * -S = path to species tree (mandatory)
     * -G = path to gene tree (mandatory)
     * -M = path to mapping (optional)
     * -t = tolerance (optional)
     * -s = step (optional)
     * */
    private void loadArguments(String[] args) {
        boolean exit = false;
        for(int i = 0; i < args.length; i+=2){
            switch (args[i]) {
                case "-h":
                    System.out.println("Usage: java -jar isometricRecon.jar [options] \n" +
                            "\n" +
                            "Options:\n" +
                            "\tInput/Output:\n" +
                            "\t\t-h\tshow this help message and exit" +
                            "\t\t-S <species tree>\tspecies tree file in newick format (mandatory)\n" +
                            "\t\t-G <gene tree>\tgene tree file in newick format (mandatory)\n" +
                            "\t\t-M <species map>\tgene to species map\n" +
                            "\t\t-t <tolerance>\ttolerance from 0 to 1 (default: 0.5)\n" +
                            "\t\t-s <step>\tstep (default: 0.5)");
                    exit = true;
                    break;
                case "-S":
                    dirPathSpecies = args[i+1];
                    break;
                case "-G":
                    dirPathGene = args[i+1];
                    break;
                case "-M":
                    dirPathMap = args[i+1];
                    break;
                case "-t":
                    tolerance = Double.valueOf(args[i+1]);
                    break;
                case "-s":
                    step = Double.valueOf(args[i+1]);
                    break;
            }
            if (exit)
                break;
        }
        /*if (args.length == 1) {
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
        }*/
    }

    private void loadSpeciesTreeFromFile() {
        if (new File(dirPathSpecies).exists()){
            S = Parser.parseRootedTree(dirPathSpecies);
        } else {
            throw new IllegalArgumentException("Wrong species tree file.");
        }
    }

    private void loadSpeciesMappingFromFile() {
        if(dirPathMap != null) {
            File Smap = new File(dirPathMap);
            Smapping = Parser.parseSpeciesMapping(Smap);
        }
    }

    private void loadGeneTreeFromFile() {
        //unrooted G
        if (new File(dirPathGene).exists()) {
            G = Parser.parseUnrootedTree(dirPathGene, tolerance, Smapping);
        } else {
            throw new IllegalArgumentException("Wrong gene tree file.");
        }
    }
}
