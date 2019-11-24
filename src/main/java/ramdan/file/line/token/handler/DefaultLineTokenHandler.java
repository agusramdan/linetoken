package ramdan.file.line.token.handler;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.Tokens;

public class DefaultLineTokenHandler implements LineTokenHandler{

    public static final DefaultLineTokenHandler DEFAULT_LINE_TOKEN_HANDLER = new DefaultLineTokenHandler();

    @Override
    public Tokens process(LineToken lineToken) {
        return lineToken;
    }

}
