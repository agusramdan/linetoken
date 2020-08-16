package ramdan.file.line.token.data;

import lombok.val;
import org.junit.Assert;
import org.junit.Test;

public class LineTokenComparatorTest {

    @Test
    public void compare() {
        val comparator = new LineTokenComparator(0);
        val lt1 = LineTokenData.newInstance("1");
        val lt2 = LineTokenData.newInstance("1");

        Assert.assertEquals(0, comparator.compare(lt1, lt1));
        Assert.assertEquals(0, comparator.compare(lt1, lt2));
    }

    @Test
    public void compare3() {
        val comparator = new LineTokenComparator(0, 1, 2);
        val lt1 = LineTokenData.newInstance("1", "2", "3");
        val lt2 = LineTokenData.newInstance("1", "2", "3");

        Assert.assertEquals(0, comparator.compare(lt1, lt1));
        Assert.assertEquals(0, comparator.compare(lt1, lt2));
    }

    @Test
    public void compareNull() {
        val comparator = new LineTokenComparator(0);

        Assert.assertEquals(0, comparator.compare(null, null));

        val lt1 = LineTokenData.newInstance("1");
        Assert.assertTrue(comparator.compare(null, lt1) < 0);
        Assert.assertTrue(comparator.compare(lt1, null) > 0);
    }

    @Test
    public void compare1() {
        val comparator = new LineTokenComparator(0);
        val lt1 = LineTokenData.newInstance("1");
        val lt2 = LineTokenData.newInstance("2");

        Assert.assertTrue(comparator.compare(lt1, lt2) < 0);
    }

    @Test
    public void compare_1_2_2_result_negatif() {
        val comparator = new LineTokenComparator(0);
        val lt1 = LineTokenData.newInstance("1");
        val lt2 = LineTokenData.newInstance("2");

        Assert.assertTrue(comparator.compare(lt1, lt2) < 0);
    }

    @Test
    public void compare_Resutl_Positif() {
        val comparator = new LineTokenComparator(0, 1, 2);
        val lt1 = LineTokenData.newInstance("1", "2", "2");
        val lt2 = LineTokenData.newInstance("1", "2", "1");

        Assert.assertTrue(comparator.compare(lt1, lt2) > 0);
    }
}
