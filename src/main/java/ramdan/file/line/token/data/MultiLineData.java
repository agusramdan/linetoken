package ramdan.file.line.token.data;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.MultiLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MultiLineData  extends LineTokenData implements MultiLine {
    public final static MultiLineData EMPTY = new MultiLineData(null,null);

    public static MultiLineData newInstance(List<LineToken> list) {
        if (list == null || list.isEmpty()) return EMPTY;
        List<LineToken> holder = new ArrayList<>();
        for (LineToken lt : list) {
            extract(lt,holder);
        }
        if (holder.isEmpty()) return EMPTY;
        LineToken[] lineTokens = holder.toArray(new LineToken[holder.size()]);
        Integer start= null;
        Integer end = null;
        for (LineToken lt : lineTokens) {
            if(start == null){
                start= lt.getStart();
            }else if(lt.getStart()!=null){
                start = Math.min(start,lt.getStart());
            }

            if(end == null){
                end= lt.getEnd();
            }else if(lt.getEnd()!=null){
                end = Math.min(end,lt.getEnd());
            }
        }
        return new MultiLineData(start,end,lineTokens);
    }
    public static void extract(LineToken lt, List<LineToken> holder){
        if(lt == null) return;
        if(lt instanceof  MultiLine){
            extract((MultiLine)lt,holder);
        }else {
            holder.add(lt);
        }
    }

    public static void extract(MultiLine multiLine, List<LineToken> holder){
        for ( int i = 0;i< multiLine.sizeLine();i++) {
            LineToken lt = multiLine.index(i);
            extract(lt,holder);
        }
    }
    public static LineToken merge(LineToken ... lineTokens) {
        return newInstance(Arrays.asList(lineTokens));
    }

    LineToken[] lineTokens;
    public MultiLineData(Integer start,Integer end,LineToken ... lineTokens) {
        super(start, end);
        this.lineTokens = lineTokens;

    }

    @Override
    public LineToken index(int idx) {
        return lineTokens[idx];
    }

    @Override
    public int sizeLine() {
        return lineTokens.length;
    }

    public void addTo(Collection collection){
        for (LineToken l: lineTokens) {
            collection.add(l);
        }
    }
}
