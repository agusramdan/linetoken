package ramdan.file.line.token;

import lombok.val;
import static org.junit.Assert.*;

import org.junit.Test;
import ramdan.file.line.token.data.LineTokenAbstract;

public class LineTokenAbstractTest {

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
