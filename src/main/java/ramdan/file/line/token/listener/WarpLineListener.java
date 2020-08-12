package ramdan.file.line.token.listener;

import ramdan.file.line.token.Line;
import ramdan.file.line.token.handler.LineHandler;

public class WarpLineListener implements LineListener {

    private final LineHandler handler;

    public WarpLineListener(LineHandler handler) {
        this.handler = handler;
    }

    @Override
    public void event(Line line) {
        handler.process(line);
    }
}
