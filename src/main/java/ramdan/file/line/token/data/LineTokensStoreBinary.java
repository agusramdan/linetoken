package ramdan.file.line.token.data;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ramdan.file.line.token.*;

import java.io.*;
import java.util.Iterator;

@Slf4j
public class LineTokensStoreBinary extends LineTokensStoreAbstract {
    private static File createTempFile() throws IOException{
        return File.createTempFile("LineTokensStoreBin_"+(System.currentTimeMillis()/3600000)+"_","_lts.bin");
    }
    private File file;// store file
    private ObjectOutputStream outputStream;

    @Getter
    private boolean destroyed;
    public LineTokensStoreBinary() throws IOException{
        this(createTempFile());
    }
    public LineTokensStoreBinary(File file) {
        this.file = file;
        log.debug("Create file {}",file);
    }
    private void ensureOpen() throws IOException {
        if(outputStream==null)
        outputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));

    }

    public long length(){
        return file.length();
    }

    protected  void addToStore(LineToken lt) throws IOException{
        ensureOpen();
        outputStream.writeObject(lt);
        outputStream.flush();
        outputStream.reset();
    }

    @Override
    public void release() {
        // not support release
        //StreamUtils.closeIgnore(outputStream);
        //outputStream = null;
    }

    @Override
    public void addAll(LineTokensStore lts) throws IOException{
        addAll0(lts);
    }

    public void addAll(Iterable<LineToken>it) throws IOException {
        addAll0(it);
    }
    private void addAll0(Iterable<LineToken>it) throws IOException {
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
        log.debug("Delete file {}",file);
    }
    @Override
    public void finalize() {
        try {
            this.destroy();
        } catch (DestroyFailedException e) {
            log.error("Filed",e);
        }
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
                log.warn("No data at file {}",file);
                eof=true;
                ois= null;
            }catch (IOException e) {
                log.error("Error Open file",e);
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
                    log.error("Loaded {}",loaded);
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
