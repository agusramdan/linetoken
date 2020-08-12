package ramdan.file.line.token.data;

import lombok.val;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.StreamUtils;

import java.io.IOException;

import static org.junit.Assert.*;

public class LineTokensStoreTextTest {

    private LineTokensStoreText store;
    private LineToken halo = LineTokenData.newInstance("HALLO","data");

    @Before
    public void before() throws IOException{
        store = new LineTokensStoreText();
    }

    @After
    public void after(){
        StreamUtils.destroyIgnore(store);
    }

    @Test
    public void Store_add_exist() throws IOException {

        store.add(halo);

        assertEquals(1,store.count);
    }
    @Test
    public void Store_empty_zero() throws IOException {

        assertEquals(0,store.count);
    }

    @Test
    public void Store_add_head() throws IOException {

        store.add(halo);

        assertEquals(halo,store.head());
    }

    @Test
    public void Store_empty_headNull() throws IOException {

        assertNull(store.head());
    }

    @Test
    public void Store_add_tail() throws IOException {

        store.add(halo);

        assertEquals(halo,store.tail());
    }

    @Test
    public void Store_iteratorHasNext_true() throws IOException {

        store.add(halo);

        assertTrue(store.iterator().hasNext());
    }
    @Test
    public void Store_iteratorHasNext_data() throws IOException {

        store.add(halo);

        assertEquals(halo,store.iterator().next());
    }

    @Test
    public void Store_addAll_data() throws IOException {
        val newStore = new LineTokensStoreText();

        store.add(halo);

        newStore.addAll(store);

        assertEquals(halo,newStore.iterator().next());
    }
}
