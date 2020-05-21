package ramdan.file.line.token.handler;

import ramdan.file.line.token.Tokens;
import ramdan.file.line.token.callback.Callback;

public interface TokensHandler extends Handler, Callback<Tokens> {
    void setNext(Callback<Tokens> next);
    @Deprecated
    Tokens process(Tokens tokes);

    void processCallback(Tokens tokes,Callback<Tokens> next);
}
