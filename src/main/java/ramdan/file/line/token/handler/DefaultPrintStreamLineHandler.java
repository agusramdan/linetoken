package ramdan.file.line.token.handler;

import lombok.Getter;
import lombok.Setter;
import ramdan.file.line.token.DestroyFailedException;
import ramdan.file.line.token.Destroyable;
import ramdan.file.line.token.Line;
import ramdan.file.line.token.StreamUtils;

import java.io.*;
import java.util.zip.GZIPOutputStream;

public class DefaultPrintStreamLineHandler implements LineHandler, Closeable, Destroyable {


    private PrintStream printStream;

    public DefaultPrintStreamLineHandler(PrintStream printStream) {
        this.printStream = printStream;
    }

    public DefaultPrintStreamLineHandler() {
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

    }

    @Override
    public boolean isDestroyed() {
        return printStream==null;
    }

    @Override
    public Line process(Line lineToken) {
        if(isDestroyed())return lineToken;
        if(lineToken!=null) {
            lineToken.println(printStream);
        }
        return lineToken;
    }
}
