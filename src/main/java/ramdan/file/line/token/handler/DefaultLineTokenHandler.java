package ramdan.file.line.token.handler;

import ramdan.file.line.token.LineToken;

public class DefaultLineTokenHandler implements LineTokenHandler{

    public static final DefaultLineTokenHandler DEFAULT_LINE_TOKEN_HANDLER = new DefaultLineTokenHandler();
    @Override
    public boolean supportMultiLine() {
        return false;
    }

    @Override
    public LineToken process(LineToken lineToken) {
        return lineToken;
    }

}
