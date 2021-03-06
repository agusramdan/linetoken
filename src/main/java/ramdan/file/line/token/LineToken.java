package ramdan.file.line.token;

import ramdan.file.line.token.handler.DoubleConversionErrorHandler;
import ramdan.file.line.token.handler.IntegerConversionErrorHandler;

import java.io.PrintStream;
import java.util.regex.Pattern;

public interface LineToken extends Comparable<LineToken>,Tokens{
    String getFileName();
    Integer getStart() ;
    Integer getEnd();
    String getTagDelimiter();
    String getTokenDelimiter();
    int length();
    long timestamp();
    Line getSource();
    boolean isEOF();

    String getTagname();
    String getValue();
    String get(int index);
    String maxLen(int idx, int len);
    String maxLenLeft(int idx, int len);
    int getInt(int index);
    int getInt(int index, IntegerConversionErrorHandler handler);
    double getDouble(int index);
    double getDouble(int index, DoubleConversionErrorHandler handler);
    boolean notEmpty(int i);
    boolean isEmpty(int index);
    boolean equal(int index, String ... parameter);
    boolean equalIgnoreCase(int index, String ... parameter);
    boolean contain(int index, String ... parameter);
    boolean containAll(int index, String ... parameter);
    boolean containIgnoreCase(int index, String ... parameter);
    boolean containAllIgnoreCase(int index, String ... parameter);
    boolean matches(String pattern, int ... idx);
    boolean matchesAll(String pattern, int ... idx);
    boolean matches(Pattern pattern, int ... idx);
    boolean matchesAll(Pattern pattern, int ... idx);
    LineToken replaceToken(int index, String token);
    LineToken toLineToken(String token);
    LineToken copyLineToken();
    void printLine(PrintStream ps);
    void println(PrintStream ps);
    void println(PrintStream ps, boolean printLine);
    void println(PrintStream ps, String delimiter,boolean printLine);
    void println(PrintStream ps, String tagdelimiter, String tokendelimiter,boolean printLine);
    void fixPrintln(PrintStream ps,int ... spaces);
    String[] copy(int idxStart);
    void arraycopy(int sourceIdxStart,String[] destination,int destinationIndexStart,int lengthCopy);
    boolean equalTokens(LineToken lt);

    int compareTo(LineToken right, int ... idxs);
    LineToken mapping(String newName, int ...idxs);
    LineToken mapping(int ...idxs);
    LineToken merge(int from, LineToken lineToken);
    boolean isTagname(String string);
    LineToken addTagname(String name);
}
