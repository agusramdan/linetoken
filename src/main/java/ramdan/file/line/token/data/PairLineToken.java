package ramdan.file.line.token.data;

import lombok.Data;
import lombok.val;
import ramdan.file.line.token.LineToken;

import java.util.ArrayList;
import java.util.List;

@Data
public class PairLineToken {
    private String defaultStart;
    private String defaultEnd;
    private LineToken start;
    private LineToken end;
    private List<LineToken> contents=new ArrayList<>();

    public PairLineToken(String defaultStart, String defaultEnd) {
        this.defaultStart = defaultStart;
        this.defaultEnd = defaultEnd;
    }
    public boolean add(LineToken lineToken){
        return contents.add(lineToken);
    }

    public List<LineToken> getPairLineToken(){
        val result = new ArrayList<LineToken>();
        if(start==null){
            result.add(LineTokenData.newInstance(defaultStart));
        }else {
            result.add(start);
        }
        result.addAll(contents);
        if(end==null){
            result.add(LineTokenData.newInstance(defaultEnd));
        }else {
            result.add(end);
        }
        return result;
    }
}
