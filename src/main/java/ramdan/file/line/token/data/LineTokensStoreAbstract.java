package ramdan.file.line.token.data;

import lombok.Getter;
import ramdan.file.line.token.LineToken;

import java.io.IOException;

public abstract class LineTokensStoreAbstract implements LineTokensStore{

    @Getter
    protected LineToken head=null;
    @Getter
    protected LineToken tail=null;
    @Getter
    protected boolean destroyed;
    protected int count =0;

    public boolean empty(){
        return count==0;
    }
    public int count(){
        return count;
    }
    /*
    public long length(){
        return file.length();
    }
    */
    public LineToken head(){
        return head;
    }
    public LineToken tail(){
        return tail;
    }
    public void add(LineToken lt) {
        try {
            addToStore(lt);
            count++;
            if (head == null) {
                head = lt;
            }
            tail = lt;
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    protected abstract void addToStore(LineToken lt) throws IOException;

    @Override
    public void addAll(LineTokensStore lts) throws IOException{
        if(lts.isEmpty()) return;
        addAll((Iterable<LineToken>)lts);
    }

    @Override
    public void release() {

    }
}
