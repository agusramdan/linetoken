package ramdan.file.line.token.data;

import ramdan.file.line.token.Line;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.StringUtils;

import java.io.File;
import java.io.PrintStream;
import java.lang.ref.WeakReference;

/**
 * immutable class
 */
public class LineData implements Line {

    private File source;
    private int no;
    private String line;
    private WeakReference<LineToken> lineToken;

    public LineData(File source, int no, String line) {
        this.source = source;
        this.no = no;
        this.line = line;
    }
    public LineData(int no,String line) {
        this(null,no,line);
    }
    public LineData(String line) {
        this(null,0,line);
    }

    public File getSource() {
        return source;
    }

    @Override
    public int getNo() {
        return no;
    }

    public String getLine() {
        return line;
    }
    @Override
    public int length() {
        return line==null?0:line.length();
    }

    @Override
    public boolean isEmpty() {
        return line==null || line.isEmpty();
    }

    @Override
    public boolean isEOF() {
        return line==null;
    }

    @Override
    public boolean contain(String... parameter) {
        return StringUtils.contain(line,parameter);
    }

    @Override
    public boolean containAll(String... parameter) {
        return false;
    }

    @Override
    public boolean containIgnoreCase(String... parameter) {
        return false;
    }

    @Override
    public boolean containAllIgnoreCase(String... parameter) {
        return false;
    }

    @Override
    public void println(PrintStream ps) {
        ps.println(line);
    }

    @Override
    public String toString() {
        return line!=null?line:"";
    }
}
