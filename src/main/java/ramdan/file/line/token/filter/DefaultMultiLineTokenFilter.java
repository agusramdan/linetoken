package ramdan.file.line.token.filter;


/**
 * Multiline menjadi 1 line
 */
public class DefaultMultiLineTokenFilter implements MultiLineTokenFilter {
    private final String name;
    private final RegexMatchRule start;
    private final RegexMatchRule end;
    private final String[] content;

    public DefaultMultiLineTokenFilter(String name, String start, String end, String ... content) {
        this.name = name;
        this.start = new RegexMatchRule(start);
        this.end = new  RegexMatchRule(end);
        this.content = content;
    }
    public String name(){
        return name;
    }
    public int length(){
        return content.length;
    }
    public boolean isMatchStart(String value){
        return start.isMatchRule(value);
    }
    public boolean isMatchEnd(String value){
        return end.isMatchRule(value);
    }
    public boolean isMatchContent(String value){
        for(String t : content){
            if(t.equals(value)){
                return true;
            }
        }
        return false;
    }
    public int getMatchIndex(String value){
        for(int index = 0 ; index<content.length; index++){
            if(content[index].equals(value))
                return index;
        }
        return -1;
    }
}
