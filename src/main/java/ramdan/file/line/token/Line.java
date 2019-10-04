package ramdan.file.line.token;

import ramdan.file.line.token.handler.IntegerConversionErrorHandler;

import java.io.File;
import java.io.PrintStream;

public interface Line {
    File getSource();
    int getNo();
    String getLine();
    int length();
    boolean isEmpty();
    boolean isEOF();
    boolean contain(String... parameter);
    boolean containAll(String... parameter);
    boolean containIgnoreCase(String... parameter);
    boolean containAllIgnoreCase(String... parameter);
    void println(PrintStream ps);
}
