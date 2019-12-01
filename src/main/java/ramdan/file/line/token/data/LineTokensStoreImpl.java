package ramdan.file.line.token.data;

import lombok.Getter;
import lombok.val;
import ramdan.file.line.token.*;
import ramdan.file.line.token.handler.Callback;

import java.io.*;
import java.util.Iterator;

public class LineTokensStoreImpl implements LineTokens,Closeable, Callback<LineToken> , Destroyable {

    private File file;// store file
    private ObjectOutputStream outputStream;
    private LineToken head;
    private LineToken tail;
    @Getter
    private boolean destroyed;

    public LineTokensStoreImpl(File file) {
        this.file = file;
    }
    private void ensureOpen() throws IOException {
        if(outputStream==null)
        outputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
    }
    private int count =0;

    public boolean empty(){
        return count==0;
    }
    public int count(){
        return count;
    }
    public long length(){
        return file.length();
    }
    public LineToken head(){
        return head;
    }
    public LineToken tail(){
        return tail;
    }
    public void add(LineToken lt) {
        try {
            ensureOpen();
            outputStream.writeObject(lt);
            outputStream.flush();
            count++;
            if (head == null) {
                head = lt;
            }
            tail = lt;
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    public void addAll(Iterable<LineToken>it) throws IOException {
        ensureOpen();
        for (LineToken lt : it) {
            add(lt);
        }
    }
    @Override
    public Iterator<LineToken> iterator() {
        return new IteratorImpl();
    }

    @Override
    public void close() throws IOException {
        StreamUtils.closeIgnore(outputStream);
    }

    public void delete(){
        StreamUtils.closeIgnore(outputStream);
        file.delete();
        destroyed=true;
    }

    @Override
    public void call(LineToken lineToken) {
        add(lineToken);
    }

    @Override
    public boolean isEmpty() {
        return empty();
    }

    @Override
    public boolean isEOF() {
        return false;
    }

    @Override
    public void destroy() throws DestroyFailedException {
        delete();
    }

    private class IteratorImpl implements Iterator<LineToken> {
        private LineToken next;
        private ObjectInputStream ois;
        private boolean eof = false;
        private int loaded=0;
        public IteratorImpl() {
            try {
                ois= new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
            }catch (java.io.EOFException e){
                eof=true;
                ois= null;
            }catch (IOException e) {
                e.printStackTrace();
            }
            eof = ois==null;
        }

        private void ensureLoad(){
            if(eof) return;
            if(next==null){
                try {
                    next = (LineToken) ois.readObject();
                    loaded++;
                } catch (java.io.EOFException e){
                    eof=true;
                    StreamUtils.closeIgnore(ois);
                    next = null;
                    ois= null;
                    return;
                } catch (IOException |java.lang.OutOfMemoryError |ClassNotFoundException e) {
                    System.err.printf("Loaded %d \n",loaded);
                    throw new RuntimeException(e);
                }
                if(next==null){
                    eof = true;
                    StreamUtils.closeIgnore(ois);
                }
            }
        }
        @Override
        public boolean hasNext() {
            ensureLoad();
            return next!=null;
        }

        @Override
        public LineToken next() {
            ensureLoad();
            val result = next;
            next=null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }
}
