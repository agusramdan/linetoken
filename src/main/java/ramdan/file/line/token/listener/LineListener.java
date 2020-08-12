package ramdan.file.line.token.listener;

import ramdan.file.line.token.Line;
import ramdan.file.line.token.LineToken;

public interface LineListener {
    void event(Line line);
}
