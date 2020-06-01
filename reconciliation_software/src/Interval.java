public class Interval {
    private double maxDepth;
    private double minDepth;
    // nemusim si pre interval pamatat ci je otvoreny alebo uzavrety, kde sa to prejavi je len pri pocitani DL
    // intervaly, ktore su z aspon jednej strany otvorene, maju rozdielny minD a maxD
    // ked pri urcovani DL vezmem priemer maxD a minD, nemam problem s otvorenymi intervalmi

    private DL dl; //celkove DL v podstrome, ked sa vrchol namapuje do hlbky z tohto intervalu

    //len pre posunuty interval, je to priemer maxD a minD povodneho intervalu pred posunom
    private double originalMappingDepth;

    public Interval(double maxDepth, double minDepth){
        this.maxDepth = maxDepth;
        this.minDepth = minDepth;
    }

    public double getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(double maxDepth) {
        this.maxDepth = maxDepth;
    }

    public double getMinDepth() {
        return minDepth;
    }

    public void setMinDepth(double minDepth) {
        this.minDepth = minDepth;
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
