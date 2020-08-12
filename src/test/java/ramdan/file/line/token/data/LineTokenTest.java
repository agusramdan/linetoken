package ramdan.file.line.token.data;

import org.junit.Test;
import ramdan.file.line.token.LineToken;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;


public class LineTokenTest {

    @Test
    public void testParse(){
        LineTokenData lineToken = LineTokenData.parse("token-0");

        assertEquals(1,lineToken.length());
    }

    @Test
    public void testParse_Space(){
        LineTokenData lineToken = LineTokenData.parse("TAGNAME token-0");

        assertEquals(2,lineToken.length());
        assertEquals(" ",lineToken.getTagDelimiter());
        assertEquals("|",lineToken.getTokenDelimiter());
    }

    @Test
    public void testParsePipeSpace(){
        LineTokenData lineToken = LineTokenData.parse("TAGNAME| token-0");

        assertEquals(2,lineToken.length());
        assertEquals("| ",lineToken.getTagDelimiter());
        assertEquals("|",lineToken.getTokenDelimiter());
    }

    @Test
    public void testParsePipe(){
        LineTokenData lineToken = LineTokenData.parse("TAGNAME|token-0");

        assertEquals(2,lineToken.length());
        assertEquals("|",lineToken.getTagDelimiter());
        assertEquals("|",lineToken.getTokenDelimiter());
    }
    @Test
    public void testParse_null(){
        LineTokenData lineToken = LineTokenData.parse((String)null);
        assertEquals(0,lineToken.length());
    }

    @Test
    public void testSize(){
        LineTokenData lineToken = LineTokenData.newInstance();
        assertEquals(0,lineToken.length());
    }

    @Test
    public void testEmpty(){
        LineTokenData lineToken = LineTokenData.newInstance();
        assertEquals("",lineToken.get(0));
        assertTrue(lineToken.isEmpty(0));

    }

    @Test
    public void testToken(){
        LineTokenData lineToken = LineTokenData.newInstance("token-0","token-1");
        assertEquals(2,lineToken.length());
        assertEquals("token-0",lineToken.get(0));
        assertEquals("token-1",lineToken.get(1));
    }

    @Test
    public void testArg_NullToEmpty_Empty(){
        LineTokenData.args("-empty");
        assertTrue(LineTokenData.stringNullToEmpty);
    }

    @Test
    public void testArg_NullToEmpty_null(){
        LineTokenData.args("-null");
        assertFalse(LineTokenData.stringNullToEmpty);
    }

    @Test
    public void testEqualIndex_(){
        LineTokenData lineToken =LineTokenData.newInstance("token-0-data");

        assertTrue(lineToken.equal(0,"token-0-data"));
        assertTrue(lineToken.equal(0,"salah","token-0-data"));
    }

    @Test
    public void testEqualIndex_NotEqual(){
        LineTokenData lineToken = LineTokenData.newInstance("token-0-data");

        assertFalse(lineToken.equal(0,"salah"));
    }

    @Test
    public void testEqualIgnoreCaseIndex_(){
        LineTokenData lineToken = LineTokenData.newInstance("token-0-data");

        assertTrue(lineToken.equalIgnoreCase(0,"Token-0-data"));
        assertTrue(lineToken.equalIgnoreCase(0,"salah","Token-0-data"));
    }

    @Test
    public void testEqualIgnoreCaseIndex_NotEqual(){
        LineTokenData lineToken = LineTokenData.newInstance("token-0-data");

        assertFalse(lineToken.equalIgnoreCase(0,"salah"));
    }

    @Test
    public void testStringNullToEmpty_false(){
        LineTokenData.args("-null");
        LineTokenData lineToken = LineTokenData.newInstance();

        assertNull(lineToken.get(0));
        assertTrue(lineToken.isEmpty(0));
    }

    @Test
    public void testStringNullToEmpty_true(){
        LineTokenData.args("-empty");
        LineTokenData lineToken = LineTokenData.newInstance();

        assertEquals("",lineToken.get(0));
        assertTrue(lineToken.isEmpty(0));
    }

    @Test
    public void testStringTrim_false(){
        LineTokenData.args("-notrim");
        LineTokenData lineToken = LineTokenData.newInstance("  "," data "," data x ");

        assertFalse(lineToken.isEmpty(0));
        assertEquals("  ",lineToken.get(0));
        assertEquals(" data ",lineToken.get(1));
    }

    @Test
    public void testStringTrim_true(){
        LineTokenData.args("-trim");
        LineTokenData lineToken = LineTokenData.newInstance("  "," data "," data x ");

        assertTrue(lineToken.isEmpty(0));
        assertEquals("",lineToken.get(0));
        assertEquals("data",lineToken.get(1));
    }

    @Test
    public void size_zero(){
        LineTokenData lineToken = LineTokenData.newInstance();

        assertEquals(0,lineToken.length());
    }

    @Test
    public void contain_true(){
        LineTokenData lineToken = LineTokenData.newInstance("NO IN");

        assertTrue(lineToken.contain(0,"data","NO"));
    }

    @Test
    public void contain_false(){
        LineTokenData lineToken = LineTokenData.newInstance("NO IN");

        assertFalse(lineToken.contain(0,"in","no"));
    }

    @Test
    public void containAll_true(){
        LineTokenData lineToken = LineTokenData.newInstance("NO IN");

        assertTrue(lineToken.containAll(0,"IN","NO"));
    }

    @Test
    public void containAll_false(){
        LineTokenData lineToken = LineTokenData.newInstance("NO IN");

        assertFalse(lineToken.containAll(0,"no","in"));
    }

    @Test
    public void containAllIgnoreCase_true(){
        LineTokenData lineToken = LineTokenData.newInstance("NO IN");

        assertTrue(lineToken.containAllIgnoreCase(0,"in","no"));
    }

    @Test
    public void containAllIgnoreCase_false(){
        LineTokenData lineToken = LineTokenData.newInstance("NO IN");

        assertFalse(lineToken.containAllIgnoreCase(0,"no","net"));
    }

    @Test
    public void replaceToken(){
        LineToken lineToken = LineTokenData.newInstance("data","NO IN");

        lineToken = lineToken.replaceToken(0,"data2");

        assertEquals("data2",lineToken.get(0));
    }

    @Test
    public void println(){
        LineTokenData lineToken = LineTokenData.newInstance("data","NO IN");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        lineToken.println(ps,false);

        assertEquals("data| NO IN|",baos.toString().trim());
    }

    @Test
    public void fixPrintln(){
        LineTokenData lineToken = LineTokenData.newInstance("data","1","NO IN","xx");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        lineToken.fixPrintln(ps,10,1,-10);

        assertEquals("data      1     NO IN",baos.toString().trim());
    }

    @Test
    public void fixPrintln_Zero(){
        LineTokenData lineToken = LineTokenData.newInstance("data","1","NO IN");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        lineToken.fixPrintln(ps,10,0,-10);

        assertEquals("data           NO IN",baos.toString().trim());
    }

    @Test
    public void fixPrintln_Zero2(){
        LineTokenData lineToken = LineTokenData.newInstance("data","1","NO IN");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        lineToken.fixPrintln(ps,10,0,-10,0,0,0,0,0);

        assertEquals("data           NO IN",baos.toString().trim());
    }
    @Test
    public void fixPrintln_TrucateToken(){
        LineTokenData lineToken = LineTokenData.newInstance("data","1","NO IN");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        lineToken.fixPrintln(ps,3,0,2);

        assertEquals("datNO",baos.toString().trim());
    }
}
