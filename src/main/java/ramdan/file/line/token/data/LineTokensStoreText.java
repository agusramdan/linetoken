package ramdan.file.line.token.data;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ramdan.file.line.token.DestroyFailedException;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.LineTokens;
import ramdan.file.line.token.StreamUtils;

import java.io.*;
import java.util.Iterator;

/**
 * Store in text mode
 */
@Slf4j
public class LineTokensStoreText extends LineTokensStoreAbstract {

    private static File createTempFile() throws IOException{
        return File.createTempFile("LineTokensStoreText_"+(System.currentTimeMillis()/3600000)+"_","_lts.txt");
    }
    @Setter
    private boolean usingCopyAllAll = true;
    private File file;// store file
    private PrintStream printStream;
    private final Object writeLock= new Object();

    @Getter
    private boolean destroyed;
    public LineTokensStoreText() throws IOException {
        this(createTempFile());
    }
    public LineTokensStoreText(File file) {
        this.file = file;
        log.debug("Create file {}",file);
    }
    private void ensureOpen() throws IOException {
        synchronized (writeLock) {
            if (printStream == null) {
                printStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(file, true)));
            }
        }
    }
    public long length(){
        return file.length();
    }

    protected  void addToStore(LineToken lt) throws IOException{
        ensureOpen();
        // todo create method serializable line
        lt.println(printStream,false);
        StreamUtils.flushIgnore(printStream);
    }
    public void addAll(LineTokensStore lts) throws IOException{
        if(usingCopyAllAll && lts instanceof LineTokensStoreText){
            if(lts.isEmpty()) return;
            synchronized (writeLock){
                try {
                    StreamUtils.flushIgnore(printStream);
                    StreamUtils.closeIgnore(printStream);
                    printStream=null;
                    val lstx = (LineTokensStoreText)lts;
                    StreamUtils.copy(lstx.file,this.file);
                    if(head==null){
                        this.head=lts.head();
                    }
                    tail = lts.tail();
                    count+=lts.count();

                }finally {
                    ensureOpen();
                }
            }
        }else {
            super.addAll(lts);
        }

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
    public void add(LineToken lt){
        synchronized (writeLock){
            super.add(lt);
        }
    }
    @Override
    public Iterator<LineToken> iterator() {
        return new IteratorImpl();
    }

    @Override
    public void close() throws IOException {
        release();
    }

    public void delete(){
        release();
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

    public void release(){
        synchronized (writeLock) {
            StreamUtils.flushIgnore(printStream);
            StreamUtils.closeIgnore(printStream);
            printStream = null;
        }
    }
    private class IteratorImpl implements Iterator<LineToken> {
        private LineToken next;
        //private ObjectInputStream ois;
        private LineNumberReader reader;
        private boolean eof = false;
        private int loaded=0;
        public IteratorImpl() {
            try {
                if(file.length()>0) {
                    InputStreamReader isr = new FileReader(file);
                    reader = new LineNumberReader(isr);
                }else {
                    log.warn("No data at file {}",file);
                }
                //String str;
                //while ((str = reader.readLine())!= null){
                //ois= new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
            }
//            catch (EOFException e){
//                log.warn("No data at file {}",file);
//                eof=true;
//                //ois= null;
//            }

            catch (IOException e1) {
                log.error("Error Open file",e1);
            }
            eof = reader==null;
        }

        private void ensureLoad(){
            if(eof) return;
            if(next==null){
                try {
                    val str = reader.readLine();
                    if(str!=null){
                         next=LineTokenData.parse(str);
                    }else {
                        eof=true;
                    }
                    //next = (LineToken) ois.readObject();
                    loaded++;
                }
//                catch (EOFException e){
//                    eof=true;
//                    StreamUtils.closeIgnore(ois);
//                    next = null;
//                    ois= null;
//                    return;
//                }

                catch (IOException | OutOfMemoryError  e) {
                    log.error("Loaded {}",loaded);
                    throw new RuntimeException(e);
                }
                if(next==null){
                    eof = true;

                    StreamUtils.closeIgnore(reader);
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
