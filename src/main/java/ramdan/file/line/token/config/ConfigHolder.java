package ramdan.file.line.token.config;

import java.util.HashMap;
import java.util.Map;

public class ConfigHolder {
    private final static Map<Object,Config> holder = new HashMap<>();

    public static <T extends Config> T  getConfig(Class<T> cls){
        return (T) holder.get(cls);
    }
    public static <T extends Config> T  getConfig(String cls){
        return (T) holder.get(cls);
    }
    public static void add(Config t){
        put(t.getClass(),t);
        put(t.getClass().getName(),t);
    }
    public static void put(Object key,Config value){
        holder.put(key,value);
    }

    public static boolean containsConfig(Object o) {
        return holder.containsKey(o);
    }
}
