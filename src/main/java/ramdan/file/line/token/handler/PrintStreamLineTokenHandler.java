package ramdan.file.line.token.handler;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.Tokens;

import java.io.PrintStream;

public class PrintStreamLineTokenHandler extends DefaultLineTokenHandler{

    private PrintStream printStream;
    public PrintStreamLineTokenHandler(PrintStream printStream) {
        this.printStream = printStream;
    }

    @Override
    public Tokens process(LineToken lineToken) {
        lineToken.println(printStream);
        printStream.flush();
        return lineToken;
    }
}
