package ramdan.file.line.token.data;

import lombok.val;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LineTokenDataTest {
    @Test
    public void equal_NotEqual(){
        val lineToken = LineTokenData.newInstance("token-0-data");

        assertFalse(lineToken.equal(0,"salah"));
    }
    @Test
    public void containAllIgnoreCase_true(){
        val lineToken = LineTokenData.newInstance("NO IN");

        assertTrue(lineToken.containAllIgnoreCase(0,"in","no"));
    }

    @Test
    public void containIgnoreCase_true(){
        LineTokenData lineToken = LineTokenData.newInstance("NO IN");

        assertTrue(lineToken.containIgnoreCase(0,"in","no","not","found"));
    }

    @Test
    public void containIgnoreCase_false(){
        LineTokenData lineToken = LineTokenData.newInstance("NO IN");

        assertFalse(lineToken.containIgnoreCase(0,"not","found"));
    }
}
