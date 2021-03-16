package Software;

public class Four<W, X, Y, Z> {
    private final W w;
    private final X x;
    private final Y y;
    private final Z z;
    public Four(W w, X x, Y y, Z z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public W getFirst(){
        return w;
    }
    public X getSecond(){
        return x;
    }
    public Y getThird(){
        return y;
    }
    public Z getFourth(){
        return z;
    }
}
