public class DL {
    private final int d;
    private final int l;

    public DL(int d, int l) {
        this.d = d;
        this.l = l;
    }

    public int getD() {
        return d;
    }

    public int getL() {
        return l;
    }

    public int getSum(){
        return d + l;
    }

    @Override public boolean equals(Object obj) {
        DL o = (DL) obj;
        return o != null && this.d == o.d && this.l == o.l;
    }
}
