package Software;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Loader {

    String[] args;

    public Loader(String[] args) {
        this.args = args;
    }

    public Object[] getLoadArgs() {
        return loadArguments(args);
    }

    /*
     * -help = help, print all posiible settings
     * -S = path to species tree (mandatory)
     * -G = path to gene tree (mandatory)
     * -M = path to mapping (optional)
     * -t = tolerance (optional)
     * -s = step (optional)
     * -r = if the gene tree is rooted
     * -reroot = if the gene tree is rooted and is wish to be rerooted
     * -l = count losses above the root of gene tree
     * -p = print type (optional)
     * -epsilon = set epsilon
     * */
    private Object[] loadArguments(String[] args) {
        Parser p = new Parser();
        RootedExactTree S = null;
        UnrootedTree G_unrooted = null;
        RootedIntervalTree G_rooted = null;
        List<Pair<String, String>> Smapping = new ArrayList<>();
        String dirPathGene = null;
        Double tolerance = null, step = null;
        String printType = null;
        double epsilon = Double.parseDouble("1e-6");
        boolean rootedGeneTree = false, reroot = false, countLossesAboveRoot = false;
        boolean exit = false;
        for (int i = 0; i < args.length; i += 2) {
            switch (args[i]) {
                case "-help":
                    System.out.println("Usage: java -jar isometricRecon.jar [options] \n" +
                            "\n" +
                            "Options:\n" +
                            "\tInput/Output:\n" +
                            "\t\t-help\tshow this help message and exit" +
                            "\t\t-S <species tree>\tspecies tree file in newick format (mandatory)\n" +
                            "\t\t-G <gene tree>\tgene tree file in newick format (mandatory)\n" +
                            "\t\t-M <species map>\tgene to species map\n" +
                            "\t\t-t <tolerance>\ttolerance from 0 to 1 (default: 0.5)\n" +
                            "\t\t-s <step>\tstep (default: 0.5)\n" +
                            "\t\t-r\tif the gene tree is rooted (by default: the gene tree is consider to be unrooted)\n" +
                            "\t\t-reroot\tif the gene tree is rooted and is wished to be rerooted\n" +
                            "\t\t-l\tcount losses above the root of the gene tree\n" +
                            "\t\t-p <print type>\ttwo options: sol = for tree with depths (default)\n" +
                            "\t\t\t\t\trel = for tree with dup, gene, spec, loss definition next to nodes\n" +
                            "\t\t-epsilon <epsilon>\tepsilon (default: 1e-6");
                    exit = true;
                    break;
                case "-S":
                    S = loadSpeciesTreeFromFile(args[i + 1], p);
                    if(S == null)
                        System.out.println("Wrong species tree.");
                    break;
                case "-G":
                    dirPathGene = args[i + 1];
                    break;
                case "-M":
                    Smapping = loadSpeciesMappingFromFile(args[i + 1], p);
                    break;
                case "-t":
                    tolerance = Double.valueOf(args[i + 1]);
                    if (tolerance < 0.0 || tolerance > 1.0){
                        System.out.println("Tolerance out of range. Set to default: 0.5");
                        tolerance = 0.5;
                    }
                    break;
                case "-s":
                    step = Double.valueOf(args[i + 1]);
                    if(step < 0.0){
                    System.out.println("Step less than 0.0. Set to default: 0.01");
                    step = 0.01;
                }
                    break;
                case "-r":
                    rootedGeneTree = true;
                    i--;
                    break;
                case "-reroot":
                    reroot = true;
                    i--;
                    break;
                case "-l":
                    countLossesAboveRoot = true;
                    i--;
                    break;
                case "-p":
                    if (args[i + 1].equals("rel") || args[i + 1].equals("sol"))
                        printType = args[i + 1];
                    else
                        printType = "sol";
                    break;
                case "-epsilon":
                    epsilon = Double.parseDouble(args[i + 1]);
                    break;
            }
            if (exit)
                break;
        }
        if (S == null || dirPathGene == null)
            return null;
        if (tolerance == null)
            tolerance = 0.5;
        if (step == null)
            step = 0.01;

        if (rootedGeneTree) {
            G_rooted = loadRootedGeneTreeFromFile(dirPathGene, p, tolerance, Smapping, S);
            if(G_rooted == null)
                return null;
            else if (G_rooted.getLeafMap().isEmpty()){
                System.out.println("Wrong leaf mapping.");
                return null;
            }
        }
        else {
            G_unrooted = loadUnrootedGeneTreeFromFile(dirPathGene, p, tolerance, Smapping);
            if(G_unrooted == null)
                return null;
            else if (G_unrooted.getLeafMap().isEmpty()){
                System.out.println("Wrong leaf mapping.");
                return null;
            }
        }
        if (reroot && G_rooted != null) {
            G_unrooted = p.rootedToUnrootedTree(G_rooted);
            G_unrooted.setLeafMap(G_rooted.getLeafMap());
            G_rooted = null;
        }

        return new Object[]{S, G_rooted, G_unrooted, step, countLossesAboveRoot, dirPathGene, printType, epsilon, tolerance};
    }

    private RootedExactTree loadSpeciesTreeFromFile(String dirPath, Parser p) {
        if (new File(dirPath).exists()) {
            return p.parseRootedTree(dirPath);
        } else {
            System.err.println("Wrong species tree file.");
            return null;
        }
    }

    private List<Pair<String, String>> loadSpeciesMappingFromFile(String dirPath, Parser p) {
        File Smap = new File(dirPath);
        if (Smap.exists()) {
            return p.parseSpeciesMapping(Smap);
        } else {
            System.err.println("Wrong mapping file.");
            return null;
        }
    }

    private RootedIntervalTree loadRootedGeneTreeFromFile(String dirPath, Parser p, Double tolerance, List<Pair<String, String>> Smapping, RootedExactTree S) {
        if (new File(dirPath).exists()) {
            return p.parseRootedIntervalTree(dirPath, tolerance, Smapping, S);
        } else {
            System.err.println("Wrong gene tree file: " + dirPath);
            return null;
        }
    }

    private UnrootedTree loadUnrootedGeneTreeFromFile(String dirPath, Parser p, Double tolerance, List<Pair<String, String>> Smapping) {
        if (new File(dirPath).exists()) {
            return p.parseUnrootedTree(dirPath, tolerance, Smapping);
        } else {
            System.err.println("Wrong gene tree file: " + dirPath);
            return null;
        }
    }
}
