package ramdan.file.line.token.filter;

import ramdan.file.line.token.LineToken;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by agusramdan on 1/4/19.
 */
public class RegexMatchRule implements LineTokenFilter{
    public static RegexMatchRule[] rule(String ... regex){
        if(regex == null) return new RegexMatchRule[0];
        RegexMatchRule [] rules = new RegexMatchRule[regex.length];
        for (int i = 0; i < regex.length; i++) {
            rules[i] = new RegexMatchRule(regex[i]);
        }
        return rules;
    }
    private Pattern pattern;

    public RegexMatchRule(String regex) {
        pattern = Pattern.compile(regex);
    }

    public boolean isMatchRule(String chek) {
        Matcher m = pattern.matcher(chek);
        return m.matches();
    }

    @Override
    public boolean accept(LineToken lineToken) {
        return isMatchRule(lineToken.get(0));
    }
}
