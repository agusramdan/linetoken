package ramdan.file.line.token.handler;

import ramdan.file.line.token.Line;
import ramdan.file.line.token.LineToken;

public interface LineHandler {
    Line process(Line lineToken);
}
