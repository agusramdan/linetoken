package ramdan.file.line.token.data;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import ramdan.file.line.token.*;
import ramdan.file.line.token.handler.Sortable;
import ramdan.file.line.token.filter.LineTokenFilter;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

@Slf4j
public class LineTokensBlockStore extends AbstractLineTokensBlock implements LineTokensBlock,Sortable<LineToken> , Destroyable {

    @Getter
    private Sort sortStatus = Sort.UNSORT;

    @Getter
    private Mode mode;

    private LineTokensStore contents;
    @Setter
    private int limit =1024*4;

    private LineTokensStore create() throws IOException {
        if(mode==Mode.TEXT){
            return new LineTokensStoreText();
        }
        return new LineTokensStoreBinary();
    }
    public LineTokensBlockStore(String name, String defaultStart, String defaultEnd) {
        this(name,defaultEnd,defaultEnd,Mode.BINARY);
    }
    public LineTokensBlockStore(String name, String defaultStart, String defaultEnd,Mode mode) {
        super(name, defaultStart, defaultEnd);
        this.mode=mode;
        try {
            contents= create();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public LineTokensBlockStore(String defaultStart, String defaultEnd) {
        this("", defaultStart, defaultEnd);

    }

    public void sort() {
        sort(comparator);
    }

    @Override
    public void sort(Comparator<LineToken> comparator) {
        log.info("LineTokensBlockStore#Sort");
        if (comparator == null) return;
        it = null;
        val count = contents.count();
        if (count<=1) return;

        var part= 1;
        var perPart = count;
        var mod =0;
        while(limit<perPart){
            part*=2;
            mod = count%part;
            perPart=(count/part)+(mod!=0?1:0);
        }
        log.debug("count : {} , Part : {} , Per Part: {} , mod : {}",count,part,perPart,mod);
        val presorting = new LinkedList<LineTokensStore>();
        try {
            val tmp = new ArrayList<LineToken>(perPart+1);
            Iterator<LineToken> i = contents.iterator();
            while (i.hasNext()){
                tmp.add(i.next());
                if(!tmp.isEmpty() && (!i.hasNext() || tmp.size()>=perPart)){
                    Collections.sort(tmp,comparator);
                    val pre = create();
                    pre.addAll(tmp);
                    tmp.clear();
                    presorting.add(pre);
                    pre.release();
                    log.debug("pre sorting : {}  ",presorting.size());
                }
            }
            while (presorting.size()>1){
                val p1 =presorting.pop();
                if(p1.empty()){
                    log.debug("p1 empty ");
                    continue;
                }
                val p2 =presorting.pop();
                if(p2.empty()){
                    log.debug("p2 empty ");
                    presorting.add(p1);
                    continue;
                }
                LineTokensStore m ;
                // chek mungkin tidak perlu soring hanya copy saja  memper cepat prosess
                if(comparator.compare(p1.tail(),p2.head()) <= 0){
                    log.debug("move p2 to p1");
                    m = p1;
                    m.addAll(p2);
                    StreamUtils.destroyIgnore(p2);
                }else if(comparator.compare(p2.tail(),p1.head()) <= 0){
                    log.debug("move p1 to p2");
                    m = p2;
                    m.addAll(p1);
                    StreamUtils.destroyIgnore(p1);
                }else {
                    log.debug("merge p1 and p2 ");
                    m=create();
                    StreamUtils.mergeSort(p1, p2, comparator, m);
                    StreamUtils.destroyIgnore(p1);
                    StreamUtils.destroyIgnore(p2);
                    if(m.empty()){
                        log.warn("merge empty ");
                    }
                }
                presorting.add(m);
                m.release();
                log.debug("merge remaining {}",presorting.size());
            }
        }catch (IOException e){
            log.error("error",e);
        }
        StreamUtils.closeIgnore(contents);
        StreamUtils.destroyIgnore(contents);
        contents=presorting.pop();
        sortStatus = Sort.SORTED;
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
        return false;
    }

    @Override
    public int count() {
        return contents.count();
    }

    @Override
    public LineToken headContent() {
        return contents.head();
    }

    @Override
    public LineToken tailContent() {
        return contents.tail();
    }

    @Override
    public void resetIterator() {
        it = contents.iterator();
    }

    @Override
    public boolean addContent(LineToken lineToken) {
        if (lineToken == null) return false;
        if(!(lineToken instanceof Serializable)){
            lineToken= LineTokenData.ensureLineTokenData(lineToken);
        }
        contents.add(lineToken);
        sortStatus = Sort.UNSORT;
        return true;
    }

    public State saveToDisk() {
        return State.DISK;
    }

    public State loadToMemory() {
        return State.DISK;
    }

    @Override
    public void destroy() throws DestroyFailedException {
        StreamUtils.destroyIgnore(contents);
        contents=null;
    }

    @Override
    public boolean isDestroyed() {
        return contents==null;
    }

    public static class FactoryImpl implements Factory {
        @Setter @Getter
        int limit;
        @Setter @Getter
        private Mode mode=Mode.BINARY;

        public LineTokensBlock newInstance(String name, String startTagname, String endTagname,Mode mode) {
            val n= new LineTokensBlockStore(name,startTagname,endTagname,mode);
            n.limit=limit;
            return n;
        }
        public LineTokensBlock newInstance(String name, String startTagname, String endTagname) {
            return newInstance(name,startTagname,endTagname,mode);
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
