package Software;

import java.util.*;

public class UnrootedTree {

    private ArrayList<UnrootedNode> nodes;
    private TreeMap<String, String> leafMap;
    private ArrayList<Edge> edges;

    public UnrootedTree(){
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        leafMap = new TreeMap<>();
    }

    public ArrayList<UnrootedNode> getNodes() {
        return nodes;
    }

    public UnrootedNode getNode(int i) {
        return nodes.get(i);
    }

    public void addNode(UnrootedNode node) {
        nodes.add(node);
    }

    public int indexOfNode(UnrootedNode node){
        return nodes.indexOf(node);
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public Edge getEdge(int i) {
        return edges.get(i);
    }

    public void addEdge(Edge e) {
        edges.add(e);
    }

    public void addMapping(String geneNode, String speciesNode){
        leafMap.put(geneNode, speciesNode);
    }

    public TreeMap<String, String> getLeafMap(){
        return leafMap;
    }

    public void renameNodes() {
        for(UnrootedNode node: nodes){
            if(node.getEdges().size() == 1){
                continue;
            }
            ArrayList<String> names = new ArrayList<>();
            for(Edge e: node.getEdges()){
                UnrootedNode otherNode = e.otherNode(node);
                TreeSet<String> leaves = leaves(e, otherNode);
                String resultingName = "";
                for(String name: leaves){
                    resultingName += name;
                }
                names.add(resultingName);
            }
            Collections.sort(names);
            String nodeName = "";
            for (int i = 0; i < names.size(); i++){
                nodeName += names.get(i);
                if (i != names.size()-1)
                    nodeName += "|";
            }
            node.setName(nodeName);
        }
    }

    private TreeSet<String> leaves(Edge sourceEdge, UnrootedNode node){
        TreeSet<String> result = new TreeSet<>();
        if(node.getEdges().size() == 1){
            result.add(node.getName());
            return result;
        }
        for(Edge e: node.getEdges()){
            if(e.equals(sourceEdge)){
                continue;
            }
            UnrootedNode otherNode = e.otherNode(node);
            result.addAll(leaves(e, otherNode));
        }
        return result;
    }

    public void setLeafMap(TreeMap<String, String> leafMap) {
        this.leafMap = leafMap;
    }

	/*public boolean equalTopology(Software.UnrootedTree other) {
		for (Software.Edge e : other.getEdges()) {
			if (!edges.contains(e)) {
				return false;
			}
		}
		return edges.size() == other.getEdges().size();
	}

    public void createFromTrees(ArrayList<Software.UnrootedTree> viableTrees, String method, double methodParam) {
		ArrayList<ArrayList<Software.UnrootedNode>> listsOfNodes = new ArrayList<>();
		for(Software.UnrootedTree tree: viableTrees){
			Collections.sort(tree.nodes, new Comparator<Software.UnrootedNode>(){
				public int compare(Software.UnrootedNode n1,Software.UnrootedNode n2){
					return n1.getName().compareTo(n2.getName());
				}});
			listsOfNodes.add(tree.nodes);
		}
		for (int i = 0; i < listsOfNodes.get(0).size(); i++) {
			Software.UnrootedNode newNode = new Software.UnrootedNode(listsOfNodes.get(0).get(i).getName());
			if(!nodes.contains(newNode)){
				nodes.add(newNode);
			} else {
				newNode = nodes.get(nodes.indexOf(newNode));
			}
			ArrayList<ArrayList<Double>> edgeLengths = new ArrayList<>();
			ArrayList<String> nodeNames = new ArrayList<>();
			for (int j = 0; j < listsOfNodes.size(); j++) {
				Software.UnrootedNode node = listsOfNodes.get(j).get(i);
				Collections.sort(node.getEdges(), new Comparator<Software.Edge>(){
					public int compare(Software.Edge e1, Software.Edge e2){
						return e1.otherNode(node).getName().compareTo(e2.otherNode(node).getName());
					}});
				ArrayList<Double> listForNode = new ArrayList<>();
				for (int k = 0; k < node.getEdges().size(); k++) {
					if(j==0) {
						nodeNames.add(node.getEdges().get(k).otherNode(node).getName());
					}
					listForNode.add(node.getEdges().get(k).getMinLength());
				}
				edgeLengths.add(listForNode);
			}
			ArrayList<ArrayList<Double>> lensForEdge = new ArrayList<>();
			boolean oneEdge = false;
			for (int k = 0; k < 3; k++) {
				if(oneEdge) break;
				lensForEdge.add(new ArrayList<>());
				for (int j = 0; j < listsOfNodes.size(); j++){
					Software.UnrootedNode node = listsOfNodes.get(j).get(i);
					if(node.getEdges().size() == 1) oneEdge = true;
					lensForEdge.get(k).add(edgeLengths.get(j).get(k));
				}
			}
			for (int j = 0; j < lensForEdge.size(); j++) {
			    double[] interval = getInterval(method, methodParam, lensForEdge.get(j));
				Software.UnrootedNode otherNode = new Software.UnrootedNode(nodeNames.get(j));
				if(nodes.contains(otherNode)){
					otherNode = nodes.get(nodes.indexOf(otherNode));
				} else {
					nodes.add(otherNode);
				}
				Software.Edge e = new Software.Edge(newNode, otherNode, interval[0], interval[1]);
				if(!edges.contains(e)) {
					edges.add(e);
					newNode.addEdge(e);
					otherNode.addEdge(e);
				}
			}
		}
		Collections.sort(nodes, new Comparator<Software.UnrootedNode>(){
			public int compare(Software.UnrootedNode n1,Software.UnrootedNode n2){
				return n1.getName().compareTo(n2.getName());
			}});
	}

    private static double[] getInterval(String method, Double param, ArrayList<Double> lens) {
	double[] result = new double[2];
	int size = lens.size();
	if(method.equals("p")) { // percentil
	    Collections.sort(lens);
	    int lowerIndex = (int) Math.floor(param * size);
	    if(lowerIndex >= size) {
		lowerIndex = size-1;
	    }
	    int upperIndex = size - 1 - lowerIndex;
	    if(upperIndex < lowerIndex) {
		upperIndex = size/2;
		lowerIndex = size/2;
	    }
	    result[0] = lens.get(lowerIndex);
	    result[1] = lens.get(upperIndex);
	} else {
	    double sum = 0;
	    double sum2 = 0;
	    for(double val : lens) {
		sum += val;
		sum2 += val*val;
	    }
	    double mean = sum / size;
	    double var = sum2 / size - mean*mean;
	    double std = Math.sqrt(var);
	    if(method.equals("d")) { // deviation
		result[0] = mean - param * std;
		result[1] = mean + param * std;
	    }
	    else if(method.equals("r")) {
		double diff = mean * param;
		result[0] = mean - diff;
		result[1] = mean + diff;
	    }
	    else if(method.equals("a")) {
		result[0] = mean - param;
		result[1] = mean + param;
	    }

	    if(result[0]<0) {
		result[0] = 0;
	    }
	}
	return result;
    }
*/
}
