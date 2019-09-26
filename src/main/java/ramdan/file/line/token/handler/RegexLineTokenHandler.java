package ramdan.file.line.token.handler;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.filter.RegexMatchRule;

public class RegexLineTokenHandler extends DefaultLineTokenHandler {

    private final RegexMatchRule rule;
    private final LineTokenHandler handler;

    public RegexLineTokenHandler(RegexMatchRule rule , LineTokenHandler handler) {
        this.rule = rule;
        this.handler = (handler!=null?handler:DefaultLineTokenHandler.DEFAULT_LINE_TOKEN_HANDLER);
    }
    public RegexLineTokenHandler(RegexMatchRule regex){
        this(regex,null);
    }
    public RegexLineTokenHandler(String regex){
        this(new RegexMatchRule(regex),null);
    }
    public RegexLineTokenHandler(String regex, LineTokenHandler handler){
        this(new RegexMatchRule(regex),null);
    }
    @Override
    public LineToken process(LineToken lineToken) {
        return rule.isMatchRule(lineToken.get(0))
                ? handler.process(lineToken)
                : LineTokenData.EMPTY;
    }


}
