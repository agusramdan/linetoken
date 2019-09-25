package ramdan.file.line.token.handler;

import ramdan.file.line.token.LineToken;

import java.util.List;

public class DelegatedLineTokenHandler implements LineTokenHandler {
    private final LineTokenHandler[] handlers;

    private static LineTokenHandler[] warp(LineTokenHandler ... handlers){
        if(handlers==null||handlers.length==0) return new LineTokenHandler[0];
        LineTokenHandler[] warpHandler = new LineTokenHandler[handlers.length];
        for (int i = 0; i < handlers.length; i++) {
            warpHandler[i]= new MultilineLineTokenHandler(handlers[i]);
        }
        return warpHandler;
    }

    public DelegatedLineTokenHandler(LineTokenHandler ... handlers) {
        this.handlers = warp(handlers);
    }
    public DelegatedLineTokenHandler(List<LineTokenHandler> handlers) {
        this(handlers.toArray(new LineTokenHandler[handlers.size()]));
    }

    @Override
    public boolean supportMultiLine() {
        return true;
    }

    @Override
    public LineToken process(LineToken lineToken) {
        for (LineTokenHandler h: handlers) {
            lineToken = h.process(lineToken);
        }
        return lineToken;
    }
}
