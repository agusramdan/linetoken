package ramdan.file.line.token.listener;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.LineTokenData;
import ramdan.file.line.token.filter.RegexMatchRule;
import ramdan.file.line.token.handler.DefaultLineTokenHandler;
import ramdan.file.line.token.handler.LineTokenHandler;

public class RegexLineTokenListener implements LineTokenListener {

    private final RegexMatchRule rule;
    private final LineTokenListener listener;

    public RegexLineTokenListener(RegexMatchRule rule , LineTokenListener listener) {
        this.rule = rule;
        this.listener = (listener!=null?listener:DefaultLineTokenListener.DEFAULT_LINE_TOKEN_LISTENER);
    }

    public RegexLineTokenListener(String regex){
        this(new RegexMatchRule(regex),null);
    }
    public RegexLineTokenListener(String regex, LineTokenHandler handler){
        this(new RegexMatchRule(regex),null);
    }

    public boolean isMatchRule(String chek) {
        return rule.isMatchRule(chek);
    }

    public boolean accept(LineToken lineToken) {
        return rule.accept(lineToken);
    }

    @Override
    public void event(LineToken lineToken) {
        if(rule.isMatchRule(lineToken.get(0))){
            listener.event(lineToken);
        }
    }

}
