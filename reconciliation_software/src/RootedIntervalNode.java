import java.util.ArrayList;

class RootedIntervalNode extends Node {

    //minimalna, maximalna hlbka namapovania vrcholu
    private double minD;
    private double maxD;

    private double depth; //hlbka, ktora (snad) minimalizuje pocet udalosti
    private DL totalDL; //totalny pocet udalosti v podstrome - ma ho definovany len koren

    private double minL;
    private double maxL;

    private RootedIntervalNode parent;
    private RootedIntervalNode left;
    private RootedIntervalNode right;

    private RootedExactNode lcaS;

    private ArrayList<Interval> intervals = new ArrayList<>();

    public RootedIntervalNode(String name) {
        super(name);
    }

    public double getMinD() {
        return minD;
    }

    public void setMinD(double minD) {
        this.minD = Reconciliation.round(minD);
    }

    public double getMaxD() {
        return maxD;
    }

    public void setMaxD(double maxD) {
        this.maxD = Reconciliation.round(maxD);
    }

    public double getMinL() {
        return minL;
    }

    public void setMinL(double minL) {
        this.minL = Reconciliation.round(minL);
    }

    public double getMaxL() {
        return maxL;
    }

    public void setMaxL(double maxL) {
        this.maxL = Reconciliation.round(maxL);
    }

    public RootedIntervalNode getParent() {
        return parent;
    }

    public void setParent(RootedIntervalNode parent) {
        this.parent = parent;
    }

    public RootedIntervalNode getLeft() {
        return left;
    }

    public void setLeft(RootedIntervalNode left) {
        this.left = left;
    }

    public RootedIntervalNode getRight() {
        return right;
    }

    public void setRight(RootedIntervalNode right) {
        this.right = right;
    }

    public RootedExactNode getLcaS() {
        return lcaS;
    }

    public void setLcaS(RootedExactNode lcaS) {
        this.lcaS = lcaS;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public Interval getInterval(int i){
        if(intervals.isEmpty()) return null;
        return intervals.get(i);
    }

    public int intervalsSize(){
        return intervals.size();
    }

    // rozdeli interval namapovania vrchola na podintervaly a vypocita celkove najmensie DL pre kazdy z nich
    // intervaly s rovnakym DL sa spoja - tymto sposobom intervalov nikdy nebude viac ako O(N^2)
    public void countSubintervalDL() {
        if(left == null){
            Interval interval = new Interval(maxD, minD);
            interval.setDl(new DL(0,0));
            intervals.add(interval);
            return;
        }

        // deti uz maju vypocitane DL vo svojich podintervaloch
        left.countSubintervalDL();
        right.countSubintervalDL();

        ArrayList<Interval> splitInterval = splitInterval(); //rozdelenie <maxD,minD> na podintervaly (ulozene v zozname intervals)

        //ziskame neprekryvajuce sa intervaly deti posunute o dlzku rodicovskej hrany v prieniku s <maxD,minD>
        ArrayList<Interval> shiftedLeftIntervals = left.getShiftedIntervals();
        ArrayList<Interval> shiftedRightIntervals = right.getShiftedIntervals();

        int i=0,j=0,k=0;

        double max = maxD;
        double min;

        Interval middleI;
        Interval leftI;
        Interval rightI;

        DL previousDL = new DL(-1,-1);
        double previousMax = Double.MAX_VALUE;

        Interval newInterval;

        do {
            middleI = splitInterval.get(i);
            leftI = shiftedLeftIntervals.get(j);
            rightI = shiftedRightIntervals.get(k);

            min = Math.max(middleI.getMinDepth(), Math.max(leftI.getMinDepth(), rightI.getMinDepth()));

            DL dl = countDL((middleI.getMaxDepth() + middleI.getMinDepth()) / 2,
                    leftI.getOriginalMappingDepth(),
                    rightI.getOriginalMappingDepth());

            DL newTotalDL = new DL(dl.getDuplication() + leftI.getDl().getDuplication() + rightI.getDl().getDuplication(),
                    dl.getLoss() + leftI.getDl().getLoss() + rightI.getDl().getLoss());

            if(!newTotalDL.equals(previousDL) && previousMax != Double.MAX_VALUE){
                newInterval = new Interval(previousMax, max);
                newInterval.setDl(previousDL);
                intervals.add(newInterval);
            }

            previousMax = max;
            previousDL = newTotalDL;

            max = min;

            //sprava sa zle pre nespresne hodnoty double premennych...
            if(middleI.getMinDepth() == min) i++;
            if(leftI.getMinDepth() == min) j++;
            if(rightI.getMinDepth() == min) k++;

        } while (min != minD);

        newInterval = new Interval(previousMax, minD);
        newInterval.setDl(previousDL);
        intervals.add(newInterval);
    }

    // vytvori usporiadany zoznam podintervalov intervalu <maxD, minD>, ktore maju rozny pocet DL
    // deliace miesta su hlbky vnutornych vrcholov S na ceste od lcaS po koren S, ktore maju hlbky v intervale <maxD,minD>
    private ArrayList<Interval> splitInterval(){
        ArrayList<Interval> result = new ArrayList<>();
        double max = maxD;

        //zacneme od najblizsieho nizsieho vrchola od maxD, teda bearingNode(maxD)
        RootedExactNode temp = getBearingNode(maxD);

        if(temp.getDepth() > lcaS.getDepth()){
            //bearing node moze byt pod lca, len ak mame nulovu hranu
            System.err.println("Duplikacia sa mapuje do lca - nulova hrana");
        }

        //ked sa na zaciatku nachadzame presne v nejakom vrchole, pridame interval len v hlbke tohto vrchola
        if(temp.getDepth() == max){
            result.add(new Interval(max,max));
        }

        //na zaciatku kazdeho cyklu sme niekde na hrane nad vrcholom temp
        while(true){
            temp = temp.getParent();
            if(temp == null || minD > temp.getDepth()){
                //ak temp nema rodica, mapujeme sa nad koren a nasli sme posledny interval
                //ak minimalna hlbka mozneho namapovania je nizsie ako rodic, tiez skoncime
                result.add(new Interval(max, minD));
                break;
            }

            // urcite je rodic uz vyssie ako v lca, teda speciacia tam nenastane
            // zaroven duplikacia vo vrchole sa namapuje tesne pod vrchol
            // => mozeme vzdy priamo vytvorit interval (max, temp.D)
            result.add(new Interval(max, temp.getDepth()));

            max = temp.getDepth();
        }
        return result;
    }

    private boolean isDuplication(double mappingDepth){
        if(left == null) return false; //list nie je duplikacia
        //vnutorny vrchol genoveho stromu je duplikacia, ked jeho lcaS je rovnake ako jedneho z jeho deti
        //taktiez ked to je speciacia (nesplna predoslu podmienku) ale mapuje sa vyssie ako do lcaS
        return lcaS.equals(left.lcaS) || lcaS.equals(right.lcaS) || mappingDepth < lcaS.getDepth();
    }

    // funkcia, ktora ako berie ako parametre parcialne mapovania vrchola a jeho deti
    // vrati (DL v podstrome s korenom v u) - (DL podstromu laveho dietata + DL podstromu praveho dietata)
    public DL countDL(double mappingDepth, double leftMappingDepth, double rightMappingDepth){
        int dup = 0;
        int del;

        //bearing nodes seba a svojich deti
        RootedExactNode bNode = getBearingNode(mappingDepth);
        RootedExactNode leftBNode = left.getBearingNode(leftMappingDepth);
        RootedExactNode rightBNode = right.getBearingNode(rightMappingDepth);

        //lca bearing node-ov deti
        RootedExactNode lcaLR = RootedExactTree.lca(leftBNode, rightBNode);

        //dlzka cesty medzi bearing node-mi deti (vyuzije sa v duplikacii aj speciacii)
        int childrenBpathLength = RootedExactTree.pathLength(leftBNode, rightBNode);

        if(mappingDepth == leftMappingDepth || mappingDepth == rightMappingDepth){
            System.err.println("Otec sa mapuje do rovnakej vysky ako jedno z deti - " + this.getName());
        }

        if(isDuplication(mappingDepth)) {
            // kedze som duplikacia, nemozem sa mapovat presne do lca - iba ak dovolujem nulove hrany
            if(mappingDepth == lcaS.getDepth()) {
                System.err.println("Duplikacia sa mapuje presne do lca - nulova hrana - " + this.getName());
            }
            dup = 1;

            // delecie po ceste od bNode po lca bearing node-ove deti
            del = 2 * (RootedExactTree.pathLengthToAncestor(lcaLR, bNode) - 1);

            // delecie na ceste medzi namapovanymi detmi
            // pocet delecii je rovnaky pre pripad ked je mapovanie niektoreho dietata predkom druheho, aj ked nie je
            del += childrenBpathLength - 1;
        } else {
            // speciacia sa mapuje presne do lca, po ceste z lca vsak tiez mohlo dojst k deleciam v druhoch, kam sa gen nedostal

            // je to podobne pripadu pri duplikacii, kde mapovanie niektoreho dieta nie je predkom druheho
            // s rozdielom, ze do lca bearing node-ov deti vstupuje len jeden gen (mame o dve delecie menej hned po lca)
            del = childrenBpathLength - 3;
        }

        if(del < 0){
            System.err.println("Pocet delecii zaporny - " + this.getName());
        }

        return new DL(dup, del);
    }

    // vrati bearing node - najblizsi vrchol pod danou hlbkou v S, na ceste z lcaS do rootu S
    public RootedExactNode getBearingNode(double mappingDepth){
        RootedExactNode node = lcaS;

        //zacni hladanie v lcaS, pokracuj do rodicov, kym nenajdes bearing node
        while(node.getDepth() > mappingDepth){
            // node z S je bearing node, ked:
            // je koren (namapovanie musi byt nad korenom)
            // rodic ma mensiu hlbku ako hlbka namapovania
            // mapujem sa presne do rodica, ale kedze som duplikacia, nemapujem sa priamo do vrchola v S ale tesne pod neho
            if(node.getParent() == null || node.getParent().getDepth() < mappingDepth ||
                    (node.getParent().getDepth() == mappingDepth && isDuplication(mappingDepth))){
                break;
            }
            node = node.getParent();
        }

        return node;
    }

    // vrati neprekryvajuce sa intervaly posunute o interval dlzky rodicovskej hrany
    // tuto metodu vzdy vola iba rodic, v ramci metody countSubintervalDL
    private ArrayList<Interval> getShiftedIntervals() {
        ArrayList<Interval> result = new ArrayList<>();

        double previousMin = Double.MAX_VALUE; //najmensia hlbka z predosleho spracovaneho intervalu = najvacsia hlbka

        for (Interval interval : intervals) {
            double shiftedIntervalMax = Reconciliation.round(interval.getMaxDepth() - minL);
            double shiftedIntervalMin = Reconciliation.round(interval.getMinDepth() - maxL);
            if(shiftedIntervalMin > parent.maxD){
                //intervaly, ktore sa aj po posunuti nachadzaju hlbsie ako najhlbsie namapovanie rodica, neberieme do uvahy
                continue;
            }
            if(result.isEmpty()){
                // som v prvom relevantnom intervale(jeho minimum >= parent.maxD)
                // urezem ho zospodu na najhlbsom namapovani rodica
                // najhlbsi bod prveho relevantneho intervalu musi byt hlbsi ako parent.maxD
                // z toho vyplyva, ze mi mozu v specialnom pripade vzniknut prve dva rovnake intervaly, to ale asi nebude vadit
                previousMin = parent.maxD;
            }

            if(previousMin < shiftedIntervalMin) {
                //predosly interval pokryl cely tento interval - nemalo by sa stat
                System.err.println("Predosly interval pokryl cely tento interval");
            }
            if(previousMin > shiftedIntervalMax) {
                //zaroven by malo platit, ze previousMin <= shiftedIntervalMax
                // TODO nepresne double s malymi rozdielmi - staci ked som priebezne zaokruhloval?
                System.err.println("Predosly interval konci nizsie ako zaciatok tohto intervalu");
            }

            if(shiftedIntervalMin <= parent.minD){
                // ak sa najmenej hlboky bod intervalu nachadza vyssie alebo rovnako ako minimalna hlbka namapovania rodica,
                // nasli sme posledny interval, ktory urezeme zhora na minimalnej hlbke rodica a skoncime
                shiftedIntervalMin = parent.minD;
            }

            Interval shiftedInterval = new Interval(previousMin, shiftedIntervalMin);
            shiftedInterval.setDl(interval.getDl());
            shiftedInterval.setOriginalMappingDepth((interval.getMaxDepth() + interval.getMinDepth())/2);
            result.add(shiftedInterval);
            previousMin = shiftedIntervalMin;

            if(shiftedIntervalMin == parent.minD) break;

        }

        return result;
    }

    public DL getTotalDL() {
        return totalDL;
    }

    public void setTotalDL(DL totalDL) {
        this.totalDL = totalDL;
    }
}