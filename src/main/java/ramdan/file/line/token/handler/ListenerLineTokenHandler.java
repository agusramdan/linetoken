package ramdan.file.line.token.handler;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.Tokens;
import ramdan.file.line.token.listener.LineTokenListener;

import java.util.List;

public class ListenerLineTokenHandler implements LineTokenHandler {
    private final LineTokenListener handler;

    public ListenerLineTokenHandler(LineTokenListener  handler) {
        this.handler =handler;
    }

    @Override
    public Tokens process(LineToken lineToken) {
        handler.event(lineToken);
        return lineToken;
    }
}
