package ramdan.file.line.token.handler;

public interface CallbackChain <S,T> extends Callback<S> {
    void setNext(Callback<T> next);
}
