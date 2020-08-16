package ramdan.file.line.token.data;

import lombok.val;
import static org.junit.Assert.*;

import org.junit.Test;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.data.LineTokenAbstract;

public class LineTokenAbstractTest {

    @Test
    public void get() {
        assertEquals("Hi_Agus", LineTokenAbstract.get(LineTokenData.parse("Hi_Agus"), 0));
    }

    @Test
    public void get_nullParameter(){
        LineTokenAbstract.stringNullToEmpty = false;
        assertNull(LineTokenAbstract.get(null,0));

        LineTokenAbstract.stringNullToEmpty = true;
        assertEquals("",LineTokenAbstract.get(null,0));
    }

    @Test
    public void tokenCheck() {
        assertEquals("Hi_Agus", LineTokenAbstract.tokenCheck("Hi_Agus"));
    }

    @Test
    public void tokenCheck_nullParameter(){
        LineTokenAbstract.stringNullToEmpty = false;
        assertNull(LineTokenAbstract.tokenCheck(null));

        LineTokenAbstract.stringNullToEmpty = true;
        assertEquals("",LineTokenAbstract.tokenCheck(null));
    }
    @Test
    public void token() {
        assertEquals("Hi_Agus", LineTokenAbstract.token("Hi_Agus"));
    }
    @Test
    public void token_nullParameter(){
        LineTokenAbstract.stringNullToEmpty = false;
        assertNull(LineTokenAbstract.token(null));

        LineTokenAbstract.stringNullToEmpty = true;
        assertEquals("",LineTokenAbstract.token(null));
    }

    @Test
    public void token_stringTrim(){
        val save = LineTokenAbstract.stringTrim;
        LineTokenAbstract.stringTrim = true;
        try {
            assertEquals("Haloo",LineTokenAbstract.token("Haloo "));
        }finally {
            LineTokenAbstract.stringTrim=save;
        }
    }

    @Test
    public void newEOF(){
        val lientoken = LineTokenAbstract.newEOF(null,null);

        assertNotNull(lientoken);
        assertNull(lientoken.getFileName());
        assertNull(lientoken.getStart());
        assertNull(lientoken.getSource());

        assertTrue(lientoken.isEOF());
        assertTrue(lientoken.isEmpty());
        assertEquals(0,lientoken.length());

        assertEquals(lientoken,lientoken.copyLineToken());
        assertEquals(lientoken,lientoken.replaceToken(0,""));
        assertEquals(lientoken,lientoken.mapping("tagname",1));
        assertEquals(lientoken,lientoken.mapping(1));
        assertEquals(lientoken,lientoken.merge(1,lientoken));

        assertEquals("",lientoken.get(0));
        assertEquals("",lientoken.getValue());
    }

    @Test
    public void newEOF_NullLine(){
        val lientoken = LineTokenAbstract.newEOF(null);

        assertNotNull(lientoken);
        assertNull(lientoken.getFileName());
        assertNull(lientoken.getStart());
    }
}
