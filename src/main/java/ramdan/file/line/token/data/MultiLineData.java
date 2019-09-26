package ramdan.file.line.token.data;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.MultiLine;

import java.util.Collection;
import java.util.List;

public class MultiLineData  extends LineTokenData implements MultiLine {
    public final static MultiLineData EMPTY = new MultiLineData(null,null);

    public static MultiLineData newInstance(List<LineToken> list) {
        LineToken[] lineTokens = new LineToken[list.size()];
        Integer start= null;
        Integer end = null;
        return new MultiLineData(start,end,lineTokens);
    }
    LineToken[] lineTokens;
    public MultiLineData(Integer start,Integer end,LineToken ... lineTokens) {
        super(start, end);
        this.lineTokens = lineTokens;

    }

    @Override
    public LineToken index(int idx) {
        return null;
    }

    @Override
    public int sizeLine() {
        return 0;
    }

    public void addTo(Collection collection){
        for (LineToken l: lineTokens) {
            collection.add(l);
        }
    }
}
