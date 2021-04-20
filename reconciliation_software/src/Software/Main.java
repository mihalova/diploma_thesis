package Software;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Main {

    public static double EPSILON;

    //actual main
    /*public static void main(String[] args) {
        Loader load = new Loader(args);
        Object[] loadArgs = load.getLoadArgs();
        EPSILON = (Double) loadArgs[7];
        if (loadArgs == null) {
            System.err.println("Start the program with correct gene tree and species tree.");
        } else {
            Reconciliator rec = new Reconciliator((RootedExactTree) loadArgs[0],
                    (RootedIntervalTree) loadArgs[1], (UnrootedTree) loadArgs[2],
                    (Double) loadArgs[3], (Boolean) loadArgs[4]);
            List<RootedIntervalTree> sol = rec.getSolutions();
            if (!sol.isEmpty()) {
                Printer print = new Printer((String) loadArgs[5], (String) loadArgs[6], sol, "");
            } else {
                System.out.println("No solution for given gene tree.");
            }
        }
    }*/







    static int duplication, loss, originalDuplication, originalLoss, totalSolutions,
            totalSameSolutions, totalDifferentSolutions, totalEdgesForRoot, wrongTrees,
            numberSame, numberDifferent, numberBoth;
    static long totalDuration;
    static Instant startTime, endTime;

    /*double[] tol = {0.0, 0.000001, 0.00001, 0.001, 0.1, 0.3, 0.5, 1.0};
    double[] st = {0.0, 0.3, 0.5, 1.0, 2.0};
    String[] fam = {"flies", "fungi"};*/

    public static void main(String[] args) {
        int wrongTreesDup, wrongTreesLoss;
        double[] tol = {0.0, 0.000001, 0.00001, 0.001, 0.1, 0.3, 0.5, 1.0};
        double[] st = {0.0, 0.3, 0.5, 1.0, 2.0};
        String[] fam = {"fungi"};
        //double tolerance = 0.0;
        //"0-0", "1-1", "1-4", "2-2", "4-1", "4-4"
        String[] allTrees = {"0-0", "1-1", "1-4", "2-2", "4-1", "4-4"};
        for (String s : fam) {
            for (double step : st) {
                for (double tolerance : tol) {
                    XSSFWorkbook workbook = new XSSFWorkbook();
                    try {
                        for (String allTree : allTrees) {
                            System.out.println("fam: " + s + " trees: " + allTree + " tolerance: " + tolerance+ " step: "+step);
                            XSSFSheet spreadsheet
                                    = workbook.createSheet("fam-" + allTree);
                            XSSFRow row;
                            Map<String, Object[]> treeData
                                    = new TreeMap<String, Object[]>();
                            treeData.put("1", new Object[]{"Trees",
                                    "Original",
                                    "Dup",
                                    "Original",
                                    "Loss",
                                    "Solutions",
                                    "Same",
                                    "Different",
                                    "Edges",
                                    "Time",
                                    });
                            duplication = loss = originalDuplication = originalLoss
                                    = totalSolutions = totalSameSolutions = totalDifferentSolutions
                                    = totalEdgesForRoot = wrongTrees = numberSame = numberDifferent = numberBoth = 0;
                            totalDuration = 0;
                            for (int treeNumber = 0; treeNumber < 1000; treeNumber++) {
                                startTime = Instant.now();
                                args = new String[]{"-G", "C:\\Users\\Dominika\\Diploma\\our\\" + s + "\\" + allTree + "\\" + treeNumber + "\\" + treeNumber + ".times.tree",
                                        "-r", "-reroot",
                                        "-S", "C:\\Users\\Dominika\\Diploma\\our\\" + s + ".stree",
                                        "-M", "C:\\Users\\Dominika\\Diploma\\our\\" + s + ".smap",
                                        "-t", String.valueOf(tolerance),
                                        "-s", String.valueOf(step),
                                        "-p", "rel",
                                        "-epsilon", "1e-6"};
                                Loader load = new Loader(args);
                                Object[] loadArgs = load.getLoadArgs();
                                EPSILON = (Double) loadArgs[7];

                                if (loadArgs == null) {
                                    //fw.write(String.valueOf(treeNumber));
                                    //fw.write(System.lineSeparator());
                                    System.err.println("Start the program with correct gene tree and species tree.");
                                    //wrongTrees++;
                                } else {
                                    Reconciliator rec = new Reconciliator((RootedExactTree) loadArgs[0],
                                                (RootedIntervalTree) loadArgs[1], (UnrootedTree) loadArgs[2],
                                                (Double) loadArgs[3], (Boolean) loadArgs[4]);
                                    List<RootedIntervalTree> sol = rec.getSolutions();
                                    endTime = Instant.now();
                                    long duration = Duration.between(startTime, endTime).toMillis();
                                    if (!sol.isEmpty()) {
                                        Printer print = new Printer((String) loadArgs[5], (String) loadArgs[6], sol, "t_" + tolerance);
                                        int edges = print.toFile();
                                        Four<Integer, Integer, Integer, Integer> result = checkRoot(s, allTree, treeNumber, sol, treeNumber, treeData);
                                        treeData.put(String.valueOf(treeNumber + 2), new Object[]{treeNumber,
                                                result.getThird(),
                                                sol.get(0).getScore().getDuplication(),
                                                result.getFourth(),
                                                sol.get(0).getScore().getLoss(),
                                                sol.size(),
                                                result.getFirst(),
                                                result.getSecond(),
                                                edges,
                                                duration / 1000.0});
                                        if(result.getFirst() != 0)
                                            numberSame++;
                                        if(result.getSecond() != 0)
                                            numberDifferent++;
                                        if (result.getFirst() != 0 && result.getSecond() != 0)
                                            numberBoth++;
                                        originalDuplication += result.getThird();
                                        originalLoss += result.getFourth();
                                        duplication += sol.get(0).getScore().getDuplication();
                                        loss += sol.get(0).getScore().getLoss();
                                        totalSolutions += sol.size();
                                        totalSameSolutions += result.getFirst();
                                        totalDifferentSolutions += result.getSecond();
                                        totalEdgesForRoot += edges;
                                        totalDuration += duration;
                                    } else {
                                        wrongTrees++;
                                        System.out.print(treeNumber+"; ");
                                        //System.out.println("No solution for tree "+treeNumber);
                                    }
                                    totalDuration += duration;
                                }
                            }
                            treeData.put("1002", new Object[]{-1,
                                    originalDuplication,
                                    duplication,
                                    originalLoss,
                                    loss,
                                    totalSolutions,
                                    totalSameSolutions,
                                    totalDifferentSolutions,
                                    totalEdgesForRoot,
                                    totalDuration/1000.0});
                            treeData.put("1003", new Object[]{-2,
                                    wrongTrees,
                                    0,
                                    0,
                                    0,
                                    0,
                                    numberSame,
                                    numberDifferent,
                                    numberBoth,
                                    0.0});
                            Set<String> keyid = treeData.keySet();
                            int rowid = 0;
                            for (String key : keyid) {

                                row = spreadsheet.createRow(rowid++);
                                Object[] objectArr = treeData.get(key);
                                int cellid = 0;
                               if (key.equals("1"))
                                    for (int i = 0; i < objectArr.length; i++) {
                                        Cell cell = row.createCell(cellid++);
                                        cell.setCellValue((String) objectArr[i]);
                                    }
                                else
                                    for (int i = 0; i < objectArr.length; i++) {
                                        Cell cell = row.createCell(cellid++);
                                        if (i < 9)
                                            cell.setCellValue((Integer) objectArr[i]);
                                        else
                                            cell.setCellValue((Double) objectArr[i]);
                                    }
                            }
                            System.out.println("\n");
                        }
                        FileOutputStream out = new FileOutputStream(
                                new File("C:\\Users\\Dominika\\Diploma\\our\\" + s + "\\t_" + tolerance +"_s_"+step+".xlsx"));

                        workbook.write(out);
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static Four<Integer, Integer, Integer, Integer> checkRoot(String s, String allTree, int treeNumber, List<RootedIntervalTree> sol, int tree, Map<String, Object[]> treeData) {
        Four<String[], String[], Integer, Integer> childrenOriginal = getChildrenRoot("C:\\Users\\Dominika\\Diploma\\our\\" + s + "\\" + allTree + "\\" + treeNumber + ".txt");
        int sameEdge = 0, differentEdge = 0;
        for (RootedIntervalTree solution : sol) {
            String[] right = solution.getRoot().getRight().getName().split(",");
            Arrays.sort(right);
            String[] left = solution.getRoot().getLeft().getName().split(",");
            Arrays.sort(left);
            if ((Arrays.equals(childrenOriginal.getFirst(), left) && Arrays.equals(childrenOriginal.getSecond(), right)) ||
                    Arrays.equals(childrenOriginal.getFirst(), right) && Arrays.equals(childrenOriginal.getSecond(), left))
                sameEdge++;
            else
                differentEdge++;
        }
        return new Four<>(sameEdge, differentEdge, childrenOriginal.getThird(), childrenOriginal.getFourth());
    }

    public static Four<String[], String[], Integer, Integer> getChildrenRoot(String path) {
        File ab = new File(path);
        String[] right = new String[0], left = new String[0];
        Integer duplication = 0, loss = 0;
        Scanner c = null;
        int sameEdge = 0, differentEdge = 0;
        try {
            c = new Scanner(ab);
            String[] root = {"", ""};
            while (c.hasNextLine()) {
                String str = c.nextLine();
                String[] split = str.split("\\t");
                if (!split[0].equals("gene") && (split[1].length() + split[2].length() > root[0].length() + root[1].length())) {
                    root[0] = split[1];
                    root[1] = split[2];
                }
                if (split[0].equals("dup"))
                    duplication++;
                if (split[0].equals("loss"))
                    loss++;
            }
            right = root[0].split(",");
            Arrays.sort(right);
            left = root[1].split(",");
            Arrays.sort(left);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new Four<>(right, left, duplication, loss);
    }

    //deleting files
   /* public static void main(String[] args) {
        String[] fam = {"fungi", "flies"};
        String[] possible = {"0-0", "1-1", "1-4", "2-2", "4-1", "4-4"};
        for (String f : fam) {
            for (String s : possible) {
                for (int i = 0; i < 1000; i++) {
                    File folder = new File("C:\\Users\\Dominika\\Diploma\\our\\"+f+"\\" + s + "\\" + i);
                    File[] fList = folder.listFiles();
                    for (File pes : fList) {
                        if (pes.getName().endsWith(".rel.txt")) {
                            boolean success = (pes.delete());
                        }
                            if (pes.getName().endsWith(".sol.txt")) {
                                boolean success = (pes.delete());
                        }
                    }
                }
            }
        }
    }*/

    //rozdeli subory rel na mensie subory, kde nazov je rovnaky s nazvom stromu
    /*public static void main(String[] args) {
        String[] fam = {"flies", "fungi"};
        String[] possible = {"0-0", "1-1", "1-4", "2-2", "4-1", "4-4"};
        int tree = -1;
        for (String family : fam) {
            for (String value : possible) {
                File rel = new File("C:\\Users\\Dominika\\Diploma\\our\\" + family + "\\" + value + ".rel.txt");
                try {
                    File output = new File("C:\\Users\\Dominika\\Diploma\\our\\" + family + "\\" + value + "\\" + tree + ".txt");
                    ;
                    FileWriter fw = new FileWriter(output);
                    Scanner s = new Scanner(rel);
                    while (s.hasNextLine()) {
                        String str = s.nextLine();
                        String[] split = str.split("\\t");
                        int treeNumber = Integer.parseInt(split[0].substring(split[0].lastIndexOf("/") + 1, split[0].lastIndexOf(".")));
                        if (treeNumber != tree) {
                            tree = treeNumber;
                            fw.close();
                            output = new File("C:\\Users\\Dominika\\Diploma\\our\\" + family + "\\" + value + "\\" + tree + ".txt");
                            fw = new FileWriter(output);
                            for (int i = 1; i < split.length; i++) {
                                fw.write(split[i] + "\t");
                            }
                            fw.write(System.lineSeparator());
                        } else {
                            for (int i = 1; i < split.length; i++) {
                                fw.write(split[i] + "\t");
                            }
                            fw.write(System.lineSeparator());
                        }
                    }
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/
    //control treefix files
    //change path
   /* public static void main(String[] args) {
        String[] fam = {"fungi"};
        String[] allTrees = {"0-0"};
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet
                = workbook.createSheet("treefix");
        XSSFRow row;
        Map<String, Object[]> treeData
                = new TreeMap<String, Object[]>();
        for (String s : fam) {
            originalLoss = originalDuplication = duplication = loss = totalSameSolutions = totalDifferentSolutions = 0;
            int i = 2;
            treeData.put("1", new Object[]{"Trees",
                    "Original Dup",
                    "Treefix Dup",
                    "Original Loss",
                    "Treefix Loss",
                    "Same root",
                    "Different root"});
            for (String allTree : allTrees) {
                for (int treeNumber = 0; treeNumber < 1000; treeNumber++) {
                    Four<String[], String[], Integer, Integer> childrenTreefix = getChildrenRoot("D:\\diploma_softwares\\treefix_use\\examples\\fungi\\0-0\\0\\0.treefix.rel.txt");
                    Four<String[], String[], Integer, Integer> childrenOriginal = getChildrenRoot("D:\\diploma_softwares\\dataset\\treefix\\sim-" + s + "\\" + s + "\\" + allTree + "\\" + treeNumber + ".txt");
                    originalDuplication += childrenOriginal.getThird();
                    originalLoss += childrenOriginal.getFourth();
                    duplication += childrenTreefix.getThird();
                    loss += childrenTreefix.getFourth();
                    if ((Arrays.equals(childrenOriginal.getFirst(), childrenTreefix.getFirst()) && Arrays.equals(childrenOriginal.getSecond(), childrenTreefix.getSecond())) ||
                            Arrays.equals(childrenOriginal.getFirst(), childrenTreefix.getSecond()) && Arrays.equals(childrenOriginal.getSecond(), childrenTreefix.getFirst()))
                        totalSameSolutions++;
                    else
                        totalDifferentSolutions++;
                }
                treeData.put(String.valueOf(i), new Object[]{allTree,
                        originalDuplication,
                        duplication,
                        originalLoss,
                        loss,
                        totalSameSolutions,
                        totalDifferentSolutions});
                i++;
            }
            Set<String> keyid = treeData.keySet();
            int rowid = 0;
            for (String key : keyid) {
                row = spreadsheet.createRow(rowid++);
                Object[] objectArr = treeData.get(key);
                int cellid = 0;
                if (key.equals("1"))
                    for (Object o : objectArr) {
                        Cell cell = row.createCell(cellid++);
                        cell.setCellValue((String) o);
                    }
                else
                    for (int a = 0; a < objectArr.length; a++) {
                        Cell cell = row.createCell(cellid++);
                        if (a == 0)
                            cell.setCellValue((String) objectArr[a]);
                        else
                            cell.setCellValue((Integer) objectArr[a]);
                    }
            }
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(
                        new File("D:\\diploma_softwares\\dataset\\treefix\\sim-" + s + "\\treefix.xlsx"));


                workbook.write(out);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

   /* public static void main(String[] args) {
        double[][] finalLen = new double[30][1000];
        for (int treeNumber = 0; treeNumber < 1000; treeNumber++) {
            File ab = new File("C:\\Users\\Dominika\\Diploma\\our\\fungi\\0-0\\" + treeNumber + "\\" + treeNumber + ".tree");
            Scanner c = null;
            int index = 0;
            try {
                c = new Scanner(ab);
                while (c.hasNextLine()) {
                    String str = c.nextLine();
                    String[] split = str.split(":");
                    if (split.length > 1) {
                        String s = split[1].replaceAll(",", "");
                        finalLen[index][treeNumber] += Double.parseDouble(s);
                        index++;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        for(double[] f:finalLen) {
            Arrays.sort(f);
            double m =(f[f.length/2-1]+f[f.length/2])/2;
            System.out.println(Reconciliator.round(m));
        }
    }*/
}


