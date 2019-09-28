package ramdan.file.line.token.filter;


/**
 * Immutable class
 *
 */
public class DefaultMultiLineTokenFilter implements MultiLineTokenFilter {
    private final String name;
    private final RegexMatchRule start;
    private final RegexMatchRule end;
    private final RegexMatchRule[] content;

    public DefaultMultiLineTokenFilter(String name, String start, String end, String ... content) {
        this.name = name;
        this.start = new RegexMatchRule(start);
        this.end = new  RegexMatchRule(end);
        this.content = RegexMatchRule.rule(content);
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
        for(RegexMatchRule t : content){
            if(t.isMatchRule(value)){
                return true;
            }
        }
        return false;
    }
    public int getMatchIndex(String value){
        for(int index = 0 ; index<content.length; index++){
            if(content[index].isMatchRule(value))
                return index;
        }
        return -1;
    }
}
