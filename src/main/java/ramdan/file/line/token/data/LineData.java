package ramdan.file.line.token.data;

import lombok.Getter;
import lombok.val;
import ramdan.file.line.token.Line;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.StringUtils;

import java.io.File;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;

/**
 * immutable class
 */
public class LineData implements Line {
    public final static long check_timestamp = Line.default_time;
    @Getter
    private final File source;
    @Getter
    private final int no;
    @Getter
    private final String line;
    @Getter private final long timestamp;
    public LineData(File source, int no, String line,long timestamp) {
        this.source = source;
        this.no = no;
        this.line = line;
        this.timestamp=timestamp;
    }
    public LineData(File source, int no, String line) {
        this(source, no,line,System.currentTimeMillis());
    }
    public LineData(int no,String line) {
        this(null,no,line);
    }
    public LineData(String line) {
        this(null,0,line);
    }

    @Override
    public int length() {
        return line==null?0:line.length();
    }

    @Override
    public long timestamp() {
        return timestamp;
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
    public boolean equal(String... parameter) {
        return StringUtils.equal(line,parameter) ;
    }
    @Override
    public boolean equalIgnoreCase(String... parameter) {
        return StringUtils.equalIgnoreCase(line,parameter) ;
    }
    @Override
    public boolean containAll(String... parameter) {
        return StringUtils.containAll(line,parameter);
    }

    @Override
    public boolean containIgnoreCase(String... parameter) {
        return StringUtils.containIgnoreCase(line,parameter);
    }

    @Override
    public boolean containAllIgnoreCase(String... parameter) {
        return StringUtils.containAllIgnoreCase(line,parameter);
    }

    @Override
    public void println(PrintStream ps) {
        if(isEOF()){
            return;
        }
        ps.println(line);
    }

    @Override
    public Line trim() {
        if(length()>0){
            val c = line.trim();
            if(c.equals(line)){
                return this;
            }
            return new LineData(source,no,c,timestamp);
        }
        return this;
    }

    @Override
    public String toString() {
        return line!=null?line:"";
    }
}
