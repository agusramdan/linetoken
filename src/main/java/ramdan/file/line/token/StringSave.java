package ramdan.file.line.token;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class StringSave {
    static boolean stringSave = false;
    static boolean stringIntern = false;
    private static WeakHashMap<String,WeakReference<String>> map = new WeakHashMap<>();

    private synchronized static String saveCache(String string){
        WeakReference<String> weakReference  = map.get(string);
        String cache = null;
        if(weakReference!=null)
            cache = weakReference.get();
        if (cache == null) {
            map.put(string, new WeakReference<>(string));
            cache = string;
        }
        return cache;
    }

    public static String save(String string){
        if(string==null) return null;
        if(stringSave) {
            if (string.length()==0) return "";
            if (stringIntern) {
                return string.intern();
            } else {
                return saveCache(string);
            }
        }else {
            return string;
        }
    }

    public static long count(){
        return map.size();
    }

    public static void clear(){
         map.clear();
    }


    public static void print(PrintStream out){
        if(!stringSave ||stringIntern){
            return;
        }
        if(out == null) {
            out = System.out;
        }
        long tokenCount =0;
        long tokenSize = 0;
        for (WeakReference<String> wr : map.values()) {
            String t = wr.get();
            if(t == null) continue;
            tokenCount ++;
            tokenSize += t.length();
        }
        out.println("Cache Statistic");
        out.printf("Count Token         : %16d \n", tokenCount);
        out.printf("Size Token          : %16d \n", tokenSize);
        out.println();
    }

    public static void args(String ... args){
        for (String arg : args) {
            if( "-save".equals(arg)){
                stringSave = true;
            }else if( "-intern".equals(arg)){
                stringIntern = true;
            }else if("-nosave".equals(arg)){
                stringSave =false;
            }
        }
    }
}
