package ramdan.file.line.token;

import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public class LineTokenCache{
    private static WeakHashMap<LineToken,WeakReference<Line>> map = new WeakHashMap<>();
    static boolean lineCache = false;
    public static LineToken cache(LineToken lineToken,Line line){
        if(!lineCache || lineToken==null) return lineToken;
        map.put(lineToken, new WeakReference<>(line));
        return lineToken;
    }
    public static Line get(LineToken lineToken){
        if(!lineCache || lineToken==null) return null;
        WeakReference<Line> wr = map.get(lineToken);
        return  wr == null? null: wr.get();
    }

    public static long count(){
        return map.size();
    }

    public static void clear(){
         map.clear();
    }

    public static void print(PrintStream out){
        if(!lineCache){
            return;
        }
        if(out == null) {
            out = System.out;
        }
        long tokenCount =0;
        long tokenSize = 0;
        for (WeakReference<Line> wr : map.values()) {
            Line t = wr.get();
            if(t == null) continue;
            tokenCount ++;
            tokenSize += t.length();
        }
        out.println("Cache Line Statistic");
        out.printf("Count Line         : %16d \n", tokenCount);
        out.printf("Size Line          : %16d \n", tokenSize);
        out.println();
    }

    public static void args(String ... args){
        for (String arg : args) {
            if( "-cache".equals(arg)){
                lineCache = true;
            }
        }
    }
}
