package ramdan.file.line.token.data;

import ramdan.file.line.token.Line;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.StringUtils;
import java.io.*;
import java.util.Arrays;

/**
 *
 */
public final class LineTokenData extends LineTokenAbstract implements Traceable, Externalizable {
    private static final long serialversionUID = 20191127;
    public final static LineTokenData EMPTY = new LineTokenData();
    public final static LineTokenData REMOVE = new LineTokenData();
    public final static LineTokenData EOF = new LineTokenData();

    public static LineToken parse(Line line){
        if(line==null|| line.isEOF()){
            return LineTokenAbstract.newEOF(line);
        }
        if(line == LineData.REMOVE){
            return REMOVE;
        }
        LineTokenData lt = parse(line.getSource(),null,null,line.getNo(),line.toString());
        lt.setSource(line);
        return lt;
    }
    public static LineTokenData parse(String line){
        return parse((File)null,null,null,null,line);
    }
    public static LineTokenData parse(File file,String tagDelimiter, String tokenDelimiter, Integer lineNo, String line){
        return parse( file,tagDelimiter, tokenDelimiter, lineNo, System.currentTimeMillis(), line);
    }
    public static LineTokenData parse(File file,String tagDelimiter, String tokenDelimiter, Integer lineNo, long timestamp,String line){
        String filename = file!= null ? file.getName():null;
        if(line==null){
            return newInstance(filename,lineNo);
        }
        line = line.trim();
        if(tagDelimiter == null){
            tagDelimiter = StringUtils.getGenevaTagDelimiter(line);
        }
        if(tokenDelimiter==null){
            tokenDelimiter = "|";
        }

        if(tagDelimiter !=null && !tagDelimiter.equals(tokenDelimiter)) {
            switch (tagDelimiter) {
                case "| ":
                    line = line.replaceFirst("\\| ", tokenDelimiter);
                    break;
                case "|" :
                    line= line.replaceFirst("\\|",tokenDelimiter);
                    break;
                default:
                    line = line.replaceFirst(tagDelimiter,tokenDelimiter);

            }
        }
        String parseRule = null;
        if(tokenDelimiter.equals("|")){
            parseRule = "\\|";
        }else{
            parseRule = tokenDelimiter;
        }

        return new LineTokenData(filename,lineNo,tagDelimiter,tokenDelimiter,timestamp,line.split(parseRule));
    }

    public static LineTokenData parse(String parseRule, Integer lineNo, String line){
        return line ==null?
                newInstance(lineNo):
                newInstance(lineNo,line.split(parseRule));
    }
    public static LineTokenData newInstance(String file ,Integer line, String ... tokens){
        return new LineTokenData(file,line,null,null,tokens);
    }
    public static LineTokenData newInstance(Integer line, String ... tokens){
        return new LineTokenData(line,null,null,tokens);
    }
    public static LineTokenData newInstance(String ... tokens){
        return new LineTokenData(null,null,null,tokens);
    }
    public static LineTokenData newInstance(Integer line, Integer end,String ... tokens){
        return new LineTokenData(line,end,null,null,tokens);
    }

    public static LineTokenData newInstance(String fileName,Integer line, Integer end,long timestamp,String ... tokens){
        return new LineTokenData(fileName,line,end,null,null,timestamp,tokens);
    }
    public static LineTokenData newInstance(String fileName,Integer line, Integer end,String ... tokens){
        return new LineTokenData(fileName,line,end,null,null,System.currentTimeMillis(),tokens);
    }
    public static LineTokenData ensureLineTokenData(LineToken lineToken){
        if(lineToken instanceof LineTokenData){
            return (LineTokenData)lineToken;
        }
        return new LineTokenData(
                lineToken.getFileName(),
                lineToken.getStart(),
                lineToken.getEnd(),
                lineToken.getTagDelimiter(),
                lineToken.getTokenDelimiter(),
                lineToken.timestamp(),
                lineToken.copy(0));
    }

    private String[] tokens;
    private transient Line source;

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        tokens = (String[]) in.readObject();
        int idx = 0;
        for (String t : tokens) {
            this.tokens[idx]=token(t);
            idx ++;
        }
        source= (Line) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(tokens);
        out.writeObject(source);
    }

    public LineTokenData(){
        this(null,null,null,null,null,System.currentTimeMillis(),null);
    }
    private LineTokenData(Integer line , String tagDelimiter, String tokenDelimiter, String[]  tokens){
        this(line,line,tagDelimiter,tokenDelimiter,tokens);
    }
    private LineTokenData(Integer start,Integer end , String tagDelimiter, String tokenDelimiter, String[]  tokens){
        this(null,start,end,tagDelimiter,tokenDelimiter,System.currentTimeMillis(),tokens);
    }
    private LineTokenData(String file,Integer line , String tagDelimiter, String tokenDelimiter, long timestamp, String[]  tokens){
        this(file,line,line,tagDelimiter,tokenDelimiter,timestamp,tokens);
    }
    private LineTokenData(String file,Integer line , String tagDelimiter, String tokenDelimiter, String[]  tokens){
        this(file,line,line,tagDelimiter,tokenDelimiter,System.currentTimeMillis(),tokens);
    }

    private LineTokenData(String file,Integer start,Integer end , String tagDelimiter, String tokenDelimiter, long timestamp,String[] tokens){
        super(file,start,end,tagDelimiter,tokenDelimiter,timestamp);

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

    protected LineToken newLineToken(String fileName, Integer start, Integer end, String... tokens) {
        return new LineTokenData(fileName,start,end,getTagDelimiter(),getTokenDelimiter(), System.currentTimeMillis(),tokens);
    }
    protected LineToken newLineToken(String fileName, Integer start, Integer end, long timestamp,  String... tokens) {
        return new LineTokenData(fileName,start,end,getTagDelimiter(),getTokenDelimiter(),timestamp,tokens);
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
    public void clearSource() {
        if(source!=null && !source.isEOF())
            source = null;
    }

    @Override
    public String toString() {
        return source==null ? get(0):source.toString();
    }

    @Override
    public boolean isEOF() {
        return this==EOF || (source!=null && source.isEOF());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(tokens);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineTokenData that = (LineTokenData) o;
        return Arrays.equals(tokens, that.tokens);
    }
}
