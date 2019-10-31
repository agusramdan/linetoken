package ramdan.file.line.token.listener;

public interface ConfigBuilderListener<T> extends LineTokenListener {
    T getConfig();
}
