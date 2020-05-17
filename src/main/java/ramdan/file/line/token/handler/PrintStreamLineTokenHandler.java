package ramdan.file.line.token.handler;

import lombok.Setter;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.Tokens;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class PrintStreamLineTokenHandler extends DefaultLineTokenHandler implements OutputLineTokenHandler{

    private final PrintStream printStream;
    @Setter
    private String tagdelimiter=null;
    @Setter
    private String tokendelimiter=null;
    @Setter
    private boolean printLine=false;
    @Setter
    private boolean flushEachLine=true;
    public PrintStreamLineTokenHandler(PrintStream printStream) {
        this.printStream = printStream;
    }
    @Override
    public Tokens process(LineToken lineToken) {
        lineToken.println(printStream,tagdelimiter,tokendelimiter,printLine);
        if(flushEachLine) {
            printStream.flush();
        }
        return lineToken;
    }

    @Override
    public void setExtension(String extension) {

    }

    @Override
    public void setFileInput(File input) {

    }

    @Override
    public void setBaseDirectoryInput(File baseInput) {

    }

    @Override
    public void setFileOutput(File input) {

    }

    @Override
    public void setBaseDirectoryOutput(File baseInput) {

    }

    @Override
    public void flush() {
        printStream.flush();
    }

    @Override
    public void close() throws IOException {
        printStream.close();
    }
}