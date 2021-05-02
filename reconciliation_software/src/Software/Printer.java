package Software;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Printer {

    public Printer(String dirPath, String printType, List<RootedIntervalTree> solutions) {
        if (printType == null)
            printType = "sol";
        saveSolutionToFile(solutions, printType, dirPath);
    }

    private void saveSolutionToFile(List<RootedIntervalTree> solutions, String printType, String dirPath) {
        File f = new File(dirPath);
        dirPath = f.getParent();
        String geneTreeName = f.getName();
        File output;
        int same = 0;
        if (dirPath == null) //if JAR in the same directory as gene tree
            output = new File(geneTreeName + "." + printType + ".txt");
        else
            output = new File(dirPath + "/" + geneTreeName + "." + printType + ".txt");
        try {
            output.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Problem with creating an output file.");
        }
        FileWriter fw;
        try {
            fw = new FileWriter(output);
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
                        fw.write("D: " + solutions.get(i).getScore().getDuplication() + " L: " + solutions.get(i).getScore().getLoss());
                        fw.write(System.lineSeparator());
                        printToRelFile(fw, solutions.get(i).getRoot());
                    }
                } else {
                    fw.write(printToTreeFile(solutions.get(i).getRoot()) + ";");
                    fw.write(System.lineSeparator());
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

    private String printToTreeFile(RootedIntervalNode node) {
        String tree = "";
        if (node.getLeft() != null) {
            String left = printToTreeFile(node.getLeft());
            String right = printToTreeFile(node.getRight());
            double leftLength = node.getLeft().getMaxD() - node.getMaxD();
            double rightLength = node.getRight().getMaxD() - node.getMaxD();
            tree = "(" + left + ":" + leftLength + "," + right + ":" + rightLength + ")";
        } else {
            tree = node.getName();
        }
        return tree;
    }

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
        }
    }
}
