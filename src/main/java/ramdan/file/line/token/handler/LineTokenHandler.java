package ramdan.file.line.token.handler;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.Tokens;

public interface LineTokenHandler {
    //boolean supportMultiLine();
    Tokens process(LineToken lineToken);
}
