package Test;

import Software.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @Test
    public void parseUnrooted_3Nodes(){
        Parser p = new Parser();
        List<Pair<String, String>> mapping = p.parseSpeciesMapping(new File("data_test/S_mapping/S_map.smap"));
        p.parseUnrootedTree("data_test/Parser/geneUnrooted_3.tree", 0.0, mapping);
    }

    @Test
    public void parseUnrooted_2Nodes(){
        Parser p = new Parser();
        List<Pair<String, String>> mapping = p.parseSpeciesMapping(new File("data_test/S_mapping/S_map.smap"));
        p.parseUnrootedTree("data_test/Parser/geneUnrooted_2.tree", 0.0, mapping);
    }

    @Test
    public void parseUnrooted_1Nodes(){
        Parser p = new Parser();
        List<Pair<String, String>> mapping = p.parseSpeciesMapping(new File("data_test/S_mapping/S_map.smap"));
        p.parseUnrootedTree("data_test/Parser/geneUnrooted_1.tree", 0.0, mapping);
    }

    @Test
    public void parseRooted(){
        Parser p = new Parser();
        p.parseRootedTree("data_test/Parser/speciesRooted.tree");
    }

    @Test
    public void changeRootedToUnrooted_1(){
        Parser p = new Parser();
        RootedExactTree species = p.parseRootedTree("data_test/Parser/speciesRooted.tree");
        List<Pair<String, String>> mapping = p.parseSpeciesMapping(new File("data_test/S_mapping/S_map.smap"));
        RootedIntervalTree t = p.parseRootedIntervalTree("data_test/Parser/geneRooted_1.tree", 0.0, mapping, species);
        UnrootedTree unroot = p.rootedToUnrootedTree(t);
        assertEquals(2, unroot.getEdges().size());
        assertEquals(3, unroot.getNodes().size());
        for(Edge e : unroot.getEdges()) {
            switch (e.getU().getName()) {
                case "a":
                    assertEquals("a|b", e.getV().getName());
                    assertEquals(1, e.getMaxLength());
                    break;
                case "b":
                    assertEquals("a|b", e.getV().getName());
                    assertEquals(1, e.getMaxLength());
                    break;
            }
        }
    }

    @Test
    public void changeRootedToUnrooted_2(){
        Parser p = new Parser();
        RootedExactTree species = p.parseRootedTree("data_test/Parser/speciesRooted.tree");
        List<Pair<String, String>> mapping = p.parseSpeciesMapping(new File("data_test/S_mapping/S_map.smap"));
        RootedIntervalTree t = p.parseRootedIntervalTree("data_test/Parser/geneRooted_2.tree", 0.0, mapping, species);
        UnrootedTree unroot = p.rootedToUnrootedTree(t);
        assertEquals(3, unroot.getEdges().size());
        assertEquals(4, unroot.getNodes().size());
        for(Edge e : unroot.getEdges()){
            switch (e.getU().getName()) {
                case "a":
                    assertEquals("a|c|d", e.getV().getName());
                    assertEquals(5, e.getMaxLength());
                    break;
                case "c":
                    assertEquals("a|c|d", e.getV().getName());
                    assertEquals(1, e.getMaxLength());
                    break;
                case "d":
                    assertEquals("a|c|d", e.getV().getName());
                    assertEquals(1, e.getMaxLength());
                    break;
            }
        }
    }

    @Test
    public void changeRootedToUnrooted_3(){
        Parser p = new Parser();
        RootedExactTree species = p.parseRootedTree("data_test/Parser/speciesRooted.tree");
        List<Pair<String, String>> mapping = p.parseSpeciesMapping(new File("data_test/S_mapping/S_map.smap"));
        RootedIntervalTree t = p.parseRootedIntervalTree("data_test/Parser/geneRooted_3.tree", 0.0, mapping, species);
        UnrootedTree unroot = p.rootedToUnrootedTree(t);
        assertEquals(5, unroot.getEdges().size());
        assertEquals(6, unroot.getNodes().size());
        for(Edge e : unroot.getEdges()){
            switch (e.getU().getName()) {
                case "a":
                    assertEquals("a|b|cd", e.getV().getName());
                    assertEquals(1, e.getMaxLength());
                    break;
                case "b":
                    assertEquals("a|b|cd", e.getV().getName());
                    assertEquals(1, e.getMaxLength());
                    break;
                case "c":
                    assertEquals("ab|c|d", e.getV().getName());
                    assertEquals(1, e.getMaxLength());
                    break;
                case "d":
                    assertEquals("ab|c|d", e.getV().getName());
                    assertEquals(1, e.getMaxLength());
                    break;
                default:
                    assertEquals(4, e.getMaxLength());
                    break;
            }
        }
    }

    @Test
    public void changeRootedToUnrooted_4(){
        Parser p = new Parser();
        RootedExactTree species = p.parseRootedTree("data_test/Parser/speciesRooted.tree");
        List<Pair<String, String>> mapping = p.parseSpeciesMapping(new File("data_test/S_mapping/S_map.smap"));
        RootedIntervalTree t = p.parseRootedIntervalTree("data_test/Parser/geneRooted_4.tree", 0.0, mapping, species);
        UnrootedTree unroot = p.rootedToUnrootedTree(t);
        assertEquals(7, unroot.getEdges().size());
        assertEquals(8, unroot.getNodes().size());
        for(Edge e : unroot.getEdges()){
            switch (e.getU().getName()) {
                case "a":
                    assertEquals("a|b1b2|cd", e.getV().getName());
                    assertEquals(1, e.getMaxLength());
                    break;
                case "b1":
                    assertEquals("acd|b1|b2", e.getV().getName());
                    assertEquals(0.5, e.getMaxLength());
                    break;
                case "b2":
                    assertEquals("acd|b1|b2", e.getV().getName());
                    assertEquals(0.5, e.getMaxLength());
                    break;
                case "c":
                    assertEquals("ab1b2|c|d", e.getV().getName());
                    assertEquals(1, e.getMaxLength());
                    break;
                case "d":
                    assertEquals("ab1b2|c|d", e.getV().getName());
                    assertEquals(1, e.getMaxLength());
                    break;
                case "ab1b2|c|d":
                    assertEquals(4, e.getMaxLength());
                    break;
            }
        }
    }

    @Test
    public void getSmapping() {
        Parser p = new Parser();
        List<Pair<String, String>> mapping = p.parseSpeciesMapping(new File("data_test/S_mapping/S_map.smap"));
        assertEquals("a", mapping.get(0).getFirst());
        assertEquals("A", mapping.get(0).getSecond());
        assertEquals("b", mapping.get(1).getFirst());
        assertEquals("B", mapping.get(1).getSecond());
        assertEquals("c", mapping.get(2).getFirst());
        assertEquals("C", mapping.get(2).getSecond());
        assertEquals("d", mapping.get(3).getFirst());
        assertEquals("D", mapping.get(3).getSecond());
        assertEquals("e", mapping.get(4).getFirst());
        assertEquals("E", mapping.get(4).getSecond());
        assertEquals("f", mapping.get(5).getFirst());
        assertEquals("F", mapping.get(5).getSecond());
    }
}