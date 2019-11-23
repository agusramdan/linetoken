package ramdan.file.line.token.data;

import lombok.Getter;
import lombok.val;
import lombok.var;
import ramdan.file.line.token.Line;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.StringSave;
import ramdan.file.line.token.StringUtils;
import ramdan.file.line.token.handler.DoubleConversionErrorHandler;
import ramdan.file.line.token.handler.ErrorHandlers;
import ramdan.file.line.token.handler.IntegerConversionErrorHandler;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * immutable class
 * except source
 */
public abstract class LineTokenAbstract implements LineToken {
    static boolean stringTrim = true;
    static boolean stringNullToEmpty=true;
    public static String get(LineToken  token , int idx){
        if(token == null){
            return stringNullToEmpty?"":null;
        }
        return token.get(idx);
    }
    public static String tokenCheck(String t){
        if (t == null){
            if(stringNullToEmpty){
                t = "";
            }
        }
        return t;
    }
    public static String token(String t){
        if (t == null){
            if(stringNullToEmpty){
                t = "";
            }
        }else{
            if(stringTrim){
                t = t.trim();
            }
            t= StringSave.save(t);
        }
        return t;
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
    public static LineToken newEOF(String file, Integer start){
        return new LineTokenEOF(file,start,start);
    }

    public static LineToken newEOF(Line line){
        if(line == null) return newEOF(null,null);
        String fileName = null;
        File file = line.getSource();
        if(file!= null){
            fileName = file.getName();
        }
        Integer start = line.getNo();
        return newEOF(fileName,start);
    }
    private final String file;
    @Getter
    private final Integer start;
    @Getter
    private final Integer end;
    protected final String tagDelimiter;
    protected final String tokenDelimiter;

    public LineTokenAbstract() {
        this(null, null,null,null,null);
    }

    public LineTokenAbstract(String file, Integer start, Integer end) {
        this(file, start,end,null,null);
    }
    public LineTokenAbstract(String file, Integer start, Integer end,String tagdelimiter, String tokendelimiter) {
        this.file = file;
        this.start = start;
        this.end = end;
        if(tagdelimiter==null){
            tagdelimiter = "| ";
        }
        this.tagDelimiter =tagdelimiter;
        if(tokendelimiter == null){
            tokendelimiter = "|";
        }
        this.tokenDelimiter= tokendelimiter;
    }
    public LineTokenAbstract(LineToken lineToken) {
        this(lineToken.getFileName(),lineToken.getStart(),lineToken.getEnd());
    }
    public String getFileName() {
        return file;
    }



    public String getTagname(){
        return get(0);
    }
    public String getValue(){
        return get(1);
    }

    protected String getSave(int i){
        val r = get(i);
        if(r==null) return "";
        return r;
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
    public double getDouble(int index){
        return getDouble(index, ErrorHandlers.DOUBLE_CONVERSION_ERROR_HANDLER);
    }

    public double getDouble(int index, DoubleConversionErrorHandler handler){
        String value = get(index);
        try {
            return Double.valueOf(value);
        }catch(Exception e){
            return handler.handle(value);
        }
    }
    public boolean notEmpty(int index){
        String chek = get(index);
        return StringUtils.notEmpty(chek);
    }
    public boolean isEmpty(int index){
        String chek = get(index);
        return StringUtils.isEmpty(chek);
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
        return StringUtils.equal(chek,parameter);
    }

    public boolean equalIgnoreCase(int index, String ... parameter){
        String chek = get(index);
        return StringUtils.equalIgnoreCase(chek,parameter);
    }

    public boolean contain(int index, String ... parameter){
        String chek = get(index);
        return StringUtils.contain(chek,parameter);
    }

    public boolean containAll(int index, String ... parameter){
        String chek = get(index);
        return StringUtils.containAll(chek,parameter);
    }

    public boolean containIgnoreCase(int index, String ... parameter){
        String chek = get(index);
        return StringUtils.containIgnoreCase(chek,parameter);
    }

    public boolean containAllIgnoreCase(int index, String ... parameter){
        String chek = get(index);
        return StringUtils.containAllIgnoreCase(chek,parameter);
    }

    public void println(PrintStream ps){
        println(ps,null,null,true);
    }

    public void printLine(PrintStream ps){
        String file = getFileName();
        if(file!=null){
            ps.printf("%s:", file);
        }
        Integer start = getStart();
        Integer end = getEnd();
        if(start!= null){
            if( end==null|| start.equals(end))
                ps.printf("%06d:", start);
            else
                ps.printf("%06d->%06d:", start,end);
        }else if(end !=null){
            ps.printf("%06d:", end);
        }
    }
    public void println(PrintStream ps, String tagdelimiter, String tokendelimiter,boolean printLine){
        if(isEOF()) return;
        int size = length();
        if(size > 0){
            if(tagdelimiter == null || "".equals(tagdelimiter)){
                tagdelimiter = this.tagDelimiter;
            }
            if(tokendelimiter == null || "".equals(tokendelimiter)){
                tokendelimiter = this.tokenDelimiter;
            }
            if(printLine) {
                printLine(ps);
            }
            ps.print(getTagname());
            ps.print(tagdelimiter);
            for (int i = 1; i < size; i++) {
                String value = get(i);
                if(!"".equals(value)){
                    ps.print(value);
                }else{
                    ps.print(" ");
                }
                ps.print(tokendelimiter);

            }
            ps.println();
        }
    }
    public void println(PrintStream ps, String delimiter,boolean printLine){
         println(ps,delimiter,delimiter,printLine);
    }
    public void fixPrintln(PrintStream ps,int ... spaces){
        int size = length();
        if(size > 0){
            for (int i = 0; i < spaces.length; i++) {
                if(spaces[i]!=0) {
                    String value = get(i);
                    String data = size> i && value!=null?value:"";
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
    @Override
    public LineToken replaceToken(int index, String token){
        String[] tokens;
        if(index< this.length()){
            tokens=copy(0);
        }else {
            tokens=new String[index+1];
            this.arraycopy(0,tokens,0,this.length());
        }
        if(index>=0 && index<= tokens.length ){
            tokens[index] = token(token);
        }
        return newLineToken(getFileName(),getStart(),getEnd(),tokens);
    }

    public LineToken toLineToken(String token){
        if(length()<=1){
            return newLineToken(getFileName(),getStart(),getEnd(),token);
        }
        return replaceToken(0,token);
    }
    public LineToken copyLineToken(){
        return newLineToken(getFileName(),getStart(),getEnd(),copy(0));
    }

    protected abstract LineToken newLineToken(String fileName, Integer start, Integer end, String ...tokens);

    @Override
    public String[] copy(int idxStart) {
        int length = length();
        if(idxStart>= length) return new String[0];
        length-=idxStart;
        String[] result = new String[length];
        for (int i = 0; i < length; i++) {
            result[i]=get(i+idxStart);
        }
        return result;
    }

//    @Override
//    public void arraycopy(int sourceIdxStart, String[] destination, int destinationIndexStart, int lengthCopy) {
//        throw new RuntimeException("not support function replace arraycopy");
//    }

    public boolean isEOF(){
        return false;
    }


//    public String[] copy(int idxStart){
//        String[] result = new String[length()-idxStart];
//        System.arraycopy(tokens,idxStart,result, 0, result.length);
//        return result;
//    }
//    public void arraycopy(int idxStart,String[] result,int idxResultStart,int length){
//        length = Math.min(tokens.length-idxStart,length);
//        length = Math.min(result.length-idxResultStart,length);
//        System.arraycopy(tokens,idxStart,result, idxResultStart, length);
//    }
    @Override
    public void arraycopy(int sourceIdxStart, String[] destination, int destinationIndexStart, int lengthCopy) {
        for (int i = 0; i < lengthCopy; i++) {
            destination[i+destinationIndexStart]=get(i+sourceIdxStart);
        }
    }

    public boolean equalTokens(LineToken o) {
        if (this == o) return true;
        if (o == null) return false;
        int length = Math.max(this.length(),o.length());
        for (int i = 0; i < length; i++) {
            if(!this.equal(i,o.get(i))){
                return false;
            }
        }
        return true;
    }

    public int compareTo(LineToken t,int ... idxs) {
        var com= 0;
        if(idxs==null || idxs.length==0){
            val l = Math.max(length(),t.length());
            for (int i = 0; com == 0 && i < l; i++) {
                com = getSave(i).compareTo(t.get(i));
            }
        }else {
            for (int i = 0; com == 0 && i < idxs.length; i++) {
                com = getSave(idxs[i]).compareTo(t.get(idxs[i]));
            }
        }
        return com;
    }

    @Override
    public int compareTo(LineToken lineToken) {
        return compareTo(lineToken,(int[]) null);
    }

    public static class LineTokenEOF extends LineTokenAbstract{
        public LineTokenEOF(String file, Integer start, Integer end) {
            super(file, start, end);
        }

        @Override
        protected LineToken newLineToken(String fileName, Integer start, Integer end, String... tokens) {
            return null;
        }

        @Override
        public int length() {
            return 0;
        }

        @Override
        public Line getSource() {
            return null;
        }

        @Override
        public String get(int index) {
            return "";
        }

        public boolean isEOF(){
            return true;
        }
    }
}
