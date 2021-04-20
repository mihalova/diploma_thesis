package Software;

import sun.rmi.runtime.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.*;

public class Parser {
    static UnrootedTree G_unrooted;
    static RootedIntervalTree G_rooted;
    static int k;

    String pattern = "##.##########";
    DecimalFormat decimalFormat = new DecimalFormat(pattern);

    public UnrootedTree parseUnrootedTree(String path, Double tolerance, List<Pair<String, String>> Smapping) {
        G_unrooted = new UnrootedTree();
        k = 0;
        boolean oneNodeTree = false, twoNodeTree = false;
        UnrootedNode first, second = null, third = null;

        try (Scanner s = new Scanner(new File(path))) {
            String str = "";
            while (s.hasNextLine()) {
                str += s.nextLine().replaceAll("\\s+", "");
            }
            //najde poslednu zatvorku
            int i = str.length() - 1;
            while (str.charAt(i) != ')') {
                i--;
            }
            str = str.substring(1, i);
            //najde prvu ciarku
            i = 0;
            int counter = 0;
            while (!(str.charAt(i) == ',' && counter == 0)) {
                if (str.charAt(i) == '(') counter++;
                if (str.charAt(i) == ')') counter--;
                i++;
                if (i == str.length()) {
                    oneNodeTree = true;
                    break;
                }
            }
            int firstCommaIndex = i;
            i++;
            //najde druhu ciarku
            if (!oneNodeTree) {
                while (!(str.charAt(i) == ',' && counter == 0)) {
                    if (str.charAt(i) == '(') counter++;
                    if (str.charAt(i) == ')') counter--;
                    i++;
                    if (i == str.length()) {
                        twoNodeTree = true;
                        break;
                    }
                }
            }
            int secondCommaIndex = i;
            //vytvorí nodes a hrany
            UnrootedNode node = new UnrootedNode("node" + k);
            k++;
            G_unrooted.addNode(node);
            first = parseNewickUnrootedNode(
                    str.substring(0, firstCommaIndex), node, tolerance, Smapping);
            if (!oneNodeTree) {
                second = parseNewickUnrootedNode(
                        str.substring(firstCommaIndex + 1, secondCommaIndex), node, tolerance, Smapping);
            }
            if (!twoNodeTree && !oneNodeTree) {
                third = parseNewickUnrootedNode(
                        str.substring(secondCommaIndex + 1), node, tolerance, Smapping);
            }
            for (Edge e : first.getEdges()) {
                if (e.otherNode(first).equals(node)) {
                    node.addEdge(e);
                }
            }
            if (second != null) {
                for (Edge e : second.getEdges()) {
                    if (e.otherNode(second).equals(node)) {
                        node.addEdge(e);
                    }
                }
            }
            if (third != null) {
                for (Edge e : third.getEdges()) {
                    if (e.otherNode(third).equals(node)) {
                        node.addEdge(e);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        G_unrooted.renameNodes();
        return G_unrooted;
    }

    private UnrootedNode parseNewickUnrootedNode(String str, UnrootedNode nodeFrom, Double tolerance, List<Pair<String, String>> Smapping) {
        int colonIndex = str.lastIndexOf(':');
        String time = str.substring(colonIndex + 1);
        double[] interval = getTimeInterval(time, tolerance);

        str = str.substring(0, colonIndex);
        if (!str.startsWith("(")) {
            //tu bude parsovanie leafmap G0_S1 = nodeG string "_" nodeS string
            String[] mapping = getMapping(str, Smapping);

            G_unrooted.addMapping(mapping[0], mapping[1]);
            UnrootedNode node = new UnrootedNode(mapping[0]);
            G_unrooted.addNode(node);
            Edge edgeFrom = new Edge(node, nodeFrom, interval[0], interval[1]);
            G_unrooted.addEdge(edgeFrom);
            node.addEdge(edgeFrom);
            return node;
        } else {
            UnrootedNode node = new UnrootedNode("node" + k);
            k++;
            G_unrooted.addNode(node);
            Edge edgeFrom = new Edge(node, nodeFrom, interval[0], interval[1]);
            G_unrooted.addEdge(edgeFrom);
            node.addEdge(edgeFrom);

            int i = 1;
            int counter = 0;
            while (!(str.charAt(i) == ',' && counter == 0)) {
                if (str.charAt(i) == '(') counter++;
                if (str.charAt(i) == ')') counter--;
                i++;
                if (i == str.length()) {
                    System.err.println("Problem with parsing tree");
                }
            }

            int commaIndex = i;

            UnrootedNode first = parseNewickUnrootedNode(
                    str.substring(1, commaIndex), node, tolerance, Smapping);
            UnrootedNode second = parseNewickUnrootedNode(
                    str.substring(commaIndex + 1, str.length() - 1), node, tolerance, Smapping);

            for (Edge e : first.getEdges()) {
                if (e.otherNode(first).equals(node)) {
                    node.addEdge(e);
                }
            }
            for (Edge e : second.getEdges()) {
                if (e.otherNode(second).equals(node)) {
                    node.addEdge(e);
                }
            }
            return node;
        }
    }

    private String[] getMapping(String str, List<Pair<String, String>> Smapping) {
        String[] mappingG;
        if (Smapping.isEmpty()) {
            mappingG = str.split("_");
            if (mappingG.length == 1) {
                System.err.println("No mapping");
            } else {
                return new String[]{mappingG[0], mappingG[1]};
            }
        } else {
            int i = 0;
            while (!str.contains(Smapping.get(i).getFirst())) {
                i++;
                if (i == Smapping.size())
                    break;
            }
            if (i == Smapping.size())
                return new String[]{"", ""};
            else
                return new String[]{str, Smapping.get(i).getSecond()};
        }
        System.err.println("No mapping");
        return new String[]{"", ""};
    }

    private double[] getTimeInterval(String time, Double tolerance) {
        double min_time = 0, max_time = 0;
        time = time.replaceAll(",", ".");
        if (tolerance != null && !time.contains("-")) {
            //nastavenie tolerance pre hrany
            double t = Double.parseDouble(time);
            double[] interval = makeInterval(t, tolerance);
            min_time = interval[0];
            max_time = interval[1];
        } else if (time.contains("-")) {
            //parsovanie intervalov 2.02-3.01 = double "-" double
            String[] interval = time.split("-");
            min_time = Double.parseDouble(interval[0]);
            max_time = Double.parseDouble(interval[1]);
        } else
            System.err.println("Insert tree with intervals or set tolerance.");
        return new double[]{min_time, max_time};
    }

    public static double[] makeInterval(double time, Double tolerance) {
        double min = time - (tolerance * time);
        double max = time + (tolerance * time);
        return new double[]{min, max};
    }

    public RootedExactTree parseRootedTree(String path) {
        RootedExactTree t = new RootedExactTree();
        TreeMap<String, RootedExactNode> nodes = new TreeMap<>();

        try (Scanner s = new Scanner(new File(path))) {
            String str = "";
            while (s.hasNextLine()) {
                str += s.nextLine().replaceAll("\\s+", "");
            }
            int[] param = getParametersRoot(str);
            //nastavenie depth pre root
            t.getRoot().setDepth(0.0);
            t.getRoot().setLevel(0);
            //nodes pod root
            RootedExactNode left = parseNode(str.substring(param[0], param[1]), 0.0, 1, nodes);
            RootedExactNode right = parseNode(str.substring(param[1] + 1, param[2]), 0.0, 1, nodes);
            //nastaví hierarchiu
            left.setParent(t.getRoot());
            right.setParent(t.getRoot());
            t.getRoot().setLeft(left);
            t.getRoot().setRight(right);
        } catch (FileNotFoundException e) {
            System.err.println("Problem with parsing the species tree.");
        }
        t.setNodes(nodes);
        return t;
    }

    //Double s_time = 0.0;

    private int[] getParametersRoot(String str) {
        //suradnica ciarky
        int i = 1;
        int start = 1;
        int k = str.length() - 1;

        i = getMiddleColon(str, i);

        if (i == str.length()) {
            while (i == str.length()) {
                start++;
                k = str.substring(0, str.length() - 2).lastIndexOf(')');
                //s_time += Double.parseDouble(str.substring(k + 2, str.length() - 2));
                str = str.substring(1, k + 1);
                i = getMiddleColon(str, 1);
                /*start++;
                i = getMiddleColon(str, start);
                k = str.substring(0, str.length() - 2).lastIndexOf(')');
                s_time = Double.parseDouble(str.substring(k + 2, str.length() - 2));*/
            }
        }
        String a = str.substring(1, i);
        a = str.substring(i + 1, k);

        //suradnica poslednej zatvorky
        k = str.lastIndexOf(')');
        //while (!(str.charAt(k) == ')')) k--;
        return new int[]{start, i + start - 1, k + start - 1};
    }

    private int getMiddleColon(String str, int i) {
        int counter = 0;
        while (!(str.charAt(i) == ',' && counter == 0)) {
            if (str.charAt(i) == '(') counter++;
            if (str.charAt(i) == ')') counter--;
            i++;
            if (i == str.length())
                break;
        }
        return i;
    }

    private RootedExactNode parseNode(String str, double parentDepth, int level, TreeMap<String, RootedExactNode> nodes) {
        RootedExactNode node;
        if (str.startsWith("(")) {
            int[] params = getParametersInnerNodes(str);
            if (params[2] == str.length() - 1) {
                System.err.println("Wrong newick format inner");
                return null;
            }
            //vzdialenost od najblizsieho node
            Double time = Double.parseDouble(str.substring(params[0] + 1));
            //celkova vzdialenost od root
            Double depth = parentDepth + time;
            //nodes pod
            RootedExactNode left = parseNode(str.substring(1, params[1]), depth, level + 1, nodes);
            RootedExactNode right = parseNode(str.substring(params[1] + 1, params[2]), depth, level + 1, nodes);

            //ulozi nazov nodu v spravnom poradi (napr. S1S2)
            String name;
            if (left.getName().compareTo(right.getName()) < 0) {
                name = left.getName() + "," + right.getName();
            } else {
                name = right.getName() + "," + left.getName();
            }

            //nastavi hierarchiu
            node = new RootedExactNode(name, depth, level);
            left.setParent(node);
            right.setParent(node);
            node.setLeft(left);
            node.setRight(right);
            return node;
        } else {
            //ulozenie leaves
            int colonIndex = str.lastIndexOf(':');
            String name;
            double time;
            if (colonIndex == -1) {
                System.err.println("Wrong newick format leaf");
                return null;
            } else {
                name = str.substring(0, colonIndex);
                time = Double.parseDouble(str.substring(colonIndex + 1));
            }
            node = new RootedExactNode(name, time + parentDepth, level);
            nodes.put(name, node);
            return node;
        }
    }

    public int[] getParametersInnerNodes(String str) {
        int i = 1;
        int counter = 0;
        int ciarkaIndex = 0;
        while (!(str.charAt(i) == ')' && counter == 0)) {
            if (str.charAt(i) == '(') counter++;
            if (str.charAt(i) == ')') counter--;
            if (str.charAt(i) == ',' && counter == 0) ciarkaIndex = i;
            i++;
        }
        int koniecIndex = i;
        i++;
        return new int[]{i, ciarkaIndex, koniecIndex};
    }

    public RootedIntervalTree parseRootedIntervalTree(String path, Double tolerance, List<Pair<String, String>> Smapping, RootedExactTree speciesTree) {
        G_rooted = new RootedIntervalTree();

        try (Scanner s = new Scanner(new File(path))) {
            String str = "";
            while (s.hasNextLine()) {
                str += s.nextLine().replaceAll("\\s+", "");
            }
            int[] param = getParametersRoot(str);
            //nastavenie depth pre root
            G_rooted.getRoot().setMinL(0.0);
            G_rooted.getRoot().setMaxL(0.0);
            G_rooted.getRoot().setLevel(0);
            //nodes pod root
            RootedIntervalNode left = parseIntervalNode(str.substring(param[0], param[1]), tolerance, Smapping, speciesTree, 0.0);
            RootedIntervalNode right = parseIntervalNode(str.substring(param[1] + 1, param[2]), tolerance, Smapping, speciesTree, 0.0);

            if (right == null || left == null) {
                RootedIntervalNode node;
                if (right == null) {
                    node = left;
                } else {
                    node = right;
                }
                left = node.getLeft();
                right = node.getRight();
            }
            //nastaví hierarchiu
            left.setParent(G_rooted.getRoot());
            right.setParent(G_rooted.getRoot());
            G_rooted.getRoot().setLeft(left);
            G_rooted.getRoot().setRight(right);
            G_rooted.getRoot().setLcaS(RootedExactTree.lca(left.getLcaS(), right.getLcaS()));
        } catch (FileNotFoundException e) {
            System.err.println("Problem with parsing the species tree.");
        }
        return G_rooted;
    }

    private RootedIntervalNode parseIntervalNode(String str, Double tolerance, List<Pair<String, String>> Smapping, RootedExactTree speciesTree, double savedTime) {
        RootedIntervalNode node;
        int[] params = {0, 0, 0};
        if (str.startsWith("(")) {
            params = getParametersInnerNodes(str);
            if (params[1] == 0) {
                while (params[1] == 0) {
                    int indexColon = str.lastIndexOf(":");
                    savedTime += Double.parseDouble(str.substring(indexColon + 1, str.length() - 1));
                    str = str.substring(1, indexColon - 1);
                    params[1]++;
                    if (str.startsWith("("))
                        params = getParametersInnerNodes(str);
                }
            }
        }
        if (str.startsWith("(")) {

            if (params[2] == str.length() - 1) {
                System.err.println("Wrong newick format inner");
                return null;
            }
            double time = Double.parseDouble(str.substring(params[0] + 1));
            if (savedTime > 0) {
                time += savedTime;
                savedTime = 0.0;
            }
            double[] interval = getTimeInterval(decimalFormat.format(time), tolerance);
            //nodes pod
            RootedIntervalNode left = parseIntervalNode(str.substring(1, params[1]), tolerance, Smapping, speciesTree, savedTime);
            RootedIntervalNode right = parseIntervalNode(str.substring(params[1] + 1, params[2]), tolerance, Smapping, speciesTree, savedTime);

            if (left != null && right != null) {
                //ulozi nazov nodu v spravnom poradi (napr. S1S2)
                String name;
                if (left.getName().compareTo(right.getName()) < 0) {
                    name = left.getName() + "," + right.getName();
                } else {
                    name = right.getName() + "," + left.getName();
                }

                //nastavi hierarchiu
                node = new RootedIntervalNode(name);
                node.setMinL(interval[0]);
                node.setMaxL(interval[1]);
                left.setParent(node);
                right.setParent(node);
                node.setLeft(left);
                node.setRight(right);
                node.setLcaS(RootedExactTree.lca(left.getLcaS(), right.getLcaS()));
                return node;
            } else {
                if (left == null)
                    node = right;
                else
                    node = left;
                if (node != null) {
                    node.setMinL(node.getMinL() + interval[0]);
                    node.setMaxL(node.getMaxL() + interval[1]);
                }
                return node;
            }
        } else {
            //ulozenie leaves
            String[] leaf = str.split(":");

            double time;
            String[] mapping;
            double[] interval;
            if (leaf.length == 1) {
                System.err.println("Wrong newick format leaf");
                return null;
            }
            String nodeName = leaf[0];
            mapping = getMapping(nodeName, Smapping);
            time = Double.parseDouble(leaf[1]);
            if (savedTime != 0) {
                time += savedTime;
            }
            interval = getTimeInterval(decimalFormat.format(time), tolerance);
            if (!mapping[0].equals("")) {
                G_rooted.addMapping(mapping[0], mapping[1]);
                node = new RootedIntervalNode(mapping[0]);
                node.setMinL(interval[0]);
                node.setMaxL(interval[1]);
                node.setLcaS(speciesTree.findNode(mapping[1]));
                return node;
            } else {
                return null;
            }
        }
    }

    public List<Pair<String, String>> parseSpeciesMapping(File smap) {
        List<Pair<String, String>> Smapping = new ArrayList<>();
        try (Scanner s = new Scanner(smap)) {
            while (s.hasNextLine()) {
                String str = s.nextLine().replaceAll("\\*", "");
                String[] mapping = str.split("\\s+");
                Smapping.add(new Pair<String, String>(mapping[0], mapping[1]));
            }
        } catch (FileNotFoundException e) {
            System.err.println("Problem with species map.");
        }
        return Smapping;
    }

    public UnrootedTree rootedToUnrootedTree(RootedIntervalTree t){
        G_unrooted = new UnrootedTree();
        k = 0;
        RootedIntervalNode root = t.getRoot();
        double newMinL = root.getLeft().getMinL() + root.getRight().getMinL();
        double newMaxL = root.getLeft().getMaxL() + root.getRight().getMaxL();
        UnrootedNode node = new UnrootedNode("node" + k);
        k++;
        G_unrooted.addNode(node);
        RootedIntervalNode one, two, three = null;
        UnrootedNode first, second, third = null;
        if (root.getRight().getLeft() == null && root.getLeft().getLeft() == null) {
            //ak ani jedno dieta nema deti
            one = root.getLeft();
            two = root.getRight();
        } else if (root.getLeft().getLeft() == null && root.getRight().getLeft() != null){
            //ak lave dieta root nema deti a prave dieta ma deti
            one = root.getLeft();
            two = root.getRight().getRight();
            three = root.getRight().getLeft();
            //zdedi dlzku od root po terajsie Node1
            one.setMinL(newMinL);
            one.setMaxL(newMaxL);
        } else {
            one = root.getRight();
            two = root.getLeft().getRight();
            three = root.getLeft().getLeft();
            //zdedi dlzku od root po terajsie Node1
            one.setMinL(newMinL);
            one.setMaxL(newMaxL);
        }

        first = rootedToUnrootedNode(one, node);
        second = rootedToUnrootedNode(two, node);
        if (three != null) {
            third = rootedToUnrootedNode(three, node);
        }
        for (Edge e : first.getEdges()) {
            if (e.otherNode(first).equals(node)) {
                node.addEdge(e);
            }
        }
            for (Edge e : second.getEdges()) {
                if (e.otherNode(second).equals(node)) {
                    node.addEdge(e);
                }
            }
            if (third != null) {
                for (Edge e : third.getEdges()) {
                    if (e.otherNode(third).equals(node)) {
                        node.addEdge(e);
                    }
                }
            }
        G_unrooted.renameNodes();
        return G_unrooted;
    }

    public UnrootedNode rootedToUnrootedNode(RootedIntervalNode originalNode, UnrootedNode nodeFrom) {
        if (originalNode.getRight() == null) {
            //mapping je už hotove predtym
            UnrootedNode node = new UnrootedNode(originalNode.getName());
            G_unrooted.addNode(node);
            Edge edgeFrom = new Edge(node, nodeFrom, originalNode.getMinL(), originalNode.getMaxL());
            G_unrooted.addEdge(edgeFrom);
            node.addEdge(edgeFrom);
            return node;
        } else {
            UnrootedNode node = new UnrootedNode("node" + k);
            k++;
            G_unrooted.addNode(node);
            Edge edgeFrom = new Edge(node, nodeFrom, originalNode.getMinL(), originalNode.getMaxL());
            G_unrooted.addEdge(edgeFrom);
            node.addEdge(edgeFrom);

            UnrootedNode first = rootedToUnrootedNode(originalNode.getLeft(), node);
            UnrootedNode second = rootedToUnrootedNode(originalNode.getRight(), node);

            for (Edge e : first.getEdges()) {
                if (e.otherNode(first).equals(node)) {
                    node.addEdge(e);
                }
            }
            for (Edge e : second.getEdges()) {
                if (e.otherNode(second).equals(node)) {
                    node.addEdge(e);
                }
            }
            return node;
        }
    }
}
