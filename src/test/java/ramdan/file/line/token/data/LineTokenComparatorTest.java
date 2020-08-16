package ramdan.file.line.token.data;

import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import ramdan.file.line.token.LineToken;

public class LineTokenComparatorTest {

    @Test
    public void compare(){
        val comparator = new LineTokenComparator(0);
        val lt1 = LineTokenData.newInstance("1");
        val lt2 = LineTokenData.newInstance("1");

        Assert.assertEquals(0,comparator.compare(lt1,lt1));
        Assert.assertEquals(0,comparator.compare(lt1,lt2));
    }

    @Test
    public void compareNull(){
        val comparator = new LineTokenComparator(0);

        Assert.assertEquals(0,comparator.compare(null,null));

        val lt1 = LineTokenData.newInstance("1");
        Assert.assertTrue(comparator.compare(null,lt1)<0);
        Assert.assertTrue(comparator.compare(lt1,null)>0);
    }

    @Test
    public void compare1(){
        val comparator = new LineTokenComparator(0);
        val lt1 = LineTokenData.newInstance("1");
        val lt2 = LineTokenData.newInstance("2");

        Assert.assertTrue(comparator.compare(lt1,lt2)<0);
    }

    @Test
    public void compare2(){
        val comparator = new LineTokenComparator(0);
        val lt1 = LineTokenData.newInstance("2");
        val lt2 = LineTokenData.newInstance("1");

        Assert.assertTrue(comparator.compare(lt1,lt2)>0);
    }
}
