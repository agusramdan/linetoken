package ramdan.file.line.token.handler;

public abstract class AbstractReturnCallback<I,R> implements ReturnCallback<I,R>{

    private Callback<R> returnCallback;

    public AbstractReturnCallback(Callback<R> returnCallback) {
        this.returnCallback = returnCallback;
    }

    public void setReturnCallback(Callback<R> returnCallback) {
        this.returnCallback = returnCallback;
    }

    @Override
    public void call(I input) {
        processCallback(input,returnCallback);
    }
}
