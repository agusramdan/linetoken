package ramdan.file.line.token.listener;

import ramdan.file.line.token.Line;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.Tokens;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.handler.Callback;

public class TokensCallbackLineListener implements LineListener {
    private Callback<Tokens> handler;

    public TokensCallbackLineListener(Callback<Tokens> handler) {
        this.handler = handler;
    }

    @Override
    public void event(Line line) {
        handler.call(LineTokenData.parse(line));
    }
}
