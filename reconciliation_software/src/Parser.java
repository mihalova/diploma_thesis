import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.TreeMap;

class Parser {
    static UnrootedTree G;
    static int k;

    static TreeMap<String, String> parseLeafMap(String prefix){
        TreeMap<String, String> leafMap = new TreeMap<>();
        try (Scanner s = new Scanner(new File(prefix + "/Greal.pruned.leafmap"))) {
            while (s.hasNextLine()) {
                String[] line = s.nextLine().split("\\s+");
                leafMap.put(line[0], line[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return leafMap;
    }

    static UnrootedTree parseUnrootedTree(String prefix, String filename) {
        UnrootedTree G = new UnrootedTree();

        try (Scanner s = new Scanner(new File(prefix + "/" + filename))) {
            while(s.hasNextLine()) {
                String[] line = s.nextLine().split("\\s+");
                String uName = line[0];
                String vName = line[1];
                double minL = Double.parseDouble(line[2]);
                double maxL = Double.parseDouble(line[3]);

                UnrootedNode u = new UnrootedNode(uName);
                int i = G.indexOfNode(u);
                if(i > -1){
                    u = G.getNode(i);
                } else {
                    G.addNode(u);
                }

                UnrootedNode v = new UnrootedNode(vName);
                i = G.indexOfNode(v);
                if(i > -1){
                    v = G.getNode(i);
                } else {
                    G.addNode(v);
                }

                Edge e = new Edge(u, v, minL, maxL);
                u.addEdge(e);
                v.addEdge(e);
                G.addEdge(e);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return G;
    }

    static UnrootedTree parseUnrootedNewick(File treeFile) {
        G = new UnrootedTree();
        k = 0;

        try (Scanner s = new Scanner(treeFile)) {
            String str = s.nextLine();
            int i = str.length() - 1;
            while(str.charAt(i) != ')'){
                i--;
            }
            str = str.substring(1, i);

            i = 0;
            int counter = 0;
            while(!(str.charAt(i) == ',' && counter == 0)){
                if(str.charAt(i) == '(') counter++;
                if(str.charAt(i) == ')') counter--;
                i++;
            }
            int firstCommaIndex = i;
            i++;

            while(!(str.charAt(i) == ',' && counter == 0)){
                if(str.charAt(i) == '(') counter++;
                if(str.charAt(i) == ')') counter--;
                i++;
            }
            int secondCommaIndex = i;

            UnrootedNode node = new UnrootedNode("node"+k);
            k++;
            G.addNode(node);
            UnrootedNode first = parseNewickUnrootedNode(
                    str.substring(0, firstCommaIndex), node);
            UnrootedNode second = parseNewickUnrootedNode(
                    str.substring(firstCommaIndex + 1, secondCommaIndex), node);
            UnrootedNode third = parseNewickUnrootedNode(
                    str.substring(secondCommaIndex + 1), node);

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

    private static UnrootedNode parseNewickUnrootedNode(String str, UnrootedNode nodeFrom) {
        int colonIndex = str.lastIndexOf(':');
        Double time = Double.parseDouble(str.substring(colonIndex + 1));
        str = str.substring(0, colonIndex);
        if(!str.startsWith("(")){
            UnrootedNode node = new UnrootedNode(str);
            G.addNode(node);
            Edge edgeFrom = new Edge(node, nodeFrom, time, time);
            G.addEdge(edgeFrom);
            node.addEdge(edgeFrom);
            return node;
        } else {
            UnrootedNode node = new UnrootedNode("node"+k);
            k++;
            G.addNode(node);
            Edge edgeFrom = new Edge(node, nodeFrom, time, time);
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
                    str.substring(1, commaIndex), node);
            UnrootedNode second = parseNewickUnrootedNode(
                    str.substring(commaIndex + 1, str.length() - 1), node);

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

    static RootedExactTree parseRootedTree(String prefix, String fileName) {
        RootedExactTree rooted = new RootedExactTree();
        try (Scanner s = new Scanner(new File(prefix + "/" + fileName))) {
            while (s.hasNextLine()) {
                String[] line = s.nextLine().split("\\s+");
                String parentName = line[0];
                String childName = line[1];
                double edgeLength = Double.parseDouble(line[2]);

                RootedExactNode parent = rooted.findNode(parentName);

                if(parent == null) {
                    System.err.println("Problem with parsing " + fileName + ": Node with name " +
                            parentName + " doesn't exist.");
                    return null;
                }

                if(parent.getLeft() != null && parent.getRight() != null) {
                    System.err.println("Problem with parsing " + fileName + ": Cannot add node " +
                            childName + ", parent " + parentName + " already has two children.");
                    return null;
                }

                RootedExactNode child = new RootedExactNode(childName, parent.getD() + edgeLength);
                child.setParent(parent);
                if (parent.getLeft() == null) {
                    parent.setLeft(child);
                } else {
                    parent.setRight(child);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return rooted;
    }

    static RootedExactTree parseRootedNewick(String prefix, String fileName){
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
            Double depth = Double.parseDouble(str.substring(k+2,str.length()-1));
            t.getRoot().setD(depth);
            RootedExactNode left = parseNewickNode(str.substring(1,i), depth);
            RootedExactNode right = parseNewickNode(str.substring(i+1,k), depth);
            left.setParent(t.getRoot());
            right.setParent(t.getRoot());
            t.getRoot().setLeft(left);
            t.getRoot().setRight(right);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return t;
    }

    private static RootedExactNode parseNewickNode(String str, double parentDepth){
        RootedExactNode node;
        if(str.startsWith("(")){
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
            Double time = Double.parseDouble(str.substring(i+1));
            Double depth = parentDepth + time;
            RootedExactNode left = parseNewickNode(str.substring(1,ciarkaIndex), depth);
            RootedExactNode right = parseNewickNode(str.substring(ciarkaIndex+1,koniecIndex), depth);

            String name;
            if(left.getName().compareTo(right.getName()) < 0){
                name = left.getName() + right.getName();
            } else {
                name = right.getName() + left.getName();
            }

            node = new RootedExactNode(name, depth);
            left.setParent(node);
            right.setParent(node);
            node.setLeft(left);
            node.setRight(right);
            return node;
        } else {
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
