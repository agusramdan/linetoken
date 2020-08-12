package ramdan.file.line.token.handler;

import ramdan.file.line.token.Line;
import ramdan.file.line.token.LineToken;

public interface LineTokenConverter extends Handler{
    LineToken convert(Line lineToken);
}
