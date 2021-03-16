package Test;

import Software.Pair;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static Software.Parser.parseSpeciesMapping;
import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @Test
    public void getSmapping() {
        List<Pair<String, String>> mapping = parseSpeciesMapping(new File("data_test/S_mapping/S_map.smap"));
        assertEquals("a", mapping.get(0).getFirst());
        assertEquals("A", mapping.get(0).getSecond());
        assertEquals("b", mapping.get(1).getFirst());
        assertEquals("B", mapping.get(1).getSecond());
        assertEquals("c", mapping.get(2).getFirst());
        assertEquals("C", mapping.get(2).getSecond());
    }

}