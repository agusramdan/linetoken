package ramdan.file.line.token.handler;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.LineTokenData;
import ramdan.file.line.token.MultiLine;
import ramdan.file.line.token.MultiLineData;

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
        Integer start = null;
        Integer end = null;
        List<LineToken> holder = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            LineToken line = processLine(lineToken.index(i));
            if(line.length()>0){
                holder.add(line);
            }
        }
        return holder.isEmpty()
                ? MultiLineData.EMPTY
                : new MultiLineData(start,end,holder.toArray(new LineToken[holder.size()]));
    }
    public LineToken process(LineToken lineToken){
        if(lineToken instanceof MultiLine){
            MultiLineData data = processMultiLine((MultiLine) lineToken);
            return  data.sizeLine()==0
                    ? LineTokenData.EMPTY
                    : data;
        }
        return processLine(lineToken);
    }
}
