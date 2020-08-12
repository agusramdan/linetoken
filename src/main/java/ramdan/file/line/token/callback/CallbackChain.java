package ramdan.file.line.token.callback;

public interface CallbackChain <S,T> extends Callback<S> {
    void setNext(Callback<T> next);
}
