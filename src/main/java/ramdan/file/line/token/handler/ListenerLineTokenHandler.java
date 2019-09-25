package ramdan.file.line.token.handler;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.listener.LineTokenListener;

import java.util.List;

public class ListenerLineTokenHandler implements LineTokenHandler {
    private final LineTokenListener handler;

    public ListenerLineTokenHandler(LineTokenListener  handler) {
        this.handler =handler;
    }

    @Override
    public boolean supportMultiLine() {
        return false;
    }

    @Override
    public LineToken process(LineToken lineToken) {
        handler.event(lineToken);
        return lineToken;
    }
}
