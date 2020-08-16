package ramdan.file.line.token.data;

import lombok.val;
import org.junit.Test;
import ramdan.file.line.token.StringUtils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class LineDataTest {

    @Test
    public void equal(){
        val ld = new LineData("data");

        assertTrue(ld.equal("data"));
    }

    @Test
    public void equal_negatif(){
        val ld = new LineData("data");

        assertFalse(ld.equal("Data"));
    }

    @Test
    public void equalIgnoreCase(){
        val ld = new LineData("token-0-data");

        assertTrue(ld.equalIgnoreCase("Token-0-data"));

    }

    @Test
    public void equalIgnoreCase_NotEqual(){
        val ld = new LineData("token-0-data");

        assertFalse(ld.equalIgnoreCase("Token-0-salah"));
    }

    @Test
    public void contain_true(){
        val ld = new LineData("NO IN");

        assertTrue(ld.contain("data","NO"));
    }

    @Test
    public void contain_false(){
        val ld = new LineData("NO IN");

        assertFalse(ld.equalIgnoreCase("in","no"));
    }

    @Test
    public void containAll_true(){
        val ld = new LineData("NO IN");

        assertTrue(ld.containAll("IN","NO"));
    }

    @Test
    public void containAll_false(){
        val ld = new LineData("NO IN");

        assertFalse(ld.containAll("no","in"));
    }

    @Test
    public void containAllIgnoreCase_true(){
        val ld = new LineData("NO IN");

        assertTrue(ld.containAllIgnoreCase("no","in"));
    }

    @Test
    public void containAllIgnoreCase_false(){
        val ld = new LineData("NO IN");

        assertFalse(ld.containAllIgnoreCase("no","net"));
    }

    @Test
    public void containIgnoreCase_true(){
        val ld = new LineData("NO IN");

        assertTrue(ld.containIgnoreCase("in","no","not","found"));

    }

    @Test
    public void containIgnoreCase_false(){
        val ld = new LineData("NO IN");

        assertFalse(ld.containIgnoreCase("not","found"));
    }

}
