package ramdan.file.line.token.listener;

import ramdan.file.line.token.Line;
import ramdan.file.line.token.handler.LineHandler;

public class LineHandlerListener implements LineListener {
    LineHandler handler;
    LineListener listener;

    public LineHandlerListener(LineHandler handler, LineListener listener) {
        this.handler = handler;
        this.listener = listener;
    }

    @Override
    public void event(Line line) {
        listener.event(handler.process(line));
    }
}
