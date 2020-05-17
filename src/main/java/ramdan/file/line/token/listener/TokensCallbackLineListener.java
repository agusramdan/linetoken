package ramdan.file.line.token.listener;

import ramdan.file.line.token.Line;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.Tokens;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.handler.Callback;
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
        setConverter(converter); ;
    }

    public void setConverter(LineTokenConverter converter) {
        if(converter==null) {
            converter = DefaultLineTokenConverter.DEFAULT;
            return;
        }
        this.converter = converter;
    }

    @Override
    public void event(Line line) {
        LineTokenData.parse(line);
        handler.call(converter.convert(line));
    }
}
