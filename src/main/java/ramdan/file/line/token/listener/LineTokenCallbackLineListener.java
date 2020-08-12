package ramdan.file.line.token.listener;

import ramdan.file.line.token.Line;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.callback.Callback;
import ramdan.file.line.token.data.LineTokenData;

public class LineTokenCallbackLineListener implements LineListener {
    private Callback<LineToken> handler;

    public LineTokenCallbackLineListener(Callback<LineToken> handler) {
        this.handler = handler;
    }

    @Override
    public void event(Line line) {
        handler.call(LineTokenData.parse(line));
    }
}
