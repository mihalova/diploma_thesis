package Software;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Printer {

    ArrayList<RootedIntervalNode> solutions;
    String dirPath;

    public Printer(String dirPath, ArrayList<RootedIntervalNode> solutions){
        this.dirPath = dirPath;
        this.solutions = solutions;
    }

    void runPrinter(){
        saveSolutionToFile();
        printReconciliation();
    }

    private void saveSolutionToFile(){
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
    }
}
