package ramdan.file.line.token.data;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.LineTokensHolder;

import java.util.*;

public class PairLineToken implements LineTokensHolder {
    @Setter
    private Comparator<LineToken> comparator;
    @Getter
    private String defaultStart;
    @Getter
    private String defaultEnd;
    private LineToken head;
    private LineToken tail;
    @Getter
    private State state = State.MEMORY;
    @Getter
    private Sort sort = Sort.UNSORT;
    @Setter
    private LineToken startBlock;
    @Setter
    private LineToken endBlock;

    @Getter
    private List<LineToken> contents=new ArrayList<>();

    public PairLineToken(String defaultStart, String defaultEnd) {
        this.defaultStart = defaultStart;
        this.defaultEnd = defaultEnd;
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

    public boolean add(LineToken lineToken){
        sort = Sort.UNSORT;
        return contents.add(lineToken);
    }

    public void sort(){
        Collections.sort(contents,comparator);
    }
    public List<LineToken> getPairLineToken(){
        val result = new ArrayList<LineToken>();
        result.add(getStartBlock());
        result.addAll(contents);
        result.add(getEndBlock());
        return result;
    }

    @Override
    public LineToken headContent() {
        return head;
    }

    @Override
    public LineToken tailContent() {
        if(tail == null && !contents.isEmpty()){
           tail= contents.get(contents.size()-1).copyLineToken();
        }
        return tail;
    }
    private Iterator<LineToken> it;
    private void ensureIteratorReady(){
        if(it ==null){
            it = contents.iterator();
        }
    }
    @Override
    public void reset() {
        it = contents.iterator();
    }

    @Override
    public boolean hashNextContent() {
        ensureIteratorReady();
        return it.hasNext();
    }

    @Override
    public LineToken nextContent() {
        return it.next();
    }

    @Override
    public boolean addContent(LineToken lineToken) {
        if(lineToken==null) return false;
        if(head==null){
            head = lineToken.copyLineToken();
        }
        tail=null;
        contents.add(lineToken);
        return true;
    }

    @Override
    public Sort mergeContent(LineTokensHolder left) {
        while (left.hashNextContent()){
            contents.add(left.nextContent());
        }
        if( comparator!=null){
           Collections.sort(contents,comparator);
        }else {
            sort = Sort.UNSORT;
        }
        return sort;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isEOF() {
        return false;
    }
    public State saveToDisk(){
        return state;
    }
    public State loadToMemory(){
        return state;
    }
}
