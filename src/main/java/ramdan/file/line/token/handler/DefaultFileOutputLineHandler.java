package ramdan.file.line.token.handler;

import lombok.Getter;
import lombok.Setter;
import ramdan.file.line.token.DestroyFailedException;
import ramdan.file.line.token.Destroyable;
import ramdan.file.line.token.Line;
import ramdan.file.line.token.StreamUtils;

import java.io.*;
import java.util.zip.GZIPOutputStream;

public class DefaultFileOutputLineHandler implements LineHandler, Closeable, Destroyable {
    @Getter
    private boolean destroyed = false;

    @Setter
    private File fileInput;

    @Setter
    private File baseDirectoryInput;

    @Setter
    private File fileOutput;

    @Setter
    private File baseDirectoryOutput;

    @Setter
    String extension;

    private PrintStream printStream;

    public DefaultFileOutputLineHandler(File fileOutput) {
        this.fileOutput = fileOutput;
    }

    public DefaultFileOutputLineHandler() {
    }

    public void ensureFileOutputReady(){
        if(fileOutput==null){
            if(baseDirectoryOutput==null){
                throw new RuntimeException("Base Directory Output not found");
            }
            if(baseDirectoryInput==null){
                throw new RuntimeException("Base Directory Input not found");
            }
            if(fileInput==null){
                throw new RuntimeException("Input not found");
            }
            String fileName = StreamUtils.relative(baseDirectoryInput,fileInput);
            if(extension!=null && !fileName.endsWith(extension)){
                fileName = fileName+extension;
            }
            fileOutput = new File(baseDirectoryOutput,fileName);
            fileOutput.getParentFile().mkdirs();
            if(fileOutput.equals(fileInput)) throw new RuntimeException("Same file Input Output");
        }
    }
    private void ensureReady(){
        if(printStream==null) {
            ensureFileOutputReady();
            try {
                if (fileOutput.getName().endsWith(".gz")) {
                    printStream = new PrintStream(new GZIPOutputStream(new FileOutputStream(fileOutput)));
                } else {
                    printStream = new PrintStream(fileOutput);
                }
                destroyed = false;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    //@Override
    public void flush() {
        StreamUtils.flushIgnore(printStream);
    }

    //@Override
    public void close() throws IOException {
        StreamUtils.closeIgnore(printStream);
    }


    @Override
    public void destroy() throws DestroyFailedException {
        printStream=null;
        fileInput=null;
        fileOutput=null;
        baseDirectoryInput=null;
        baseDirectoryOutput=null;
        destroyed=true;
    }

    @Override
    public Line process(Line lineToken) {
        ensureReady();
        lineToken.println(printStream);
        return lineToken;
    }
}
