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
    public void newEOF(){
        val lientoken = LineTokenAbstract.newEOF(null,null);

        assertNotNull(lientoken);
        assertNull(lientoken.getFileName());
        assertNull(lientoken.getStart());
    }

    @Test
    public void newEOF_NullLine(){
        val lientoken = LineTokenAbstract.newEOF(null);

        assertNotNull(lientoken);
        assertNull(lientoken.getFileName());
        assertNull(lientoken.getStart());
    }
}
