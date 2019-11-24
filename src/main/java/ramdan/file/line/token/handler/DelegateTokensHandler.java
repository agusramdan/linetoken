package ramdan.file.line.token.handler;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.LineTokensHolder;
import ramdan.file.line.token.MultiLine;
import ramdan.file.line.token.Tokens;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.data.MultiLineData;

import java.util.ArrayList;
import java.util.List;

public class DelegateTokensHandler implements TokensHandler{

    private LineTokenHandler delegated;

    public DelegateTokensHandler(LineTokenHandler delegated) {
        this.delegated = delegated;
    }

    private Tokens processLine(LineToken lineToken){
        return delegated.process(lineToken);
    }

    private void toHolder(List<Tokens> holder,Tokens lt){
        if(lt!=null && (lt.isEOF()|| (LineTokenData.EMPTY!= lt && MultiLineData.EMPTY!= lt))){
            holder.add(lt);
        }
    }
    private Tokens processLineTokensHolder(LineTokensHolder lineTokensHolder){
        List<Tokens> holder = new ArrayList<>();
        toHolder(holder,processLine(lineTokensHolder.getStartBlock()));
        while (lineTokensHolder.hashNextContent()){
            toHolder(holder,processLine(lineTokensHolder.nextContent()));
        }
        toHolder(holder,processLine(lineTokensHolder.getEndBlock()));
        return MultiLineData.tokens(holder);
    }
    private Tokens processMultiLine(MultiLine lineToken){
        int size = lineToken.sizeLine();
        List<Tokens> holder = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            toHolder(holder,process(lineToken.index(i)));
        }
        return MultiLineData.tokens(holder);
    }
    public Tokens process(Tokens lineToken){
        if(lineToken==null){
            return LineTokenData.EMPTY;
        }
        if(lineToken instanceof LineTokensHolder){
            return processLineTokensHolder((LineTokensHolder) lineToken);
        }
        if(lineToken instanceof MultiLine){
            return processMultiLine((MultiLine) lineToken);
        }
        if(lineToken instanceof LineToken){
            return processLine((LineToken)lineToken);
        }
        return lineToken;
    }
}
