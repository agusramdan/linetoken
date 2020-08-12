package ramdan.file.line.token.callback;

import ramdan.file.line.token.callback.Callback;

public interface CallbackChain <S,T> extends Callback<S> {
    void setNext(Callback<T> next);
}
