package ramdan.file.line.token.handler;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.MultiLine;
import ramdan.file.line.token.data.MultiLineData;

import java.util.ArrayList;
import java.util.List;

public class MultilineLineTokenHandler implements LineTokenHandler {

    private LineTokenHandler delegated;

    public MultilineLineTokenHandler(LineTokenHandler delegated) {
        this.delegated = delegated;
    }

    @Override
    public boolean supportMultiLine() {
        return true;
    }

    public LineToken processLine(LineToken lineToken){
        return delegated.process(lineToken);
    }

    public MultiLineData processMultiLine(MultiLine lineToken){
        int size = lineToken.sizeLine();
        List<LineToken> holder = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            LineToken line = lineToken.index(i);
            if(line != null){
                line= processLine(line);
            }else {
                continue;
            }
            if(line==null){
                continue;
            }
            if(line.isEOF()|| (LineTokenData.EMPTY!= line && MultiLineData.EMPTY!= line)){
                holder.add(line);
            }
        }
        return MultiLineData.newInstance(holder);
    }
    public LineToken process(LineToken lineToken){
        if(lineToken instanceof MultiLine){
            MultiLineData data = processMultiLine((MultiLine) lineToken);
            return  data.sizeLine()==0
                    ? LineTokenData.EMPTY
                    : data.sizeLine() == 1 ? data.index(0):data;
        }
        return processLine(lineToken);
    }
}
