package ramdan.file.line.token.handler;

import java.io.File;
import java.util.Collection;

public interface HandlerFactory {
    void prepare();
    void loadConfig(String string);
    LineTokenHandler getStartLineTokenHandler();
    void loadContentLineTokenHandlers(Collection<LineTokenHandler> holder);
    LineTokenHandler getOutputLineTokenHandler();
    LineTokenHandler getFinallyLineTokenHandler();
    Callback getFinishCallback();
}
