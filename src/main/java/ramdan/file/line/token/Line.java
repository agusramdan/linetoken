package ramdan.file.line.token;

import ramdan.file.line.token.handler.IntegerConversionErrorHandler;

import java.io.File;
import java.io.PrintStream;
import java.io.Serializable;

public interface Line extends Serializable {
    long default_time =System.currentTimeMillis();
    long ten_days = 864000000;// 10 hari
    File getSource();
    int getNo();
    String getLine();
    int length();
    long timestamp();
    boolean isEmpty();
    boolean isEOF();
    boolean equal(String... parameter) ;
    boolean equalIgnoreCase(String... parameter);
    boolean contain(String... parameter);
    boolean containAll(String... parameter);
    boolean containIgnoreCase(String... parameter);
    boolean containAllIgnoreCase(String... parameter);
    void println(PrintStream ps);
    Line trim();
}
