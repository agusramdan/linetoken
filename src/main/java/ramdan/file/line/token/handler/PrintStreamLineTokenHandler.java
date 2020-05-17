package ramdan.file.line.token.handler;

import lombok.Setter;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.Tokens;

import java.io.PrintStream;

public class PrintStreamLineTokenHandler extends DefaultLineTokenHandler{

    private final PrintStream printStream;
    @Setter
    private String tagdelimiter=null;
    @Setter
    private String tokendelimiter=null;
    @Setter
    private boolean printLine=false;
    public PrintStreamLineTokenHandler(PrintStream printStream) {
        this.printStream = printStream;
    }
    @Override
    public Tokens process(LineToken lineToken) {
        lineToken.println(printStream,tagdelimiter,tokendelimiter,printLine);
        printStream.flush();
        return lineToken;
    }
}