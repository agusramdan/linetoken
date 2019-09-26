package ramdan.file.line.token;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StringSaveTest {

    @Before
    public void before(){
        StringSave.stringSave=false;
        StringSave.stringIntern=false;
    }

    @After
    public void after(){
        StringSave.clear();
    }

    @Test
    public void testStringSave_False(){
        StringSave.save("data");
        assertEquals(0,StringSave.count());
    }

    @Test
    public void testSaveClean_null(){
        StringSave.stringSave=true;

        StringSave.save("data");

        assertEquals(1,StringSave.count());
        assertNull(StringSave.save(null));
    }

    @Test
    public void testSave_null(){
        assertNull(StringSave.save(null));
    }

    @Test
    public void testSave_empty(){

        assertSame("",StringSave.save(""));
    }

    @Test
    public void testArgs_save_true(){
        StringSave.args("-save");

        assertTrue(StringSave.stringSave);
    }

    @Test
    public void testArgs_save_false(){
        StringSave.args("-nosave");

        assertFalse(StringSave.stringSave);
    }

    @Test
    public void testArgs_intern_true(){
        StringSave.args("-intern");

        assertTrue(StringSave.stringIntern);
    }

    @Test
    public void testSave_data(){
        StringSave.stringSave=true;
        String data = StringSave.save("data");

        assertSame(data,StringSave.save(new String("data")));
    }

    @Test
    public void testSave_intern(){
        StringSave.stringSave=true;
        StringSave.stringIntern=true;
        String data = StringSave.save("data");

        assertSame(data,StringSave.save(new String("data")));
        assertEquals(0,StringSave.count());
    }

    @Test
    public void testPrint(){
        StringSave.stringSave=true;
        StringSave.stringIntern=false;
        String data = StringSave.save("data");

        StringSave.print(null);

        assertSame(data,StringSave.save(new String("data")));
        assertEquals(1,StringSave.count());
    }
}
