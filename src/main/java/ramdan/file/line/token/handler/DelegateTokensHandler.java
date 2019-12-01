package ramdan.file.line.token.handler;

import lombok.Setter;
import lombok.val;
import ramdan.file.line.token.MultiLine;
import ramdan.file.line.token.Tokens;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.data.MultiLineData;

public class DelegateTokensHandler implements TokensHandler,Callback<Tokens>{

    private TokensHandler delegate;
    @Setter
    private Callback<Tokens> next;

    public DelegateTokensHandler(TokensHandler delegate) {
        this.delegate = delegate;
    }

    private void processMultiLine(MultiLine lineToken , Callback<Tokens> next){
        int size = lineToken.sizeLine();
        for (int i = 0; i < size; i++) {
            processCallback(lineToken.index(i),next);
        }
    }
    public Tokens process(Tokens lineToken){
        if(lineToken==null){
            return LineTokenData.EMPTY;
        }
        val next = new ArrayListCallback<Tokens>();
        processCallback(lineToken,next);
        return MultiLineData.tokens(next.getArrayList());
    }

    @Override
    public void processCallback(Tokens tokes, Callback<Tokens> next) {
        if(tokes==null||(!tokes.isEOF()&&tokes.isEmpty())){
            next.call(LineTokenData.EMPTY);
            return;
        }
        if(tokes instanceof MultiLine){
            processMultiLine((MultiLine) tokes, next);
        }else {
            next.call(delegate.process(tokes));
        }
    }

    @Override
    public void call(Tokens tokens) {
        processCallback(tokens,next);

    }
}
