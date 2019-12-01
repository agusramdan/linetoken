package ramdan.file.line.token.handler;

import java.util.Comparator;
import java.util.Iterator;

public interface Sortable<T> extends Iterator<T> {
    enum Sort{
        UNSORT,SORTED
    }
    void sort();
    void sort(Comparator<T> comparator);
    Sort getSortStatus();
}
