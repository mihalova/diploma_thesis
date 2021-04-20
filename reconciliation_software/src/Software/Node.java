package Software;

public class Node {
    private String name;
    int level;

    Node(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override public boolean equals(Object obj) {
        Node o = (Node) obj;
        return o != null && this.name.equals(o.name);
    }
    public int getLevel() {
        return level;
    }

    public void setLevel(int l) {
        this.level = l;
    }


}
