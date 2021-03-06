package ramdan.file.line.token.handler;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.Tokens;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.data.MultiLineData;
import ramdan.file.line.token.filter.RegexMatchRule;

import java.util.ArrayList;
import java.util.List;

/**
 * ramdan.file.line.token.handler.DuplicateFilterLineTokenHandler
 */
public class DuplicateFilterLineTokenHandler extends DefaultLineTokenHandler {

    private RegexMatchRule first;
    private RegexMatchRule last;
    private RegexMatchRule reset;
    private List<Tokens> capture = new ArrayList<>();
    public DuplicateFilterLineTokenHandler(RegexMatchRule first,RegexMatchRule last,RegexMatchRule reset) {
        this.first = first;
        this.last = last;
        this.reset = reset;
    }

    public DuplicateFilterLineTokenHandler(String first,String last,String resetRegex){
        this(new RegexMatchRule(first),new RegexMatchRule(last),new RegexMatchRule(resetRegex));
    }
    @Override
    public Tokens process(LineToken lineToken) {
        if(reset.accept(lineToken)){
            Tokens result;
            if(capture.isEmpty()){
                result = lineToken;
            }else {
                capture.add(lineToken);
                result = MultiLineData.tokens(capture);
                capture = new ArrayList<>();
            }
            return result;
        }
        if(first.accept(lineToken)){
            for (Tokens t : capture) {
                if(t.equals(lineToken)){
                    return  LineTokenData.EMPTY;
                }
            }
            capture.add(lineToken);
            return  LineTokenData.EMPTY;
        }
        if(last.accept(lineToken)){
            for (int i = 0; i < capture.size(); i++) {
                if(lineToken.equals(capture.get(i))){
                    capture.remove(i);
                    break;
                }
            }
            capture.add(lineToken);
            return  LineTokenData.EMPTY;
        }
        return lineToken;
    }
}
