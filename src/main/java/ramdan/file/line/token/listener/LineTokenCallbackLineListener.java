package ramdan.file.line.token.listener;

import jdk.nashorn.internal.parser.Token;
import ramdan.file.line.token.Line;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.Tokens;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.handler.Callback;
import ramdan.file.line.token.handler.LineTokenHandler;

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
