package ramdan.file.line.token.listener;

import ramdan.file.line.token.Line;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.handler.LineTokenHandler;

public class LineTokenHandlerLineListener implements LineListener {
    private LineTokenHandler handler;

    public LineTokenHandlerLineListener(LineTokenHandler handler) {
        this.handler = handler;
    }

    @Override
    public void event(Line line) {
        handler.process(LineTokenData.parse(line));
    }
}
