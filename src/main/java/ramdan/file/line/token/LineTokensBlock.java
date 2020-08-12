package ramdan.file.line.token;

import ramdan.file.line.token.filter.LineTokenFilter;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public interface LineTokensBlock extends Tokens{
    void setComparator(Comparator<LineToken> comparator);
    int count();
    LineToken getStartBlock();
    void setStartBlock(LineToken lineToken);
    LineToken getEndBlock();
    void setEndBlock(LineToken lineToken);
    LineToken headContent();
    LineToken tailContent();
    LineToken findFirst(LineTokenFilter filter);
    List<LineToken> findAll(LineTokenFilter filter);
    boolean replace(LineToken lineTokenOld, LineToken lineTokenNew);
    void resetIterator();
    boolean hashNextContent();
    LineToken nextContent();
    boolean addContent(LineToken lineToken);
    boolean addContent(Iterable<? extends LineToken> collection);
    State getState();
    void sort();
    void sort(Comparator<LineToken> comparator);
    enum State{
        MEMORY, DISK
    }
    enum Mode{
        BINARY, TEXT
    }
    interface Factory{
        LineTokensBlock newInstance(String name, String startTagname, String endTagname,Mode mode);
        LineTokensBlock newInstance(String name, String startTagname, String endTagname);
        LineTokensBlock newInstance(String name);
    }
}
