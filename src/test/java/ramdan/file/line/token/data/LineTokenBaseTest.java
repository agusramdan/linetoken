package ramdan.file.line.token.data;

import lombok.val;
import org.junit.Assert;
import org.junit.Test;

public class LineTokenBaseTest {

    @Test(expected = RuntimeException.class)
    public void length(){
        val base = new LineTokenBase(null,null,null);

        base.length();
    }

    @Test(expected = RuntimeException.class)
    public void get(){
        val base = new LineTokenBase(null,null,null);

        base.get(1);
    }

    @Test(expected = RuntimeException.class)
    public void getSource(){
        val base = new LineTokenBase(null,null,null);

        Assert.assertNull(base.get(0));
    }
}
