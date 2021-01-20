package Test;

import Software.*;
import org.junit.jupiter.api.Test;

import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class RootedIntervalTreeTest {

    @Test
    void countDLTest1(){
        System.out.println("---Test 1---");
        //gene tree 1
        RootedIntervalTree G = getGtree1_1();
        G.countDL(G.getRoot());
        System.out.println("Losses: "+G.getTotalDL().getLoss()+" Duplications: "+G.getTotalDL().getDuplication());
        assertEquals(G.getTotalDL().getSum(), 0);
        System.out.println("");
        //gene tree 2
        G = getGtree1_2();
        G.countDL(G.getRoot());
        System.out.println("Losses: "+G.getTotalDL().getLoss()+" Duplications: "+G.getTotalDL().getDuplication());
        assertEquals(G.getTotalDL().getSum(), 6);
    }


    @Test
    void countDLTest2(){
        System.out.println("---Test 2---");
        //gene tree 1
        RootedIntervalTree G = getGtree2_1();
        G.countDL(G.getRoot());
        System.out.println("Losses: "+G.getTotalDL().getLoss()+" Duplications: "+G.getTotalDL().getDuplication());
        assertEquals(G.getTotalDL().getSum(), 2);
        System.out.println("");
        //gene tree 2
        G = getGtree2_2();
        G.countDL(G.getRoot());
        System.out.println("Losses: "+G.getTotalDL().getLoss()+" Duplications: "+G.getTotalDL().getDuplication());
        assertEquals(G.getTotalDL().getSum(), 5);
    }


    RootedExactNode[] getS1(){
        RootedExactNode A = new RootedExactNode("A", 3.0);
        RootedExactNode B = new RootedExactNode("B", 3.0);
        RootedExactNode C = new RootedExactNode("C", 3.0);
        RootedExactNode X = new RootedExactNode("X", 2.0);
        RootedExactNode Y = new RootedExactNode("Y", 0.0);
        A.setParent(X);
        B.setParent(X);
        C.setParent(Y);
        X.setParent(Y);
        X.setLeft(A);
        X.setRight(B);
        Y.setLeft(X);
        Y.setRight(C);
        return new RootedExactNode[]{A, B, C, X, Y};
    }

    RootedExactNode[] getS2(){
        RootedExactNode A = new RootedExactNode("A", 1.0);
        RootedExactNode B = new RootedExactNode("B", 2.0);
        RootedExactNode R = new RootedExactNode("R", 0.0);
        A.setParent(R);
        B.setParent(R);
        R.setLeft(A);
        R.setRight(B);
        return new RootedExactNode[]{A, B, R};
    }

    RootedIntervalNode getG1(RootedExactNode[] nodesS, double[] depths){
        RootedIntervalNode a = new RootedIntervalNode("a");
        a.setMaxD(depths[0]);
        a.setLcaS(nodesS[0]);
        RootedIntervalNode b = new RootedIntervalNode("b");
        b.setMaxD(depths[1]);
        b.setLcaS(nodesS[1]);
        RootedIntervalNode c = new RootedIntervalNode("c");
        c.setMaxD(depths[2]);
        c.setLcaS(nodesS[2]);
        RootedIntervalNode u = new RootedIntervalNode("u");
        u.setMaxD(depths[3]);
        u.setLcaS(nodesS[3]);
        RootedIntervalNode r = new RootedIntervalNode("r");
        r.setMaxD(depths[4]);
        r.setLcaS(nodesS[4]);
        a.setParent(u);
        b.setParent(u);
        c.setParent(r);
        u.setParent(r);
        u.setLeft(a);
        u.setRight(b);
        r.setLeft(u);
        r.setRight(c);
        return r;
    }

    RootedIntervalNode getG2(RootedExactNode[] nodesS, double[] depths){
        RootedIntervalNode a1 = new RootedIntervalNode("a1");
        a1.setMaxD(depths[0]);
        a1.setLcaS(nodesS[0]);
        RootedIntervalNode a2 = new RootedIntervalNode("a2");
        a2.setMaxD(depths[1]);
        a2.setLcaS(nodesS[0]);
        RootedIntervalNode b1 = new RootedIntervalNode("b1");
        b1.setMaxD(depths[2]);
        b1.setLcaS(nodesS[1]);
        RootedIntervalNode u = new RootedIntervalNode("u");
        u.setMaxD(depths[3]);
        u.setLcaS(nodesS[2]);
        RootedIntervalNode q = new RootedIntervalNode("q");
        q.setMaxD(depths[4]);
        q.setLcaS(nodesS[2]);
        a1.setParent(q);
        a2.setParent(u);
        b1.setParent(u);
        u.setParent(q);
        u.setLeft(a2);
        u.setRight(b1);
        q.setLeft(a1);
        q.setRight(u);
        return q;
    }

    TreeMap<String, String> getLeafMap1(){
        //leaf mapping
        TreeMap<String, String> leafMap = new TreeMap<>();
        leafMap.put("a", "A");
        leafMap.put("b", "B");
        leafMap.put("c", "C");
        return leafMap;
    }

    TreeMap<String, String> getLeafMap2(){
        //leaf mapping
        TreeMap<String, String> leafMap = new TreeMap<>();
        leafMap.put("a1", "A");
        leafMap.put("a2", "A");
        leafMap.put("b1", "B");
        return leafMap;
    }

    RootedIntervalTree getGtree1_1(){
        //species tree
        RootedExactNode[] nodesS = getS1();
        RootedExactTree S = new RootedExactTree();
        S.setRoot(nodesS[4]);
        //gene tree
        System.out.println("Gene tree 1");
        double[] depths = {3.0, 3.0, 3.0, 2.0, 0.0};
        RootedIntervalNode g = getG1(nodesS, depths);
        RootedIntervalTree G = new RootedIntervalTree(g, S, getLeafMap1());
        return G;
    }

    RootedIntervalTree getGtree1_2(){
        //species tree
        RootedExactNode[] nodesS = getS1();
        RootedExactTree S = new RootedExactTree();
        S.setRoot(nodesS[4]);
        //gene tree
        System.out.println("Gene tree 2");
        double[] depths = {3.0, 3.0, 3.0, 1.0, -1.0};
        RootedIntervalNode g = getG1(nodesS, depths);
        RootedIntervalTree G = new RootedIntervalTree(g, S, getLeafMap1());
        return G;
    }

    RootedIntervalTree getGtree2_1(){
        //species tree
        RootedExactNode[] nodesS = getS2();
        RootedExactTree S = new RootedExactTree();
        S.setRoot(nodesS[2]);
        //gene tree
        System.out.println("Gene tree 1");
        double[] depths = {1.0, 1.0, 2.0, 0.0, -1.0};
        RootedIntervalNode g = getG2(nodesS, depths);
        RootedIntervalTree G = new RootedIntervalTree(g, S, getLeafMap2());
        return G;
    }

    RootedIntervalTree getGtree2_2(){
        //species tree
        RootedExactNode[] nodesS = getS2();
        RootedExactTree S = new RootedExactTree();
        S.setRoot(nodesS[2]);
        //gene tree
        System.out.println("Gene tree 2");
        double[] depths = {1.0, 1.0, 2.0, -1.0, -2.0};
        RootedIntervalNode g = getG2(nodesS, depths);
        RootedIntervalTree G = new RootedIntervalTree(g, S, getLeafMap2());
        return G;
    }

}