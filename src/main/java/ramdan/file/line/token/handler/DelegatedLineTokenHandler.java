package ramdan.file.line.token.handler;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.data.Traceable;

import java.util.ArrayList;
import java.util.List;

public class DelegatedLineTokenHandler implements LineTokenHandler {
    private final LineTokenHandler[] handlers;
    private final boolean cleanTrace ;
    private static LineTokenHandler[] warp(LineTokenHandler ... handlers){
        if(handlers==null||handlers.length==0) return new LineTokenHandler[0];
        List<LineTokenHandler> list = new ArrayList<>();
        for (LineTokenHandler h :handlers) {
            if(h!=null && h != DefaultLineTokenHandler.DEFAULT_LINE_TOKEN_HANDLER){
                list.add(h.supportMultiLine() ?h :new MultilineLineTokenHandler(h));
            }
        }
        return list.toArray(new LineTokenHandler[list.size()]);
    }

    public DelegatedLineTokenHandler(boolean cleanTrace,LineTokenHandler ... handlers) {
        this.handlers = warp(handlers);
        this.cleanTrace =cleanTrace;
    }
    public DelegatedLineTokenHandler(LineTokenHandler ... handlers) {
        this(true,handlers);

    }
    public DelegatedLineTokenHandler(boolean cleanTrace,List<LineTokenHandler> handlers) {
        this(cleanTrace,handlers.toArray(new LineTokenHandler[handlers.size()]));
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
        Traceable source = null;
        if(cleanTrace && lineToken instanceof Traceable){
            source =(Traceable) lineToken;
        }
        for (LineTokenHandler h: handlers) {
            lineToken = h.process(lineToken);
        }
        if(source!=null){
            source.clearSource();
        }
        return lineToken;
    }
}
