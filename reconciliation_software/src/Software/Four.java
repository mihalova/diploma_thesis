package Software;

public class Four<W, X, Y, Z> {
    public final W w;
    public final X x;
    public final Y y;
    public final Z z;
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
