public class Interval {
    private double maxD;
    private double minD;
    // nemusim si pre interval pamatat ci je otvoreny alebo uzavrety, kde sa to prejavi je len pri pocitani DL
    // intervaly, ktore su z aspon jednej strany otvorene, maju rozdielny minD a maxD
    // ked pri urcovani DL vezmem priemer maxD a minD, nemam problem s otvorenymi intervalmi

    private DL dl; //celkove DL v podstrome, ked sa vrchol namapuje do hlbky z tohto intervalu

    //len pre posunuty interval, je to priemer maxD a minD povodneho intervalu pred posunom
    private double originalMappingDepth;

    public Interval(double maxD, double minD){
        this.maxD = maxD;
        this.minD = minD;
    }

    public double getMaxD() {
        return maxD;
    }

    public void setMaxD(double maxD) {
        this.maxD = maxD;
    }

    public double getMinD() {
        return minD;
    }

    public void setMinD(double minD) {
        this.minD = minD;
    }

    public DL getDl() {
        return dl;
    }

    public void setDl(DL dl) {
        this.dl = dl;
    }

    public double getOriginalMappingDepth() {
        return originalMappingDepth;
    }

    public void setOriginalMappingDepth(double originalMappingDepth) {
        this.originalMappingDepth = originalMappingDepth;
    }
}
