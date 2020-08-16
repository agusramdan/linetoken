package ramdan.file.line.token.filter;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;


public class FilterComplexTest {

    @Test
    public void read_Root() throws IOException {
        FilterComplex filterComplex = FilterComplex.read(new File("src/test/resources/filter-root.cfg"));
        assertFalse(filterComplex.remove);
        assertNotNull(filterComplex.start);
        assertTrue(filterComplex.start.isMatchRule("START"));
        assertNotNull(filterComplex.end);
        assertTrue(filterComplex.end.isMatchRule("END"));
    }

    @Test
    public void read_RootChild() throws IOException {
        FilterComplex filterComplex = FilterComplex.read(new File("src/test/resources/filter-root-childs.cfg"));

        assertFalse(filterComplex.remove);
        assertNotNull(filterComplex.start);
        assertTrue(filterComplex.start.isMatchRule("START"));
        assertNotNull(filterComplex.end);
        assertTrue(filterComplex.end.isMatchRule("END"));

        assertFalse(filterComplex.childs[0].remove);
        assertNotNull(filterComplex.childs[0].start);
        assertTrue(filterComplex.childs[0].start.isMatchRule("C_1_START"));
        assertNotNull(filterComplex.childs[0].end);
        assertTrue(filterComplex.childs[0].end.isMatchRule("C_1_END"));


        assertFalse(filterComplex.childs[0].childs[0].remove);
        assertNotNull(filterComplex.childs[0].childs[0].start);
        assertTrue(filterComplex.childs[0].childs[0].start.isMatchRule("C_1_2_START"));
        assertNotNull(filterComplex.childs[0].childs[0].end);
        assertTrue(filterComplex.childs[0].childs[0].end.isMatchRule("C_1_2_END"));

        assertFalse(filterComplex.childs[1].remove);
        assertNotNull(filterComplex.childs[1].start);
        assertTrue(filterComplex.childs[1].start.isMatchRule("C_2_START"));
        assertNotNull(filterComplex.childs[1].end);
        assertTrue(filterComplex.childs[1].end.isMatchRule("C_2_END"));
    }
}
