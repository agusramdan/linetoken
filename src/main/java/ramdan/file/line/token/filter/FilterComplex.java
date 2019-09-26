package ramdan.file.line.token.filter;

import ramdan.file.line.token.data.LineTokenData;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FilterComplex {

    public static final boolean REMOVE = true;
    public static FilterComplex read(File file) throws IOException {
        BuilderLineToken blt = null ;
        try(InputStreamReader isr = new FileReader(file);
            BufferedReader br = new BufferedReader(isr)){
            String line;
            while ((line = br.readLine())!= null){
                if(line.startsWith("#")) continue;
                blt = new BuilderLineToken(LineTokenData.parse(line));
                readChild(blt,br);
            }
        }
        return blt.build();
    }

    /**
     * Sibling of parent
     * @param parent
     * @param br
     * @return Sibling of parent
     */
    public static BuilderLineToken readChild(BuilderLineToken parent,BufferedReader br) throws IOException {
        String line;
        BuilderLineToken blt = null;
        int parentLevel= parent.token.getInt(0);
        BuilderLineToken sibling = parent.childsBuilder.isEmpty()?null:parent.childsBuilder.get(parent.childsBuilder.size()-1);
        while ((line = br.readLine())!= null){
            if(line.startsWith("#")) continue;
            blt = new BuilderLineToken(LineTokenData.parse(line));
            if(blt.token.getInt(0)<=parentLevel){
                return blt;
            }
            if(sibling ==null || sibling.token.getInt(0)==blt.token.getInt(0)){
                sibling = blt;
                parent.childsBuilder.add(blt);
                blt = null;
                continue;
            }else {
                sibling.childsBuilder.add(blt);
                blt= readChild(sibling,br);
            }
            // if child
            if(blt==null)return null;
            if(sibling.token.getInt(0)==blt.token.getInt(0)){
                sibling = blt;
                parent.childsBuilder.add(blt);
                blt = null;
                continue;
            }
            return blt;
        }
        return blt;
    }

    public FilterComplex(String start, String end,  boolean remove) {
        this(null,new RegexMatchRule(start),new RegexMatchRule(end),remove,new RegexMatchRule[0]);
    }

    public FilterComplex(String start, String end,  FilterComplex... childs) {
        this(null,new RegexMatchRule(start),new RegexMatchRule(end),false,new RegexMatchRule[0]);
    }
    public FilterComplex(String start, String end, boolean remove, String tags, FilterComplex... childs) {
        this(null,new RegexMatchRule(start),new RegexMatchRule(end),remove,RegexMatchRule.rule(tags),childs);
    }
    public FilterComplex(String start, String end, String tags, FilterComplex... childs) {
        this(null,new RegexMatchRule(start),new RegexMatchRule(end),false,RegexMatchRule.rule(tags),childs);
    }
    public FilterComplex(String start, String end, RegexMatchRule ... tags) {
        this(null,new RegexMatchRule(start),new RegexMatchRule(end),false,tags);
    }
    public FilterComplex(String start, String end, RegexMatchRule [] tags, FilterComplex... childs) {
        this(null,new RegexMatchRule(start),new RegexMatchRule(end),false,tags,childs);
    }
    public FilterComplex(RegexMatchRule start, RegexMatchRule end, RegexMatchRule ... tags) {
        this(null,start,end,false,tags);
    }
    public FilterComplex(FilterComplex parent, String start, String end, String ... tags) {
        this(parent,new RegexMatchRule(start),new RegexMatchRule(end),false,RegexMatchRule.rule(tags));
    }
    public FilterComplex(FilterComplex parent, String start, String end, RegexMatchRule ... tags) {
        this(parent,new RegexMatchRule(start),new RegexMatchRule(end),false,tags);
    }
    public FilterComplex(FilterComplex parent, RegexMatchRule start, RegexMatchRule end, RegexMatchRule ... tags) {
        this(parent,start,end,false,tags);
    }
    public FilterComplex(RegexMatchRule start, RegexMatchRule end, FilterComplex... childs) {
        this(null,start,end,false,null,childs);
    }
    public FilterComplex(FilterComplex parent, RegexMatchRule start, RegexMatchRule end, FilterComplex... childs) {
        this(parent, start, end,false,null,childs);
    }

    public FilterComplex(FilterComplex parent, RegexMatchRule start, RegexMatchRule end, boolean remove, RegexMatchRule[] tags, FilterComplex... childs) {
        this.parent = parent;
        this.start = start;
        this.end = end;
        this.remove = remove;
        this.tags = tags!=null && !remove ? tags:new RegexMatchRule[0];
        this.childs = childs != null && !remove? childs:new FilterComplex[0];
        for (int i = 0; i < this.childs.length ; i++) {
            this.childs[i].parent = this;
        }
    }

    private FilterComplex parent;
    final RegexMatchRule start;
    final RegexMatchRule end;
    final RegexMatchRule [] tags ;
    final FilterComplex[] childs ;
    final boolean remove;
    public boolean isRoot() {
        return parent == null;
    }

    public boolean isRemove() {
        return remove;
    }

    public FilterComplex getParent() {
        return parent;
    }
    public boolean isMatchStart(String value){
        return start.isMatchRule(value);
    }
    public boolean isMatchEnd(String value){
        return end.isMatchRule(value);
    }
    public boolean isMatchContent(String value){
        for(RegexMatchRule t : tags){
            if(t.isMatchRule(value)){
                return true;
            }
        }
        return false;
    }
    public FilterComplex findMatchChild(String value){
        for(FilterComplex t : childs){
            if(t.isMatchStart(value)){
                return t;
            }
        }
        return null;
    }

    /**
     * builder form line token
     */
    static class BuilderLineToken{
        LineTokenData token;
        List<BuilderLineToken> childsBuilder = new ArrayList<>();

        public BuilderLineToken(LineTokenData token) {
            this.token = token;
        }

        FilterComplex build(){
            List<FilterComplex> list = new ArrayList<>();
            for (BuilderLineToken blt: childsBuilder) {
                list.add(blt.build());
            }
            boolean remove = token.length() <= 3 && list.isEmpty();
            return remove ? new FilterComplex(token.get(1),token.get(2),REMOVE)
                    : new FilterComplex(token.get(1),token.get(2),
                     RegexMatchRule.rule(token.copy(3)),
                    list.toArray(new FilterComplex[list.size()]));

        }

    }
}
