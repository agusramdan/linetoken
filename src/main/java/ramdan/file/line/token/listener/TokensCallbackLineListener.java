package ramdan.file.line.token.listener;

import ramdan.file.line.token.Line;
import ramdan.file.line.token.Tokens;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.callback.Callback;
import ramdan.file.line.token.handler.DefaultLineTokenConverter;
import ramdan.file.line.token.handler.LineTokenConverter;

public class TokensCallbackLineListener implements LineListener {
    private Callback<Tokens> handler;
    private LineTokenConverter converter;
    public TokensCallbackLineListener(Callback<Tokens> handler) {
        this(handler,null);
    }

    public TokensCallbackLineListener(Callback<Tokens> handler, LineTokenConverter converter) {
        this.handler = handler;
        if(converter==null) {
            this.converter = DefaultLineTokenConverter.DEFAULT;
        }else {
            this.converter= converter;
        }
    }

    public void setConverter(LineTokenConverter converter) {
        if(converter==null) {
            return;
        }
        this.converter = converter;
    }

    @Override
    public void event(Line line) {
        handler.call(converter.convert(line));
    }
}
