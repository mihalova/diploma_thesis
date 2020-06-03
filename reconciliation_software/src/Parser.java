import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class Parser {
    static UnrootedTree G;
    static int k;

    static UnrootedTree parseUnrootedTree(File treeFile, Double tolerance) {
        G = new UnrootedTree();
        k = 0;

        try (Scanner s = new Scanner(treeFile)) {
            String str = s.nextLine();
            //najde poslednu zatvorku
            int i = str.length() - 1;
            while(str.charAt(i) != ')'){
                i--;
            }
            str = str.substring(1, i);
            //najde prvu ciarku
            i = 0;
            int counter = 0;
            while(!(str.charAt(i) == ',' && counter == 0)){
                if(str.charAt(i) == '(') counter++;
                if(str.charAt(i) == ')') counter--;
                i++;
            }
            int firstCommaIndex = i;
            i++;
            //najde druhu ciarku
            while(!(str.charAt(i) == ',' && counter == 0)){
                if(str.charAt(i) == '(') counter++;
                if(str.charAt(i) == ')') counter--;
                i++;
            }
            int secondCommaIndex = i;
            //vytvorí nodes a hrany
            UnrootedNode node = new UnrootedNode("node"+k);
            k++;
            G.addNode(node);
            UnrootedNode first = parseNewickUnrootedNode(
                    str.substring(0, firstCommaIndex), node, tolerance);
            UnrootedNode second = parseNewickUnrootedNode(
                    str.substring(firstCommaIndex + 1, secondCommaIndex), node, tolerance);
            UnrootedNode third = parseNewickUnrootedNode(
                    str.substring(secondCommaIndex + 1), node, tolerance);

            for (Edge e: first.getEdges()){
                if(e.otherNode(first).equals(node)){
                    node.addEdge(e);
                }
            }
            for (Edge e: second.getEdges()){
                if(e.otherNode(second).equals(node)){
                    node.addEdge(e);
                }
            }
            for (Edge e: third.getEdges()){
                if(e.otherNode(third).equals(node)){
                    node.addEdge(e);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        G.renameNodes();
        return G;
    }

    private static UnrootedNode parseNewickUnrootedNode(String str, UnrootedNode nodeFrom, Double tolerance) {
        double min_time = 0, max_time = 0;
        int colonIndex = str.lastIndexOf(':');
        String time = str.substring(colonIndex + 1);
        if (tolerance != null && !time.contains("-")){
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
            throw new IllegalArgumentException("Insert tree with intervals or set tolerance.");

        str = str.substring(0, colonIndex);
        if(!str.startsWith("(")){
            //tu bude parsovanie leafmap G0_S1 = nodeG string "_" nodeS string
            String[] mappingG  = str.split("_");
            G.addMapping(str, mappingG[1]);

            UnrootedNode node = new UnrootedNode(str);
            G.addNode(node);
            Edge edgeFrom = new Edge(node, nodeFrom, min_time, max_time);
            G.addEdge(edgeFrom);
            node.addEdge(edgeFrom);
            return node;
        } else {
            UnrootedNode node = new UnrootedNode("node"+k);
            k++;
            G.addNode(node);
            Edge edgeFrom = new Edge(node, nodeFrom, min_time, max_time);
            G.addEdge(edgeFrom);
            node.addEdge(edgeFrom);

            int i = 1;
            int counter = 0;
            while(!(str.charAt(i) == ',' && counter == 0)){
                if(str.charAt(i) == '(') counter++;
                if(str.charAt(i) == ')') counter--;
                i++;
                if(i == str.length()){
                    System.err.println("Problem with parsing tree");
                }
            }

            int commaIndex = i;

            UnrootedNode first = parseNewickUnrootedNode(
                    str.substring(1, commaIndex), node, tolerance);
            UnrootedNode second = parseNewickUnrootedNode(
                    str.substring(commaIndex + 1, str.length() - 1), node, tolerance);

            for (Edge e: first.getEdges()){
                if(e.otherNode(first).equals(node)){
                    node.addEdge(e);
                }
            }
            for (Edge e: second.getEdges()){
                if(e.otherNode(second).equals(node)){
                    node.addEdge(e);
                }
            }
            return node;
        }
    }

    public static double[] makeInterval(double time, Double tolerance) {
        double min = time -(tolerance*time);
        double max = time+(tolerance*time);
        return new double[]{min, max};
    }

    static RootedExactTree parseRootedTree(String prefix, String fileName){
        RootedExactTree t = new RootedExactTree();

        try (Scanner s = new Scanner(new File(prefix + "/" + fileName))) {
            String str = s.nextLine();
            //suradnica ciarky
            int i = 1;
            int counter = 0;
            while(!(str.charAt(i) == ',' && counter == 0)){
                if(str.charAt(i) == '(') counter++;
                if(str.charAt(i) == ')') counter--;
                i++;
            }
            //suradnica poslednej zatvorky
            int k = str.length()-1;
            while(!(str.charAt(k)==')')) k--;
            //root depth
            Double depth = Double.parseDouble(str.substring(k+2,str.length()-1));
            //nastavenie depth pre root
            t.getRoot().setDepth(depth);
            //nodes pod root
            RootedExactNode left = parseNode(str.substring(1,i), depth);
            RootedExactNode right = parseNode(str.substring(i+1,k), depth);
            //nastaví hierarchiu
            left.setParent(t.getRoot());
            right.setParent(t.getRoot());
            t.getRoot().setLeft(left);
            t.getRoot().setRight(right);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return t;
    }

    private static RootedExactNode parseNode(String str, double parentDepth){
        RootedExactNode node;
        if(str.startsWith("(")){
            //ulozenie inner nodes
            int i = 1;
            int counter = 0;
            int ciarkaIndex = 0;
            while(!(str.charAt(i) == ')' && counter == 0)){
                if(str.charAt(i) == '(') counter++;
                if(str.charAt(i) == ')') counter--;
                if(str.charAt(i) == ',' && counter == 0) ciarkaIndex = i;
                i++;
            }
            int koniecIndex = i;
            if(koniecIndex == str.length() - 1){
                System.err.println("Wrong newick format inner");
                return null;
            }
            i++;
            //vzdialenost od najblizsieho node
            Double time = Double.parseDouble(str.substring(i+1));
            //celkova vzdialenost od root
            Double depth = parentDepth + time;
            //nodes pod
            RootedExactNode left = parseNode(str.substring(1,ciarkaIndex), depth);
            RootedExactNode right = parseNode(str.substring(ciarkaIndex+1,koniecIndex), depth);

            //ulozi nazov nodu v spravnom poradi (napr. S1S2)
            String name;
            if(left.getName().compareTo(right.getName()) < 0){
                name = left.getName() + right.getName();
            } else {
                name = right.getName() + left.getName();
            }

            //nastavi hierarchiu
            node = new RootedExactNode(name, depth);
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
            if(colonIndex == -1){
                System.err.println("Wrong newick format leaf");
                return null;
            } else {
                name = str.substring(0, colonIndex);
                time = Double.parseDouble(str.substring(colonIndex + 1));
            }
            node = new RootedExactNode(name, time + parentDepth);
            return node;
        }
    }
}
