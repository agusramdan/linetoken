package ramdan.file.line.token.data;

import org.junit.Test;
import ramdan.file.line.token.StringUtils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class LineDataTest {

    @Test
    public void equal(){
        assertTrue(StringUtils.equal("data","data"));
        assertTrue(StringUtils.equal(null,(String)null));
    }

    @Test
    public void equal_negatif(){
        assertFalse(StringUtils.equal("data","Data"));
        assertFalse(StringUtils.equal("data",(String)null));
        assertFalse(StringUtils.equal(null,"Data"));
    }

    @Test
    public void equal_NotEqual(){
        LineTokenData lineToken = LineTokenData.newInstance("token-0-data");

        assertFalse(lineToken.equal(0,"salah"));
    }

    @Test
    public void equalIgnoreCase(){
        assertTrue(StringUtils.equalIgnoreCase("token-0-data","Token-0-data"));
        assertTrue(StringUtils.equalIgnoreCase("token-0-data","salah","Token-0-data"));
    }

    @Test
    public void equalIgnoreCase_NotEqual(){
        assertFalse(StringUtils.equalIgnoreCase("token-0-data","salah"));
    }

    @Test
    public void testEqualIgnoreCase(){
        assertTrue(StringUtils.equalIgnoreCase("data","data"));
        assertTrue(StringUtils.equalIgnoreCase("data","Data"));
        assertTrue(StringUtils.equalIgnoreCase(null,(String)null));
    }

    @Test
    public void testEqualIgnoreCase_negatif(){
        assertFalse(StringUtils.equalIgnoreCase("data","data2"));
        assertFalse(StringUtils.equalIgnoreCase("data",(String)null));
        assertFalse(StringUtils.equalIgnoreCase(null,"Data"));
    }

    @Test
    public void contain_true(){
        assertTrue(StringUtils.contain("NO IN","data","NO"));
    }

    @Test
    public void contain_false(){
        assertFalse(StringUtils.contain("NO IN","in","no"));
    }

    @Test
    public void containAll_true(){
        assertTrue(StringUtils.containAll("NO IN","IN","NO"));
    }

    @Test
    public void containAll_false(){
        assertFalse(StringUtils.containAll("NO IN","no","in"));
    }

    @Test
    public void containAllIgnoreCase_true(){
        LineTokenData lineToken = LineTokenData.newInstance("NO IN");

        assertTrue(lineToken.containAllIgnoreCase(0,"in","no"));
    }

    @Test
    public void containAllIgnoreCase_false(){
        assertFalse(StringUtils.containAllIgnoreCase("NO IN","no","net"));
    }

}
