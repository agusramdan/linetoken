package ramdan.file.line.token.handler;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.Tokens;

public interface LineTokenHandler extends Handler{
    Tokens process(LineToken lineToken);
}
