package ramdan.file.line.token.handler;

import java.io.File;

public interface HandlerFactory {
    void loadConfig(String string);
    LineTokenHandler getStartLineTokenHandler();
    LineTokenHandler getEndLineTokenHandler();
    LineTokenHandler getOutputLineTokenHandler();
    LineTokenHandler getFinallyLineTokenHandler();

}
