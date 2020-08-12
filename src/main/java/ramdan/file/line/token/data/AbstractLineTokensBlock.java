package ramdan.file.line.token.data;

import lombok.Getter;
import lombok.Setter;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.LineTokensBlock;
import ramdan.file.line.token.handler.Sortable;

import java.util.Comparator;
import java.util.Iterator;

public abstract class AbstractLineTokensBlock implements LineTokensBlock,Sortable<LineToken>{
    @Setter
    protected Comparator<LineToken> comparator;
    @Getter
    private String name;
    @Getter
    private String defaultStart;
    @Getter
    private String defaultEnd;
    protected LineToken head;
    protected LineToken tail;
    @Getter
    private State state = State.MEMORY;
    @Getter
    private Sort sortStatus = Sort.UNSORT;
    @Setter
    private LineToken startBlock;
    @Setter
    private LineToken endBlock;

    public AbstractLineTokensBlock(String name, String defaultStart, String defaultEnd, LineToken startBlock, LineToken endBlock) {
        this.name = name;
        this.defaultStart = defaultStart;
        this.defaultEnd = defaultEnd;
        this.startBlock = startBlock;
        this.endBlock = endBlock;
    }

    public AbstractLineTokensBlock(String name , String defaultStart, String defaultEnd) {
        this.defaultStart = defaultStart;
        this.defaultEnd = defaultEnd;
        this.name = name;
    }

    @Override
    public LineToken getStartBlock() {
        if(startBlock==null){
            startBlock = LineTokenData.newInstance(defaultStart);
        }
        return startBlock;
    }

    @Override
    public LineToken getEndBlock() {
        if(endBlock==null){
            endBlock = LineTokenData.newInstance(defaultEnd);
        }
        return endBlock;
    }

    protected Iterator<LineToken> it;

    protected void ensureIteratorReady(){
        if(it==null){
            resetIterator();
        }
    }

    @Override
    public boolean hashNextContent() {
        ensureIteratorReady();
        return it.hasNext();
    }

    @Override
    public LineToken nextContent() {
        ensureIteratorReady();
        return it.next();
    }


    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isEOF() {
        return false;
    }

    public boolean addContent(Iterable<? extends LineToken> lineToken) {
        if(lineToken==null) return false;
        for (LineToken lt: lineToken) {
            addContent(lt);
        }
        return true;
    }

    public State saveToDisk(){
        return state;
    }
    public State loadToMemory(){
        return state;
    }

    @Override
    public boolean hasNext() {
        return hashNextContent();
    }

    @Override
    public LineToken next() {
        return nextContent();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }
}
