package Software;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Printer {

    int edgesForRoot;

    public Printer(String dirPath, String printType, List<RootedIntervalTree> solutions, String tolerance) {
        dirPath = new File(dirPath).getParent();
        //this.printType = settings[1];
        /*if (printType == null)
            printType = "sol";*/
        saveSolutionToFile(solutions, "rel", dirPath, tolerance);
        saveSolutionToFile(solutions, "sol", dirPath, tolerance);
        //saveSolutionToFile(solutions, printType, dirPath, tolerance);
    }

    public int toFile() {
        return edgesForRoot;
    }

    private void saveSolutionToFile(List<RootedIntervalTree> solutions, String printType, String dirPath, String tolerance) {
        edgesForRoot = 0;
        File output;
        int same = 0;
        if (dirPath == null) //if JAR in the same directory as gene tree
            output = new File("reroot-" + tolerance + "." + printType + ".txt");
        else
            output = new File(dirPath + "/reroot-" + tolerance + "." + printType + ".txt");
        try {
            output.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Problem with creating an output file.");
        }
        FileWriter fw;
        try {
            fw = new FileWriter(output);
            //System.out.println("Solutions: "+solutions.size());
            for (int i = 0; i < solutions.size(); i++) {
                if (printType.equals("rel")) {
                    if (i != 0 &&
                            solutions.get(i).getRoot().getRight().getName().equals(solutions.get(i - 1).getRoot().getRight().getName()) &&
                            solutions.get(i).getRoot().getLeft().getName().equals(solutions.get(i - 1).getRoot().getLeft().getName())) {
                        same++;
                    } else {
                        if (same != 0) {
                            fw.write(System.lineSeparator());
                            fw.write("Same " + same);
                            same = 0;
                            fw.write(System.lineSeparator());
                            fw.write(System.lineSeparator());
                        }
                        edgesForRoot++;
                        fw.write("D: " + solutions.get(i).getScore().getDuplication() + " L: " + solutions.get(i).getScore().getLoss());
                        //System.out.println("D: " + solutions.get(i).getFirst().getDuplication() + " L: " + solutions.get(i).getFirst().getLoss());
                        fw.write(System.lineSeparator());
                        printToRelFile(fw, solutions.get(i).getRoot());
                    }
                } else {
                    if (i != 0 &&
                            solutions.get(i).getRoot().getRight().getName().equals(solutions.get(i - 1).getRoot().getRight().getName()) &&
                            solutions.get(i).getRoot().getLeft().getName().equals(solutions.get(i - 1).getRoot().getLeft().getName())) {
                        same++;
                    } else {
                        if (same != 0) {
                            fw.write(System.lineSeparator());
                            fw.write("Same " + same);
                            same = 0;
                            fw.write(System.lineSeparator());
                            fw.write(System.lineSeparator());
                        }
                        edgesForRoot++;
                        fw.write("D: " + solutions.get(i).getScore().getDuplication() + " L: " + solutions.get(i).getScore().getLoss());
                        //System.out.println("D: " + solutions.get(i).getFirst().getDuplication() + " L: " + solutions.get(i).getFirst().getLoss());
                        fw.write(System.lineSeparator());
                        printToTreeFile(fw, solutions.get(i).getRoot());
                    }
                }
            }
            if (same != 0) {
                fw.write(System.lineSeparator());
                fw.write("Same " + same);
                same = 0;
            }
            fw.close();
        } catch (IOException ex) {
            System.err.println("Problem with writing into output file.");
        }
    }

    private void printToTreeFile(FileWriter fw, RootedIntervalNode node) throws IOException {
        fw.write(node.getName() + "\t" + node.getMinL() + "\t" + node.getMaxL());
        fw.write(System.lineSeparator());
        if (node.getLeft() != null) {
            printToTreeFile(fw, node.getLeft());
            printToTreeFile(fw, node.getRight());
        }
    }

    //List<Pair<String, String>> branches = new ArrayList<>();

    private void printToRelFile(FileWriter fw, RootedIntervalNode node) throws IOException {
        if (node.getRight() != null) {
            if (!node.getMappedToLca())
                fw.write("dup\t" + node.getRight().getName() + "\t" + node.getLeft().getName());
            else
                fw.write("spec\t" + node.getRight().getName() + "\t" + node.getLeft().getName());
        } else
            fw.write("gene\t" + node.getName());
        fw.write(System.lineSeparator());
        if (node.getLevelDistanceFromParent() > 0) {
            for (int i = 0; i < node.getLevelDistanceFromParent(); i++) {
                fw.write("loss\t" + node.getName());
                fw.write(System.lineSeparator());
            }
        }
        if (node.getLeft() != null) {
            printToRelFile(fw, node.getLeft());
            printToRelFile(fw, node.getRight());
            /*branches.add(new Pair<>(node.getName(), node.getLeft().getName()));
            branches.add(new Pair<>(node.getName(), node.getRight().getName()));*/
        }
    }

    /*private void saveSolutionToFile(){
        for (int i = 0; i < solutions.size(); i++) {
            FileWriter fw;
            FileWriter fw2;
            try {
                fw = new FileWriter(new File(dirPath + "/solution_" + i + ".tree"));
                printToTreeFile(fw, solutions.get(i));
                fw2 = new FileWriter(new File(dirPath + "/solution_" + i + ".depths"));
                fw2.write(solutions.get(i).getLeft().getName() + " " +
                        solutions.get(i).getRight().getName());
                fw2.write(System.lineSeparator());
                printToDepthsFile(fw2, solutions.get(i));
                fw.close();
                fw2.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void printToTreeFile(FileWriter fw, RootedIntervalNode node) throws IOException {
        if (node.getLeft() == null) return;
        fw.write(node.getName() + " " + node.getLeft().getName() + " " +
                (node.getLeft().getDepth() - node.getDepth()));
        fw.write(System.lineSeparator());
        fw.write(node.getName() + " " + node.getRight().getName() + " " +
                (node.getRight().getDepth() - node.getDepth()));
        fw.write(System.lineSeparator());
        printToTreeFile(fw, node.getLeft());
        printToTreeFile(fw, node.getRight());
    }

    private void printToDepthsFile(FileWriter fw, RootedIntervalNode node) throws IOException {
        if (node == null) return;
        fw.write(node.getName() + " " + node.getMinD() + " " + node.getMaxD());
        fw.write(System.lineSeparator());
        printToDepthsFile(fw, node.getLeft());
        printToDepthsFile(fw, node.getRight());
    }

    private void printReconciliation(){
        boolean goodRoot = false;
        int i = 0;
        String filename = dirPath + "/solution_" + i + ".depths";
        File f = new File(filename);
        while (f.exists()) {
            try {
                Scanner s = new Scanner(f);
                s.nextLine();
                goodRoot = true;
                System.out.println("#[min depth] [max depth]");
                while (s.hasNextLine()) {
                    String[] line = s.nextLine().split("\\s+");
                    double minD = Double.parseDouble(line[1]);
                    double maxD = Double.parseDouble(line[2]);
                    System.out.print(line[0] + " " + minD + " " + " " + maxD);
                    System.out.println();
                }
                s.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            i++;
            filename = dirPath + "/solution_" + i + ".depths";
            f = new File(filename);
        }
        if (!goodRoot) {
            System.err.println("BAD ROOT");
        }
    }*/
}
