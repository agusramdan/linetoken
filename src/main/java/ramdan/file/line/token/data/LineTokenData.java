package ramdan.file.line.token.data;

import ramdan.file.line.token.Line;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.StringSave;
import ramdan.file.line.token.StringUtils;
import ramdan.file.line.token.handler.ErrorHandlers;
import ramdan.file.line.token.handler.IntegerConversionErrorHandler;

import java.io.File;
import java.io.PrintStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Arrays;

/**
 * immutable class
 * except source
 */
public class LineTokenData extends LineTokenAbstract implements Traceable {

    public final static LineTokenData EMPTY = new LineTokenData(null);
    public final static LineTokenData EOF = new LineTokenData(null);

    public static LineToken parse(String parseRule , Line line){
        if(line==null|| line.isEOF()){
            return LineTokenAbstract.newEOF(line);
        }
        LineTokenData lt = parse(line.getSource(),parseRule,line.getNo(),line.toString());
        lt.setSource(line);
        return lt;
    }

    public static LineToken parse(Line line){
        return parse("\\|",line);
    }
    public static LineTokenData parse(String line){
        return parse("\\|",null,line);
    }
    public static LineTokenData parse(Integer lineNo, String line){
        return parse("\\|",lineNo,line);
    }
    public static LineTokenData parse(File file, Integer lineNo, String line){
        return parse(file,"\\|",lineNo,line);
    }

    public static LineTokenData parse(File file,String parseRule, Integer lineNo, String line){
        String filename = file!= null ? file.getName():null;
        return line ==null?
                newInstance(filename,lineNo):
                newInstance(filename,lineNo,line.split(parseRule));
    }
    public static LineTokenData parse(String parseRule, Integer lineNo, String line){
        return line ==null?
                newInstance(lineNo):
                newInstance(lineNo,line.split(parseRule));
    }
    public static LineTokenData newInstance(String file ,Integer line, String ... tokens){
        return new LineTokenData(file,line,tokens);
    }
    public static LineTokenData newInstance(Integer line, String ... tokens){
        return new LineTokenData(line,tokens);
    }
    public static LineTokenData newInstance(Integer line, Integer end,String ... tokens){
        return new LineTokenData(line,end,tokens);
    }
    public static LineTokenData newInstance(String fileName,Integer line, Integer end,String ... tokens){
        return new LineTokenData(fileName,line,end,tokens);
    }
    public static LineTokenData newInstance(String ... tokens){
        return new LineTokenData(null,tokens);
    }

    //private final String file;
    //private final Integer start;
    //private final Integer end;
    private final String[] tokens;
    private Line source;


    LineTokenData(Integer line , String ... tokens){
        this(line,line,tokens);
    }
    LineTokenData(Integer start,Integer end , String ... tokens){
        this(null,start,end,tokens);
    }
    LineTokenData(String file,Integer line , String ... tokens){
        this(file,line,line,tokens);
    }

    LineTokenData(String file,Integer start,Integer end , String ... tokens){
        super(file,start,end);
        //this.start=start;
        //this.end=end;
        if(tokens==null){
            this.tokens=new String[0];
            return;
        }
        this.tokens = new String[tokens.length];
        int idx = 0;
        for (String t : tokens) {
            this.tokens[idx]=token(t);
            idx ++;
        }
    }

    public int length(){
        return tokens.length;
    }

    public Line getSource(){
        return source;
    }
    public void setSource(Line line){
        if(source==null){
            source = line;
        }
    }
    /**
     * Return empty string
     * @param index
     * @return
     */
    public String get(int index){
        return index >= 0 && index < tokens.length?tokens[index]:
                stringNullToEmpty?"":null;
    }



    @Override
    protected LineToken newLineToken(String fileName, Integer start, Integer end, String... tokens) {
        return new LineTokenData(fileName,start,end,tokens);
    }

    public void println(PrintStream ps, String delimiter,boolean printLine){
        if(isEOF()) return;
        int size = length();
        if(size > 0){
            if(delimiter == null || "".equals(delimiter)){
                delimiter = "|";
            }
            if(printLine) {
                printLine(ps);
            }
            for (int i = 0; i < size; i++) {
                if(!"".equals(tokens[i])){
                    ps.print(tokens[i]);
                }else{
                    ps.print(" ");
                }
                ps.print(delimiter);
            }
            ps.println();
        }
    }

    public void fixPrintln(PrintStream ps,int ... spaces){
        if(isEOF()) return;
        int size = length();
        if(size > 0){
            for (int i = 0; i < spaces.length; i++) {
                if(spaces[i]!=0) {
                    String data = size> i && tokens[i]!=null?tokens[i]:"";
                    int abs = Math.abs(spaces[i]);
                    if(data.length()>abs){
                        ps.print(data.substring(0,abs));
                    }else {
                        ps.printf("%" + (-1 * spaces[i]) + "s", data);
                    }
                }
            }
            ps.println();
        }
    }

    public String[] copy(int idxStart){
        String[] result = new String[length()-idxStart];
        System.arraycopy(tokens,idxStart,result, 0, result.length);
        return result;
    }
    public void arraycopy(int idxStart,String[] result,int idxResultStart,int length){
        length = Math.min(tokens.length-idxStart,length);
        length = Math.min(result.length-idxResultStart,length);
        System.arraycopy(tokens,idxStart,result, idxResultStart, length);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineTokenData that = (LineTokenData) o;
        return Arrays.equals(tokens, that.tokens);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(tokens);
    }

    @Override
    public String toString() {
        return source==null ? get(0):source.toString();
    }

    @Override
    public void clearSource() {
        if(source!=null && !source.isEOF())
            source = null;
    }

    @Override
    public boolean isEOF() {
        return this==EOF || (source!=null && source.isEOF());
    }
}
