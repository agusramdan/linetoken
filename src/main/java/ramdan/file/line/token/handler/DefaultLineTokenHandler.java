package ramdan.file.line.token.handler;

import lombok.Getter;
import lombok.Setter;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.Tokens;

public class DefaultLineTokenHandler implements LineTokenHandler{

    public static final DefaultLineTokenHandler DEFAULT_LINE_TOKEN_HANDLER = new DefaultLineTokenHandler();
    public static final Callback<Tokens> DEFAULT_TOKEN_HANDLER = new DefaultCallback();

    @Getter
    @Setter
    private Callback<Tokens> next=DEFAULT_TOKEN_HANDLER;

    @Override
    public Tokens process(LineToken lineToken) {
        return lineToken;
    }
}
