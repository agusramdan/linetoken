package ramdan.file.line.token;

import ramdan.file.line.token.handler.IntegerConversionErrorHandler;

import java.io.PrintStream;

public interface LineToken {
    String getFileName();
    Integer getStart() ;
    Integer getEnd();
    int length();
    Line getSource();
    boolean isEOF();
    String get(int index);
    int getInt(int index);
    int getInt(int index, IntegerConversionErrorHandler handler);
    boolean isEmpty(int index);
    boolean equal(int index, String ... parameter);
    boolean equalIgnoreCase(int index, String ... parameter);
    boolean contain(int index, String ... parameter);
    boolean containAll(int index, String ... parameter);
    boolean containIgnoreCase(int index, String ... parameter);
    boolean containAllIgnoreCase(int index, String ... parameter);
    LineToken replaceToken(int index, String token);
    LineToken toLineToken(String token);
    LineToken copyLineToken();
    void printLine(PrintStream ps);
    void println(PrintStream ps);
    void println(PrintStream ps, String delimiter,boolean printLine);
    void fixPrintln(PrintStream ps,int ... spaces);
    String[] copy(int idxStart);
    //void arraycopy(int sourceIdxStart,String[] destination,int destinationIndexStart,int lengthCopy);
}
