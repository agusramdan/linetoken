package ramdan.file.line.token.config;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.listener.LineTokenListener;

public abstract class ConfigToken implements Config {
    final LineTokenListener configListener = new ConfigLineTokenListener() ;

    protected abstract void configToken(LineToken lineToken);

    class ConfigLineTokenListener implements LineTokenListener {
        @Override
        public void event(LineToken lineToken) {
            configToken(lineToken);
        }
    }
}
