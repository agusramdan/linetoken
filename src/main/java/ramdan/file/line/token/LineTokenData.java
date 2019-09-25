package ramdan.file.line.token;

import ramdan.file.line.token.handler.ErrorHandlers;
import ramdan.file.line.token.handler.IntegerConversionErrorHandler;

import java.io.PrintStream;
import java.util.Arrays;

/**
 * immutable class
 */
public class LineTokenData implements LineToken {

    public final static LineTokenData EMPTY = new LineTokenData(null);

    static boolean stringTrim = true;
    static boolean stringNullToEmpty=true;

    static boolean equal(String p1,String p2){
        if(p1== null){
            return p2 == null;
        }
        return p2 != null && p1.equals(p2);
    }

    static boolean equalIgnoreCase(String p1,String p2){
        if(p1== null){
            return p2 == null;
        }
        return p2 != null && p1.equalsIgnoreCase(p2);
    }

    public static LineTokenData parse(String line){
        return parse("\\|",null,line);
    }

    public static LineTokenData parse(Integer lineNo, String line){
        return parse("\\|",lineNo,line);
    }

    public static LineTokenData parse(String parseRule, Integer lineNo, String line){
        return line ==null? newInstance(lineNo):newInstance(lineNo,line.split(parseRule));
    }

    public static LineTokenData newInstance(Integer line, String ... tokens){
        return new LineTokenData(line,tokens);
    }
    public static LineTokenData newInstance(Integer line, Integer end,String ... tokens){
        return new LineTokenData(line,end,tokens);
    }
    public static LineTokenData newInstance(String ... tokens){
        return new LineTokenData(null,tokens);
    }

    public static void args(String ... args){
        for (String arg : args) {
            if( "-empty".equals(arg)){
                stringNullToEmpty = true;
            }else if("-null".equals(arg)){
                stringNullToEmpty=false;
            }else if( "-trim".equals(arg)){
                stringTrim = true;
            }else if("-notrim".equals(arg)){
                stringTrim = false;
            }
        }
    }

    private Integer start;
    private Integer end;
    private String[] tokens;

    private String token(String t){
        if (t == null){
            if(stringNullToEmpty){
                t = "";
            }
        }else{
            if(stringTrim){
                t = t.trim();
            }
            t=StringSave.save(t);
        }
        return t;
    }
    LineTokenData(Integer line , String ... tokens){
        this(line,line,tokens);
    }
    LineTokenData(Integer start,Integer end , String ... tokens){
        this.start=start;
        this.end=end;
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

    public Integer getStart() {
        return start;
    }

    public Integer getEnd() {
        return end;
    }

    public int length(){
        return tokens.length;
    }

    /**
     * Return empty string
     * @param index
     * @return
     */
    public String get(int index){
        return index < tokens.length?tokens[index]:
                stringNullToEmpty?"":null;
    }

    public int getInt(int index){
        return getInt(index, ErrorHandlers.INTEGER_CONVERSION_ERROR_HANDLER);
    }

    public int getInt(int index, IntegerConversionErrorHandler handler){
        String value = get(index);
        try {
            return Integer.valueOf(value);
        }catch(Exception e){
            return handler.handle(value);
        }
    }

    public boolean isEmpty(int index){
        String chek = get(index);
        return chek==null || "".equals(chek);
    }

    /**
     * equal one of parameter
     *
     * @param index
     * @param parameter
     * @return
     */
    public boolean equal(int index, String ... parameter){
        String chek = get(index);
        for (String p: parameter) {
            if(equal(chek,p))return true;
        }
        return false;
    }

    public boolean equalIgnoreCase(int index, String ... parameter){
        String chek = get(index);
        for (String p: parameter) {
            if(equalIgnoreCase(chek,p))return true;
        }
        return false;
    }

    public boolean contain(int index, String ... parameter){
        String chek = get(index);
        if(chek == null) return false;
        for (String p: parameter) {
            if(chek.contains(p))return true;
        }
        return false;
    }

    public boolean containAll(int index, String ... parameter){
        String chek = get(index);
        if(chek == null) return false;
        for (String p: parameter) {
            if(!chek.contains(p))return false;
        }
        return true;
    }

    public boolean containIgnoreCase(int index, String ... parameter){
        String chek = get(index);
        if(chek == null) return false;
        for (String p: parameter) {
            if(chek.contains(p))return true;
        }
        return false;
    }

    public boolean containAllIgnoreCase(int index, String ... parameter){
        String chek = get(index);
        if(chek == null) return false;
        chek = chek.toUpperCase();
        for (String p: parameter) {
            if(!chek.contains(p.toUpperCase()))return false;
        }
        return true;
    }

    public LineToken replaceToken(int index, String token){
        String[] tokens = new String[this.tokens.length];
        tokens[index] = token(token);
        return new LineTokenData(start,end,tokens);
    }

    public void println(PrintStream ps){
        println(ps,"|",true);
    }

    public void println(PrintStream ps, String delimiter,boolean printLine){
        int size = length();
        if(size > 0){
            if(delimiter == null || "".equals(delimiter)){
                delimiter = "|";
            }

            if(printLine) {
                if(start!= null){
                    if( end==null|| start.equals(end))
                        ps.printf("%06d:", start);
                    else
                        ps.printf("%06d->%06d:", start,end);
                }
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
        int size = length();
        if(size > 0){
            for (int i = 0; i < spaces.length; i++) {
                if(spaces[i]!=0) {
                    String data = size> i && tokens[i]!=null?tokens[i]:"";
                    int abs = Math.abs(spaces[i]);
                    if(data.length()>abs){
                        ps.print(data.substring(0,abs));
                    }else {
                        ps.printf("%" + (-1 * spaces[i]) + "s", size > i && tokens[i] != null ? tokens[i] : "");
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
}
