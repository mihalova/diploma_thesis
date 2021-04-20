package Test;

import Software.*;
import org.junit.jupiter.api.Test;

import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class RootedIntervalTreeTest {

    private RootedIntervalNode[] nodes_G;
    private RootedExactNode[] nodes_S;

    @Test
    void countDL_Test1(){
        System.out.println("---Counting Algorithm - Species tree 1 - Gene Tree 1---");
        RootedIntervalTree G = getGtree1_1();
        G.computeLevel(G.getRoot());
        DL score = G.countDL(G.getRoot());
        System.out.println("Losses: "+score.getLoss()+" Duplications: "+score.getDuplication());
        assertEquals(0, score.getSum());
    }

    @Test
    void countDL_Test2(){
        System.out.println("---Counting Algorithm - Species tree 1 - Gene Tree 2---");
        RootedIntervalTree G = getGtree1_2();
        G.computeLevel(G.getRoot());
        DL score = G.countDL(G.getRoot());
        System.out.println("Losses: "+score.getLoss()+" Duplications: "+score.getDuplication());
        assertEquals(6, score.getSum());
    }


    @Test
    void countDL_Test3(){
        System.out.println("---Counting Algorithm - Species tree 2 - Gene tree 1---");
        RootedIntervalTree G = getGtree2_1();
        G.computeLevel(G.getRoot());
        DL score = G.countDL(G.getRoot());
        System.out.println("Losses: "+score.getLoss()+" Duplications: "+score.getDuplication());
        assertEquals(2, score.getSum());
    }

    @Test
    void countDL_Test4(){
        System.out.println("---Counting Algorithm - Species tree 2 - Gene tree 2---");
        RootedIntervalTree G = getGtree2_2();
        G.computeLevel(G.getRoot());
        DL score = G.countDL(G.getRoot());
        System.out.println("Losses: "+score.getLoss()+" Duplications: "+score.getDuplication());
        assertEquals(5, score.getSum());
    }

    @Test
    void countDLTest5(){
        System.out.println("---Counting Algorithm - Species tree 3 - Gene tree 1---");
        RootedIntervalTree G = getGtree3_1();
        G.computeLevel(G.getRoot());
        DL score = G.countDL(G.getRoot());
        System.out.println("Losses: "+score.getLoss()+" Duplications: "+score.getDuplication());
        assertEquals(6, score.getSum());
    }

    @Test
    void countDL_Test6() {
        System.out.println("---Counting Algorithm - Species tree 3 - Gene tree 2---");
        RootedIntervalTree G = getGtree3_2();
        G.computeLevel(G.getRoot());
        DL score = G.countDL(G.getRoot());
        System.out.println("Losses: " + score.getLoss() + " Duplications: " + score.getDuplication());
        assertEquals(9, score.getSum());
    }

    @Test
    void countDL_Test7(){
        System.out.println("---Counting Algorithm - Species tree 3 - Gene tree 3---");
        RootedIntervalTree G = getGtree3_3();
        G.computeLevel(G.getRoot());
        DL score = G.countDL(G.getRoot());
        System.out.println("Losses: "+score.getLoss()+" Duplications: "+score.getDuplication());
        assertEquals(9, score.getSum());
    }

    @Test
    void countDL_Test8(){
        System.out.println("---Counting Algorithm - Species tree 3 - Gene tree 4---");
        RootedIntervalTree G = getGtree3_4();
        G.computeLevel(G.getRoot());
        DL score = G.countDL(G.getRoot());
        System.out.println("Losses: "+score.getLoss()+" Duplications: "+score.getDuplication());
        assertEquals(10, score.getSum());
    }

    @Test
    void countDL_Test9(){
        System.out.println("---Counting Algorithm - Species tree 4 - Gene tree 1---");
        RootedIntervalTree G = getGtree4_1();
        G.computeLevel(G.getRoot());
        DL score = G.countDL(G.getRoot());
        System.out.println("Losses: "+score.getLoss()+" Duplications: "+score.getDuplication());
        assertEquals(12, score.getSum());
    }

    @Test
    void countDL_Test10(){
        System.out.println("---Counting Algorithm - Species tree 4 - Gene tree 2---");
        RootedIntervalTree G = getGtree4_2();
        G.computeLevel(G.getRoot());
        DL score = G.countDL(G.getRoot());
        System.out.println("Losses: "+score.getLoss()+" Duplications: "+score.getDuplication());
        assertEquals(11, score.getSum());
    }

    @Test
    void countDL_Test11(){
        System.out.println("---Counting Algorithm - Species tree 4 - Gene tree 3---");
        RootedIntervalTree G = getGtree4_3();
        G.computeLevel(G.getRoot());
        DL score = G.countDL(G.getRoot());
        System.out.println("Losses: "+score.getLoss()+" Duplications: "+score.getDuplication());
        assertEquals(0, score.getSum());
    }

    @Test
    void variables_Test1(){
        System.out.println("---Variables - Species tree 1 - Gene tree 1---");
        RootedIntervalTree G = getGtree1_1();
        G.computeLevel(G.getRoot());
        //nodes_S - A, B, C, X, Y
        //nodes_G - a, b, c, u, r
        //speciesNodeBelow
        assertEquals(nodes_S[0].getName(), nodes_G[0].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[1].getName(), nodes_G[1].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[2].getName(), nodes_G[2].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[3].getName(), nodes_G[3].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[4].getName(), nodes_G[4].getSpeciesNodeBelow().getName());
        //levelS
        assertEquals(2, nodes_G[0].getLevel());
        assertEquals(2, nodes_G[1].getLevel());
        assertEquals(1, nodes_G[2].getLevel());
        assertEquals(1, nodes_G[3].getLevel());
        assertEquals(0, nodes_G[4].getLevel());
        //levelDistanceFromParent
        assertEquals(0, nodes_G[0].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[1].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[2].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[3].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[4].getLevelDistanceFromParent());
    }

    @Test
    void variables_Test2(){
        System.out.println("---Variables - Species tree 1 - Gene tree 2---");
        RootedIntervalTree G = getGtree1_2();
        G.computeLevel(G.getRoot());
        //nodes_S - A, B, C, X, Y
        //nodes_G - a, b, c, u, r
        //speciesNodeBelow
        assertEquals(nodes_S[0].getName(), nodes_G[0].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[1].getName(), nodes_G[1].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[2].getName(), nodes_G[2].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[3].getName(), nodes_G[3].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[4].getName(), nodes_G[4].getSpeciesNodeBelow().getName());
        //levelS
        assertEquals(2, nodes_G[0].getLevel());
        assertEquals(2, nodes_G[1].getLevel());
        assertEquals(1, nodes_G[2].getLevel());
        assertEquals(1, nodes_G[3].getLevel());
        assertEquals(0, nodes_G[4].getLevel());
        //levelDistanceFromParent
        assertEquals(1, nodes_G[0].getLevelDistanceFromParent());
        assertEquals(1, nodes_G[1].getLevelDistanceFromParent());
        assertEquals(1, nodes_G[2].getLevelDistanceFromParent());
        assertEquals(1, nodes_G[3].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[4].getLevelDistanceFromParent());
    }

    @Test
    void variables_Test3(){
        System.out.println("---Variables - Species tree 2 - Gene tree 1---");
        RootedIntervalTree G = getGtree2_1();
        G.computeLevel(G.getRoot());
        //nodes_S - A, B, R
        //nodes_G - a1, a2, b1, u, q
        //speciesNodeBelow
        assertEquals(nodes_S[0].getName(), nodes_G[0].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[0].getName(), nodes_G[1].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[1].getName(), nodes_G[2].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[2].getName(), nodes_G[3].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[2].getName(), nodes_G[4].getSpeciesNodeBelow().getName());
        //levelS
        assertEquals(1, nodes_G[0].getLevel());
        assertEquals(1, nodes_G[1].getLevel());
        assertEquals(1, nodes_G[2].getLevel());
        assertEquals(0, nodes_G[3].getLevel());
        assertEquals(0, nodes_G[4].getLevel());
        //levelDistanceFromParent
        assertEquals(1, nodes_G[0].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[1].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[2].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[3].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[4].getLevelDistanceFromParent());
    }

    @Test
    void variables_Test4(){
        System.out.println("---Variables - Species tree 2 - Gene tree 2---");
        RootedIntervalTree G = getGtree2_2();
        G.computeLevel(G.getRoot());
        //nodes_S - A, B, R
        //nodes_G - a1, a2, b1, u, q
        //speciesNodeBelow
        assertEquals(nodes_S[0].getName(), nodes_G[0].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[0].getName(), nodes_G[1].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[1].getName(), nodes_G[2].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[2].getName(), nodes_G[3].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[2].getName(), nodes_G[4].getSpeciesNodeBelow().getName());
        //levelS
        assertEquals(1, nodes_G[0].getLevel());
        assertEquals(1, nodes_G[1].getLevel());
        assertEquals(1, nodes_G[2].getLevel());
        assertEquals(0, nodes_G[3].getLevel());
        assertEquals(0, nodes_G[4].getLevel());
        //levelDistanceFromParent
        assertEquals(1, nodes_G[0].getLevelDistanceFromParent());
        assertEquals(1, nodes_G[1].getLevelDistanceFromParent());
        assertEquals(1, nodes_G[2].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[3].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[4].getLevelDistanceFromParent());
    }

    @Test
    void variables_Test5(){
        System.out.println("---Variables - Species tree 3 - Gene tree 1---");
        RootedIntervalTree G = getGtree3_1();
        G.computeLevel(G.getRoot());
        //nodes_S - A, B, C, D, X, Y, Z
        //nodes_G - a, b, c, d, u, v, r
        //speciesNodeBelow
        assertEquals(nodes_S[0].getName(), nodes_G[0].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[1].getName(), nodes_G[1].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[2].getName(), nodes_G[2].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[3].getName(), nodes_G[3].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[4].getName(), nodes_G[4].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[5].getName(), nodes_G[5].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[6].getName(), nodes_G[6].getSpeciesNodeBelow().getName());
        //levelS
        assertEquals(2, nodes_G[0].getLevel());
        assertEquals(2, nodes_G[1].getLevel());
        assertEquals(2, nodes_G[2].getLevel());
        assertEquals(2, nodes_G[3].getLevel());
        assertEquals(1, nodes_G[4].getLevel());
        assertEquals(1, nodes_G[5].getLevel());
        assertEquals(0, nodes_G[6].getLevel());
        //levelDistanceFromParent
        assertEquals(1, nodes_G[0].getLevelDistanceFromParent());
        assertEquals(1, nodes_G[1].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[2].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[3].getLevelDistanceFromParent());
        assertEquals(1, nodes_G[4].getLevelDistanceFromParent());
        assertEquals(1, nodes_G[5].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[6].getLevelDistanceFromParent());
    }

    @Test
    void variables_Test6(){
        System.out.println("---Variables - Species tree 3 - Gene tree 2---");
        RootedIntervalTree G = getGtree3_2();
        G.computeLevel(G.getRoot());
        //nodes_S - A, B, C, D, X, Y, Z
        //nodes_G - a, b, c, d, u, v, r
        //speciesNodeBelow
        assertEquals(nodes_S[0].getName(), nodes_G[0].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[1].getName(), nodes_G[1].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[2].getName(), nodes_G[2].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[3].getName(), nodes_G[3].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[4].getName(), nodes_G[4].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[5].getName(), nodes_G[5].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[6].getName(), nodes_G[6].getSpeciesNodeBelow().getName());
        //levelS
        assertEquals(2, nodes_G[0].getLevel());
        assertEquals(2, nodes_G[1].getLevel());
        assertEquals(2, nodes_G[2].getLevel());
        assertEquals(2, nodes_G[3].getLevel());
        assertEquals(1, nodes_G[4].getLevel());
        assertEquals(1, nodes_G[5].getLevel());
        assertEquals(0, nodes_G[6].getLevel());
        //levelDistanceFromParent
        assertEquals(1, nodes_G[0].getLevelDistanceFromParent());
        assertEquals(1, nodes_G[1].getLevelDistanceFromParent());
        assertEquals(1, nodes_G[2].getLevelDistanceFromParent());
        assertEquals(1, nodes_G[3].getLevelDistanceFromParent());
        assertEquals(1, nodes_G[4].getLevelDistanceFromParent());
        assertEquals(1, nodes_G[5].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[6].getLevelDistanceFromParent());
    }

    @Test
    void variables_Test7(){
        System.out.println("---Variables - Species tree 3 - Gene tree 3---");
        RootedIntervalTree G = getGtree3_3();
        G.computeLevel(G.getRoot());
        //nodes_S - A, B, C, D, X, Y, Z
        //nodes_G - a, b, c, d, u, v, r
        //speciesNodeBelow
        assertEquals(nodes_S[0].getName(), nodes_G[0].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[1].getName(), nodes_G[1].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[2].getName(), nodes_G[2].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[3].getName(), nodes_G[3].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[4].getName(), nodes_G[4].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[5].getName(), nodes_G[5].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[6].getName(), nodes_G[6].getSpeciesNodeBelow().getName());
        //levelS
        assertEquals(2, nodes_G[0].getLevel());
        assertEquals(2, nodes_G[1].getLevel());
        assertEquals(2, nodes_G[2].getLevel());
        assertEquals(2, nodes_G[3].getLevel());
        assertEquals(1, nodes_G[4].getLevel());
        assertEquals(1, nodes_G[5].getLevel());
        assertEquals(0, nodes_G[6].getLevel());
        //levelDistanceFromParent
        assertEquals(1, nodes_G[0].getLevelDistanceFromParent());
        assertEquals(1, nodes_G[1].getLevelDistanceFromParent());
        assertEquals(1, nodes_G[2].getLevelDistanceFromParent());
        assertEquals(1, nodes_G[3].getLevelDistanceFromParent());
        assertEquals(1, nodes_G[4].getLevelDistanceFromParent());
        assertEquals(1, nodes_G[5].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[6].getLevelDistanceFromParent());
    }

    @Test
    void variables_Test8(){
        System.out.println("---Variables - Species tree 3 - Gene tree 4---");
        RootedIntervalTree G = getGtree3_4();
        G.computeLevel(G.getRoot());
        //nodes_S - A, B, C, D, X, Y, Z
        //nodes_G - a, b, c, d, u, v, r
        //speciesNodeBelow
        assertEquals(nodes_S[0].getName(), nodes_G[0].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[1].getName(), nodes_G[1].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[2].getName(), nodes_G[2].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[3].getName(), nodes_G[3].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[4].getName(), nodes_G[4].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[6].getName(), nodes_G[5].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[6].getName(), nodes_G[6].getSpeciesNodeBelow().getName());
        //levelS
        assertEquals(2, nodes_G[0].getLevel());
        assertEquals(2, nodes_G[1].getLevel());
        assertEquals(2, nodes_G[2].getLevel());
        assertEquals(2, nodes_G[3].getLevel());
        assertEquals(1, nodes_G[4].getLevel());
        assertEquals(0, nodes_G[5].getLevel());
        assertEquals(0, nodes_G[6].getLevel());
        //levelDistanceFromParent
        assertEquals(1, nodes_G[0].getLevelDistanceFromParent());
        assertEquals(1, nodes_G[1].getLevelDistanceFromParent());
        assertEquals(2, nodes_G[2].getLevelDistanceFromParent());
        assertEquals(2, nodes_G[3].getLevelDistanceFromParent());
        assertEquals(1, nodes_G[4].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[5].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[6].getLevelDistanceFromParent());
    }

    @Test
    void variables_Test9(){
        System.out.println("---Variables - Species tree 4 - Gene tree 1---");
        RootedIntervalTree G = getGtree4_1();
        G.computeLevel(G.getRoot());
        //nodes_S - A, B, C, D, E, X, Y, Z, R
        //nodes_G - a, b, c, d, e, u, v, w, r
        //speciesNodeBelow
        assertEquals(nodes_S[0].getName(), nodes_G[0].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[1].getName(), nodes_G[1].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[2].getName(), nodes_G[2].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[3].getName(), nodes_G[3].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[4].getName(), nodes_G[4].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[8].getName(), nodes_G[5].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[8].getName(), nodes_G[6].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[7].getName(), nodes_G[7].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[8].getName(), nodes_G[8].getSpeciesNodeBelow().getName());
        //levelS
        assertEquals(3, nodes_G[0].getLevel());
        assertEquals(3, nodes_G[1].getLevel());
        assertEquals(3, nodes_G[2].getLevel());
        assertEquals(3, nodes_G[3].getLevel());
        assertEquals(1, nodes_G[4].getLevel());
        assertEquals(0, nodes_G[5].getLevel());
        assertEquals(0, nodes_G[6].getLevel());
        assertEquals(2, nodes_G[7].getLevel());
        assertEquals(0, nodes_G[8].getLevel());
        //levelDistanceFromParent
        assertEquals(3, nodes_G[0].getLevelDistanceFromParent());
        assertEquals(3, nodes_G[1].getLevelDistanceFromParent());
        assertEquals(1, nodes_G[2].getLevelDistanceFromParent());
        assertEquals(1, nodes_G[3].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[4].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[5].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[6].getLevelDistanceFromParent());
        assertEquals(1, nodes_G[7].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[8].getLevelDistanceFromParent());
    }

    @Test
    void variables_Test10(){
        System.out.println("---Variables - Species tree 4 - Gene tree 2---");
        RootedIntervalTree G = getGtree4_2();
        G.computeLevel(G.getRoot());
        //nodes_S - A, B, C, D, E, X, Y, Z, R
        //nodes_G - a, b, c, d, e, u, v, w, r
        //speciesNodeBelow
        assertEquals(nodes_S[0].getName(), nodes_G[0].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[1].getName(), nodes_G[1].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[2].getName(), nodes_G[2].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[3].getName(), nodes_G[3].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[4].getName(), nodes_G[4].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[6].getName(), nodes_G[5].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[8].getName(), nodes_G[6].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[7].getName(), nodes_G[7].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[8].getName(), nodes_G[8].getSpeciesNodeBelow().getName());
        //levelS
        assertEquals(3, nodes_G[0].getLevel());
        assertEquals(3, nodes_G[1].getLevel());
        assertEquals(3, nodes_G[2].getLevel());
        assertEquals(3, nodes_G[3].getLevel());
        assertEquals(1, nodes_G[4].getLevel());
        assertEquals(1, nodes_G[5].getLevel());
        assertEquals(0, nodes_G[6].getLevel());
        assertEquals(2, nodes_G[7].getLevel());
        assertEquals(0, nodes_G[8].getLevel());
        //levelDistanceFromParent
        assertEquals(2, nodes_G[0].getLevelDistanceFromParent());
        assertEquals(2, nodes_G[1].getLevelDistanceFromParent());
        assertEquals(1, nodes_G[2].getLevelDistanceFromParent());
        assertEquals(1, nodes_G[3].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[4].getLevelDistanceFromParent());
        assertEquals(1, nodes_G[5].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[6].getLevelDistanceFromParent());
        assertEquals(1, nodes_G[7].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[8].getLevelDistanceFromParent());
    }

    @Test
    void variables_Test11(){
        System.out.println("---Variables - Species tree 4 - Gene tree 3---");
        RootedIntervalTree G = getGtree4_3();
        G.computeLevel(G.getRoot());
        //nodes_S - A, B, C, D, E, X, Y, Z, R
        //nodes_G - a, b, c, d, e, u, v, w, r
        //speciesNodeBelow
        assertEquals(nodes_S[0].getName(), nodes_G[0].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[1].getName(), nodes_G[1].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[2].getName(), nodes_G[2].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[3].getName(), nodes_G[3].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[4].getName(), nodes_G[4].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[5].getName(), nodes_G[5].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[6].getName(), nodes_G[6].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[7].getName(), nodes_G[7].getSpeciesNodeBelow().getName());
        assertEquals(nodes_S[8].getName(), nodes_G[8].getSpeciesNodeBelow().getName());
        //levelS
        assertEquals(3, nodes_G[0].getLevel());
        assertEquals(3, nodes_G[1].getLevel());
        assertEquals(3, nodes_G[2].getLevel());
        assertEquals(3, nodes_G[3].getLevel());
        assertEquals(1, nodes_G[4].getLevel());
        assertEquals(2, nodes_G[5].getLevel());
        assertEquals(1, nodes_G[6].getLevel());
        assertEquals(2, nodes_G[7].getLevel());
        assertEquals(0, nodes_G[8].getLevel());
        //levelDistanceFromParent
        assertEquals(0, nodes_G[0].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[1].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[2].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[3].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[4].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[5].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[6].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[7].getLevelDistanceFromParent());
        assertEquals(0, nodes_G[8].getLevelDistanceFromParent());
    }

    void getS1(){
        RootedExactNode A = new RootedExactNode("A", 3.0,2);
        RootedExactNode B = new RootedExactNode("B", 3.0,2);
        RootedExactNode C = new RootedExactNode("C", 3.0,1);
        RootedExactNode X = new RootedExactNode("X", 2.0,1);
        RootedExactNode Y = new RootedExactNode("Y", 0.0,0);
        A.setParent(X);
        B.setParent(X);
        C.setParent(Y);
        X.setParent(Y);
        X.setLeft(A);
        X.setRight(B);
        Y.setLeft(X);
        Y.setRight(C);
        nodes_S = new RootedExactNode[]{A, B, C, X, Y};
    }

    void getS2(){
        RootedExactNode A = new RootedExactNode("A", 1.0,1);
        RootedExactNode B = new RootedExactNode("B", 2.0,1);
        RootedExactNode R = new RootedExactNode("R", 0.0,0);
        A.setParent(R);
        B.setParent(R);
        R.setLeft(A);
        R.setRight(B);
        nodes_S = new RootedExactNode[]{A, B, R};
    }

    void getS3() {
        RootedExactNode A = new RootedExactNode("A", 4.0, 2);
        RootedExactNode B = new RootedExactNode("B", 4.0, 2);
        RootedExactNode C = new RootedExactNode("C", 4.0, 2);
        RootedExactNode D = new RootedExactNode("D", 4.0, 2);
        RootedExactNode X = new RootedExactNode("X", 2.0, 1);
        RootedExactNode Y = new RootedExactNode("Y", 2.0, 1);
        RootedExactNode Z = new RootedExactNode("Z", 0.0, 0);
        A.setParent(X);
        B.setParent(X);
        C.setParent(Y);
        D.setParent(Y);
        X.setParent(Z);
        Y.setParent(Z);
        X.setLeft(A);
        X.setRight(B);
        Y.setLeft(C);
        Y.setRight(D);
        Z.setLeft(X);
        Z.setRight(Y);
        nodes_S = new RootedExactNode[]{A, B, C, D, X, Y, Z};
    }

    void getS4() {
        RootedExactNode A = new RootedExactNode("A", 6.0, 3);
        RootedExactNode B = new RootedExactNode("B", 6.0, 3);
        RootedExactNode C = new RootedExactNode("C", 6.0, 3);
        RootedExactNode D = new RootedExactNode("D", 6.0, 3);
        RootedExactNode E = new RootedExactNode("E", 6.0, 1);
        RootedExactNode X = new RootedExactNode("X", 4.0, 2);
        RootedExactNode Y = new RootedExactNode("Y", 2.0, 1);
        RootedExactNode Z = new RootedExactNode("Z", 4.0, 2);
        RootedExactNode R = new RootedExactNode("R", 0.0, 0);
        A.setParent(X);
        B.setParent(X);
        C.setParent(Z);
        D.setParent(Z);
        E.setParent(R);
        X.setParent(Y);
        Z.setParent(Y);
        Y.setParent(R);
        X.setLeft(A);
        X.setRight(B);
        Y.setLeft(X);
        Y.setRight(Z);
        Z.setLeft(C);
        Z.setRight(D);
        R.setLeft(Y);
        R.setRight(E);
        nodes_S = new RootedExactNode[]{A, B, C, D, E, X, Y, Z, R};
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
        nodes_G = new RootedIntervalNode[]{a, b, c, u, r};
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
        nodes_G = new RootedIntervalNode[]{a1, a2, b1, u, q};
        return q;
    }

    RootedIntervalNode getG3(RootedExactNode[] nodesS, double[] depths){
        RootedIntervalNode a = new RootedIntervalNode("a");
        a.setMaxD(depths[0]);
        a.setLcaS(nodesS[0]);
        RootedIntervalNode b = new RootedIntervalNode("b");
        b.setMaxD(depths[1]);
        b.setLcaS(nodesS[1]);
        RootedIntervalNode c = new RootedIntervalNode("c");
        c.setMaxD(depths[2]);
        c.setLcaS(nodesS[2]);
        RootedIntervalNode d = new RootedIntervalNode("d");
        d.setMaxD(depths[3]);
        d.setLcaS(nodesS[3]);
        RootedIntervalNode u = new RootedIntervalNode("u");
        u.setMaxD(depths[4]);
        u.setLcaS(nodesS[4]);
        RootedIntervalNode v = new RootedIntervalNode("v");
        v.setMaxD(depths[5]);
        v.setLcaS(nodesS[5]);
        RootedIntervalNode r = new RootedIntervalNode("r");
        r.setMaxD(depths[6]);
        r.setLcaS(nodesS[6]);
        a.setParent(u);
        b.setParent(u);
        c.setParent(v);
        d.setParent(v);
        u.setParent(r);
        v.setParent(r);
        u.setLeft(a);
        u.setRight(b);
        v.setLeft(c);
        v.setRight(d);
        r.setLeft(u);
        r.setRight(v);
        nodes_G = new RootedIntervalNode[]{a, b, c, d, u, v, r};
        return r;
    }

    RootedIntervalNode getG4_1(RootedExactNode[] nodesS, double[] depths, int[] lca){
        RootedIntervalNode a = new RootedIntervalNode("a");
        a.setMaxD(depths[0]);
        a.setLcaS(nodesS[lca[0]]);
        RootedIntervalNode b = new RootedIntervalNode("b");
        b.setMaxD(depths[1]);
        b.setLcaS(nodesS[lca[1]]);
        RootedIntervalNode c = new RootedIntervalNode("c");
        c.setMaxD(depths[2]);
        c.setLcaS(nodesS[lca[2]]);
        RootedIntervalNode d = new RootedIntervalNode("d");
        d.setMaxD(depths[3]);
        d.setLcaS(nodesS[lca[3]]);
        RootedIntervalNode e = new RootedIntervalNode("e");
        e.setMaxD(depths[4]);
        e.setLcaS(nodesS[lca[4]]);
        RootedIntervalNode u = new RootedIntervalNode("u");
        u.setMaxD(depths[5]);
        u.setLcaS(nodesS[lca[5]]);
        RootedIntervalNode v = new RootedIntervalNode("v");
        v.setMaxD(depths[6]);
        v.setLcaS(nodesS[lca[6]]);
        RootedIntervalNode w = new RootedIntervalNode("w");
        w.setMaxD(depths[7]);
        w.setLcaS(nodesS[lca[7]]);
        RootedIntervalNode r = new RootedIntervalNode("r");
        r.setMaxD(depths[8]);
        r.setLcaS(nodesS[lca[8]]);
        a.setParent(r);
        b.setParent(u);
        c.setParent(w);
        d.setParent(w);
        e.setParent(v);
        u.setParent(r);
        v.setParent(u);
        w.setParent(v);
        u.setLeft(b);
        u.setRight(v);
        v.setLeft(w);
        v.setRight(e);
        w.setLeft(c);
        w.setRight(d);
        r.setLeft(a);
        r.setRight(u);
        nodes_G = new RootedIntervalNode[]{a, b, c, d, e, u, v, w, r};
        return r;
    }

    RootedIntervalNode getG4_2(RootedExactNode[] nodesS, double[] depths, int[] lca){
        RootedIntervalNode a = new RootedIntervalNode("a");
        a.setMaxD(depths[0]);
        a.setLcaS(nodesS[lca[0]]);
        RootedIntervalNode b = new RootedIntervalNode("b");
        b.setMaxD(depths[1]);
        b.setLcaS(nodesS[lca[1]]);
        RootedIntervalNode c = new RootedIntervalNode("c");
        c.setMaxD(depths[2]);
        c.setLcaS(nodesS[lca[2]]);
        RootedIntervalNode d = new RootedIntervalNode("d");
        d.setMaxD(depths[3]);
        d.setLcaS(nodesS[lca[3]]);
        RootedIntervalNode e = new RootedIntervalNode("e");
        e.setMaxD(depths[4]);
        e.setLcaS(nodesS[lca[4]]);
        RootedIntervalNode u = new RootedIntervalNode("u");
        u.setMaxD(depths[5]);
        u.setLcaS(nodesS[lca[5]]);
        RootedIntervalNode v = new RootedIntervalNode("v");
        v.setMaxD(depths[6]);
        v.setLcaS(nodesS[lca[6]]);
        RootedIntervalNode w = new RootedIntervalNode("w");
        w.setMaxD(depths[7]);
        w.setLcaS(nodesS[lca[7]]);
        RootedIntervalNode r = new RootedIntervalNode("r");
        r.setMaxD(depths[8]);
        r.setLcaS(nodesS[lca[8]]);
        a.setParent(u);
        b.setParent(u);
        c.setParent(w);
        d.setParent(w);
        e.setParent(v);
        u.setParent(r);
        v.setParent(r);
        w.setParent(v);
        u.setLeft(a);
        u.setRight(b);
        v.setLeft(w);
        v.setRight(e);
        w.setLeft(c);
        w.setRight(d);
        r.setLeft(u);
        r.setRight(v);
        nodes_G = new RootedIntervalNode[]{a, b, c, d, e, u, v, w, r};
        return r;
    }

    RootedIntervalNode getG4_3(RootedExactNode[] nodesS, double[] depths, int[] lca){
        RootedIntervalNode a = new RootedIntervalNode("a");
        a.setMaxD(depths[0]);
        a.setLcaS(nodesS[lca[0]]);
        RootedIntervalNode b = new RootedIntervalNode("b");
        b.setMaxD(depths[1]);
        b.setLcaS(nodesS[lca[1]]);
        RootedIntervalNode c = new RootedIntervalNode("c");
        c.setMaxD(depths[2]);
        c.setLcaS(nodesS[lca[2]]);
        RootedIntervalNode d = new RootedIntervalNode("d");
        d.setMaxD(depths[3]);
        d.setLcaS(nodesS[lca[3]]);
        RootedIntervalNode e = new RootedIntervalNode("e");
        e.setMaxD(depths[4]);
        e.setLcaS(nodesS[lca[4]]);
        RootedIntervalNode u = new RootedIntervalNode("u");
        u.setMaxD(depths[5]);
        u.setLcaS(nodesS[lca[5]]);
        RootedIntervalNode v = new RootedIntervalNode("v");
        v.setMaxD(depths[6]);
        v.setLcaS(nodesS[lca[6]]);
        RootedIntervalNode w = new RootedIntervalNode("w");
        w.setMaxD(depths[7]);
        w.setLcaS(nodesS[lca[7]]);
        RootedIntervalNode r = new RootedIntervalNode("r");
        r.setMaxD(depths[8]);
        r.setLcaS(nodesS[lca[8]]);
        a.setParent(u);
        b.setParent(u);
        c.setParent(w);
        d.setParent(w);
        e.setParent(r);
        u.setParent(v);
        v.setParent(r);
        w.setParent(v);
        u.setLeft(a);
        u.setRight(b);
        v.setLeft(u);
        v.setRight(w);
        w.setLeft(c);
        w.setRight(d);
        r.setLeft(v);
        r.setRight(e);
        nodes_G = new RootedIntervalNode[]{a, b, c, d, e, u, v, w, r};
        return r;
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

    TreeMap<String, String> getLeafMap3(){
        //leaf mapping
        TreeMap<String, String> leafMap = new TreeMap<>();
        leafMap.put("a", "A");
        leafMap.put("b", "B");
        leafMap.put("c", "C");
        leafMap.put("d", "D");
        return leafMap;
    }

    TreeMap<String, String> getLeafMap4(){
        //leaf mapping
        TreeMap<String, String> leafMap = new TreeMap<>();
        leafMap.put("a", "A");
        leafMap.put("b", "B");
        leafMap.put("c", "C");
        leafMap.put("d", "D");
        leafMap.put("e", "E");
        return leafMap;
    }

    RootedIntervalTree getGtree1_1(){
        //species tree
        getS1();
        RootedExactTree S = new RootedExactTree();
        S.setRoot(nodes_S[4]);
        //gene tree
        System.out.println("Gene tree 1");
        double[] depths = {3.0, 3.0, 3.0, 2.0, 0.0};
        RootedIntervalNode g = getG1(nodes_S, depths);
        RootedIntervalTree G = new RootedIntervalTree(g, S, getLeafMap1());
        return G;
    }

    RootedIntervalTree getGtree1_2(){
        //species tree
        getS1();
        RootedExactTree S = new RootedExactTree();
        S.setRoot(nodes_S[4]);
        //gene tree
        System.out.println("Gene tree 2");
        double[] depths = {3.0, 3.0, 3.0, 1.0, -1.0};
        RootedIntervalNode g = getG1(nodes_S, depths);
        RootedIntervalTree G = new RootedIntervalTree(g, S, getLeafMap1());
        return G;
    }

    RootedIntervalTree getGtree2_1(){
        //species tree
        getS2();
        RootedExactTree S = new RootedExactTree();
        S.setRoot(nodes_S[nodes_S.length-1]);
        //gene tree
        System.out.println("Gene tree 1");
        double[] depths = {1.0, 1.0, 2.0, 0.0, -1.0};
        RootedIntervalNode g = getG2(nodes_S, depths);
        RootedIntervalTree G = new RootedIntervalTree(g, S, getLeafMap2());
        return G;
    }

    RootedIntervalTree getGtree2_2(){
        //species tree
        getS2();
        RootedExactTree S = new RootedExactTree();
        S.setRoot(nodes_S[nodes_S.length-1]);
        //gene tree
        System.out.println("Gene tree 2");
        double[] depths = {1.0, 1.0, 2.0, -1.0, -2.0};
        RootedIntervalNode g = getG2(nodes_S, depths);
        RootedIntervalTree G = new RootedIntervalTree(g, S, getLeafMap2());
        return G;
    }

    RootedIntervalTree getGtree3_1(){
        //species tree
        getS3();
        RootedExactTree S = new RootedExactTree();
        S.setRoot(nodes_S[nodes_S.length-1]);
        //gene tree
        System.out.println("Gene tree 1");
        double[] depths = {4.0, 4.0, 4.0, 4.0, 0.0, 2.0, -1.0};
        RootedIntervalNode g = getG3(nodes_S, depths);
        RootedIntervalTree G = new RootedIntervalTree(g, S, getLeafMap3());
        return G;
    }

    RootedIntervalTree getGtree3_2(){
        //species tree
        getS3();
        RootedExactTree S = new RootedExactTree();
        S.setRoot(nodes_S[nodes_S.length-1]);
        //gene tree
        System.out.println("Gene tree 2");
        double[] depths = {4.0, 4.0, 4.0, 4.0, 0.0, 1.0, -1.0};
        RootedIntervalNode g = getG3(nodes_S, depths);
        RootedIntervalTree G = new RootedIntervalTree(g, S, getLeafMap3());
        return G;
    }

    RootedIntervalTree getGtree3_3(){
        //species tree
        getS3();
        RootedExactTree S = new RootedExactTree();
        S.setRoot(nodes_S[nodes_S.length-1]);
        //gene tree
        System.out.println("Gene tree 3");
        double[] depths = {4.0, 4.0, 4.0, 4.0, 0.0, 0.0, -1.0};
        RootedIntervalNode g = getG3(nodes_S, depths);
        RootedIntervalTree G = new RootedIntervalTree(g, S, getLeafMap3());
        return G;
    }

    RootedIntervalTree getGtree3_4(){
        //species tree
        getS3();
        RootedExactTree S = new RootedExactTree();
        S.setRoot(nodes_S[nodes_S.length-1]);
        //gene tree
        System.out.println("Gene tree 4");
        double[] depths = {4.0, 4.0, 4.0, 4.0, 0.0, -1.0, -2.0};
        RootedIntervalNode g = getG3(nodes_S, depths);
        RootedIntervalTree G = new RootedIntervalTree(g, S, getLeafMap3());
        return G;
    }

    RootedIntervalTree getGtree4_1(){
        //species tree
        getS4();
        RootedExactTree S = new RootedExactTree();
        S.setRoot(nodes_S[nodes_S.length-1]);
        //gene tree
        System.out.println("Gene tree 1");
        //nodes_G - a, b, c, d, e, u, v, w, r
        //nodes_S - A, B, C, D, E, X, Y, Z, R
        double[] depths = {6.0, 6.0, 6.0, 6.0, 6.0, -1.0, 0.0, 2.0, -2.0};
        int[] lca = {0, 1, 2, 3, 4, 8, 8, 7, 8};
        RootedIntervalNode g = getG4_1(nodes_S, depths, lca);
        RootedIntervalTree G = new RootedIntervalTree(g, S, getLeafMap4());
        return G;
    }

    RootedIntervalTree getGtree4_2(){
        //species tree
        getS4();
        RootedExactTree S = new RootedExactTree();
        S.setRoot(nodes_S[nodes_S.length-1]);
        //gene tree
        System.out.println("Gene tree 2");
        //nodes_G - a, b, c, d, e, u, v, w, r
        //nodes_S - A, B, C, D, E, X, Y, Z, R
        double[] depths = {6.0, 6.0, 6.0, 6.0, 6.0, 0.0, 0.0, 2.0, -1.0};
        int[] lca = {0, 1, 2, 3, 4, 5, 8, 7, 8};
        RootedIntervalNode g = getG4_2(nodes_S, depths, lca);
        RootedIntervalTree G = new RootedIntervalTree(g, S, getLeafMap4());
        return G;
    }

    RootedIntervalTree getGtree4_3(){
        //species tree
        getS4();
        RootedExactTree S = new RootedExactTree();
        S.setRoot(nodes_S[nodes_S.length-1]);
        //gene tree
        System.out.println("Gene tree 3");
        //nodes_G - a, b, c, d, e, u, v, w, r
        //nodes_S - A, B, C, D, E, X, Y, Z, R
        double[] depths = {6.0, 6.0, 6.0, 6.0, 6.0, 4.0, 2.0, 4.0, 0.0};
        int[] lca = {0, 1, 2, 3, 4, 5, 6, 7, 8};
        RootedIntervalNode g = getG4_3(nodes_S, depths, lca);
        RootedIntervalTree G = new RootedIntervalTree(g, S, getLeafMap4());
        return G;
    }
}