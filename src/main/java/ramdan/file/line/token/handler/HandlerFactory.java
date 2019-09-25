package ramdan.file.line.token.handler;

public interface HandlerFactory {
    LineTokenHandler getStartLineTokenHandler();
    LineTokenHandler getFinallyLineTokenHandler();

}
