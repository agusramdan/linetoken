package ramdan.file.line.token.handler;

import ramdan.file.line.token.Line;

public interface LineHandler {
    Line process(Line lineToken);
}
