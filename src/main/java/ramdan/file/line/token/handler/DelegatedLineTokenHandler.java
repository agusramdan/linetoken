package ramdan.file.line.token.handler;

import lombok.val;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.Tokens;
import ramdan.file.line.token.data.Traceable;

import java.util.ArrayList;
import java.util.List;

public class DelegatedLineTokenHandler implements LineTokenHandler {
    private final TokensHandler[] handlers;
    private final boolean cleanTrace ;
    private static TokensHandler[] warp(LineTokenHandler ... handlers){
        if(handlers==null||handlers.length==0) return new TokensHandler[0];
        List<TokensHandler> list = new ArrayList<>();
        for (LineTokenHandler h :handlers) {
            if(h!=null){
                if(h instanceof TokensHandler){
                    list.add((TokensHandler) h);
                }else {
                    list.add(new DelegateTokensHandler(h));
                }
            }
        }
        return list.toArray(new TokensHandler[list.size()]);
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

    public Tokens process(LineToken lineToken) {

        return processTokens(lineToken);
    }

    public Tokens processTokens(Tokens lineToken) {
        Traceable source = null;
        if(cleanTrace && lineToken instanceof Traceable){
            source =(Traceable) lineToken;
        }
        for (TokensHandler h: handlers) {
            lineToken= h.process(lineToken);

        }
        if(source!=null){
            source.clearSource();
        }
        return lineToken;
    }
}
