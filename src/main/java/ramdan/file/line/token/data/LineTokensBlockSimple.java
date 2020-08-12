package ramdan.file.line.token.data;

import lombok.Getter;
import lombok.val;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.LineTokensBlock;
import ramdan.file.line.token.handler.Sortable;
import ramdan.file.line.token.filter.LineTokenFilter;

import java.util.*;

public class LineTokensBlockSimple extends AbstractLineTokensBlock implements LineTokensBlock,Sortable<LineToken> {

    @Getter
    private Sortable.Sort sortStatus = Sortable.Sort.UNSORT;

    @Getter
    private List<LineToken> contents = new ArrayList<>();
    public LineTokensBlockSimple(String name, String defaultStart, String defaultEnd, LineToken startBlock, LineToken endBlock,List<LineToken> contents) {
        super(name, defaultStart, defaultEnd, startBlock, endBlock);
        this.contents.addAll(contents);
    }

    public LineTokensBlockSimple(String name, String defaultStart, String defaultEnd, LineToken startBlock, LineToken endBlock) {
        super(name, defaultStart, defaultEnd, startBlock, endBlock);
    }
    public LineTokensBlockSimple(String name, String defaultStart, String defaultEnd) {
        super(name, defaultStart, defaultEnd);
    }
    public LineTokensBlockSimple(String defaultStart, String defaultEnd) {
        this("", defaultStart, defaultEnd);
    }

    public void sort() {
        sort(comparator);
    }

    @Override
    public void sort(Comparator<LineToken> comparator) {
        if (comparator == null) return;
        it = null;
        Collections.sort(contents, comparator);
        sortStatus = Sortable.Sort.SORTED;
    }

    @Override
    public LineToken findFirst(LineTokenFilter filter) {
        for (LineToken lt : contents) {
            if (filter.accept(lt)) return lt;
        }
        return null;
    }

    @Override
    public List<LineToken> findAll(LineTokenFilter filter) {
        val buffer = new ArrayList<LineToken>();
        for (LineToken lt : contents) {
            if (filter.accept(lt)) buffer.add(lt);
        }
        return buffer;
    }

    @Override
    public boolean replace(LineToken lineTokenOld, LineToken lineTokenNew) {
        int idx = contents.indexOf(lineTokenOld);
        if (idx > 0) {
            contents.set(idx, lineTokenNew);
        }
        return false;
    }

    @Override
    public int count() {
        return contents.size();
    }

    @Override
    public LineToken headContent() {
        if (head == null && !contents.isEmpty()) {
            head = contents.get(0);
        }
        return head;
    }

    @Override
    public LineToken tailContent() {
        if (tail == null && !contents.isEmpty()) {
            tail = contents.get(contents.size() - 1).copyLineToken();
        }
        return tail;
    }

    @Override
    public void resetIterator() {
        it = contents.iterator();
    }

    @Override
    public boolean addContent(LineToken lineToken) {
        if (lineToken == null) return false;
        tail = null;
        contents.add(lineToken);
        sortStatus = Sortable.Sort.UNSORT;
        return true;
    }

    @Override
    protected void ensureIteratorReady() {
        if(it==null){
            resetIterator();
        }
    }

    public State saveToDisk() {
        return State.MEMORY;
    }

    public State loadToMemory() {
        return State.MEMORY;
    }

    public static class FactoryImpl implements LineTokensBlock.Factory {

        @Override
        public LineTokensBlock newInstance(String name, String startTagname, String endTagname, Mode mode) {
            return newInstance(name,startTagname,endTagname);
        }

        @Override
        public LineTokensBlock newInstance(String name, String startTagname, String endTagname) {
            return new LineTokensBlockSimple(name,startTagname,endTagname);
        }

        @Override
        public LineTokensBlock newInstance(String name) {
            String startTagname;
            String endTagname;
            if(name.contains("_")){
                startTagname= "BSTART_"+name;
                endTagname = "BEND_"+name;
            }else {
                startTagname= "BSTART"+name;
                endTagname = "BEND"+name;
            }
            return newInstance(name,startTagname,endTagname);
        }
    }
}
