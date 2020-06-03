public class DL {
    private final int duplication;
    private final int loss;

    public DL(int duplication, int loss) {
        this.duplication = duplication;
        this.loss = loss;
    }

    public int getDuplication() {
        return duplication;
    }

    public int getLoss() {
        return loss;
    }

    public int getSum(){
        return duplication + loss;
    }

    @Override public boolean equals(Object obj) {
        DL o = (DL) obj;
        return o != null && this.duplication == o.duplication && this.loss == o.loss;
    }
}
