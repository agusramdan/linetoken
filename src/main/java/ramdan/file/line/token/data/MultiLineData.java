package ramdan.file.line.token.data;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.MultiLine;
import ramdan.file.line.token.Tokens;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MultiLineData  implements MultiLine {

    public final static MultiLineData EMPTY = new MultiLineData(null,null);
    public static Tokens tokens(List<? extends Tokens> list) {
        if (list == null || list.isEmpty()) return EMPTY;
        List<Tokens> holder = new ArrayList<>();
        for (Tokens lt : list) {
            extract(lt,holder);
        }
        if (holder.isEmpty()) return EMPTY;
        if (holder.size()==1) return holder.get(0);
        Tokens[] lineTokens = holder.toArray(new Tokens[holder.size()]);
        Integer start= null;
        Integer end = null;
        for (Tokens t : lineTokens) {
            if(t.isEmpty()) continue;
            if(t instanceof LineToken){
                LineToken lt = (LineToken) t;
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
        }
        return new MultiLineData(start,end,lineTokens);
    }
    @Deprecated
    public static MultiLineData newInstance(List<Tokens> list) {
        if (list == null || list.isEmpty()) return EMPTY;
        List<Tokens> holder = new ArrayList<>();
        for (Tokens lt : list) {
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
    public static void extract(Tokens lt, List<Tokens> holder){
        if(lt == null || lt.isEmpty()) return;
        if(lt instanceof MultiLine){
            extract((MultiLine)lt,holder);
        }else {
            holder.add(lt);
        }
    }

    public static void extract(MultiLine multiLine, List<Tokens> holder){
        for ( int i = 0;i< multiLine.sizeLine();i++) {
            Tokens lt = multiLine.index(i);
            extract(lt,holder);
        }
    }
    public static Tokens merge(Tokens ... lineTokens) {
        return tokens(Arrays.asList(lineTokens));
    }

    Tokens[] lineTokens;

    public MultiLineData(Integer start,Integer end,Tokens ... lineTokens) {
        //super(start, end,null,null,null);
        this.lineTokens = lineTokens;

    }

    @Override
    public Tokens index(int idx) {
        return lineTokens[idx];
    }

    @Override
    public int sizeLine() {
        return lineTokens.length;
    }

    public void addTo(Collection collection){
        for (Tokens l: lineTokens) {
            collection.add(l);
        }
    }

    @Override
    public boolean isEmpty() {
        return lineTokens==null || lineTokens.length==0;
    }

    @Override
    public boolean isEOF() {
        return false;
    }
}
