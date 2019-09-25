package ramdan.file.line.token.handler;

import ramdan.file.line.token.LineToken;

public interface LineTokenHandler {
    boolean supportMultiLine();
    LineToken process(LineToken lineToken);
}
