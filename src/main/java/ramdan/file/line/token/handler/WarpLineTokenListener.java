package ramdan.file.line.token.handler;

import ramdan.file.line.token.Line;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.listener.LineListener;
import ramdan.file.line.token.listener.LineTokenListener;

public class WarpLineTokenListener implements LineTokenListener {

    private final LineTokenHandler handler;

    public WarpLineTokenListener(LineTokenHandler handler) {
        this.handler = handler;
    }

    @Override
    public void event(LineToken lineToken) {
        handler.process(lineToken);
    }
}
