package ramdan.file.line.token;

public class StringUtils {
    public static boolean equalOnce(String param,String ...compare){
        for (String c: compare) {
            if(param.equals(c)) return true;
        }
        return false;
    }
    public static boolean equal(String p1, String p2){
        if(p1== null){
            return p2 == null;
        }
        return p1.equals(p2);
    }
    public static boolean equalIgnoreCase(String p1,String p2){
        if(p1== null){
            return p2 == null;
        }
        return p1.equalsIgnoreCase(p2);
    }
    public static boolean equal(String chek, String ... parameter){
        for (String p: parameter) {
            if(equal(chek,p))return true;
        }
        return false;
    }
    public static boolean equalIgnoreCase(String chek, String ... parameter){
        for (String p: parameter) {
            if(equalIgnoreCase(chek,p))return true;
        }
        return false;
    }

    public static boolean contain(String chek, String ... parameter){
        if(chek != null)  {
            for (String p: parameter) {
                if(chek.contains(p))return true;
            }
        }
        return false;
    }

    public static boolean containAll(String chek, String ... parameter){
        if(chek == null) return false;
        for (String p: parameter) {
            if(!chek.contains(p))return false;
        }
        return true;
    }

    public static boolean containIgnoreCase(String chek, String ... parameter){
        if(chek == null) return false;
        for (String p: parameter) {
            if(chek.contains(p))return true;
        }
        return false;
    }

    public static boolean containAllIgnoreCase(String chek, String ... parameter){
        if(chek == null) return false;
        chek = chek.toUpperCase();
        for (String p: parameter) {
            if(!chek.contains(p.toUpperCase()))return false;
        }
        return true;
    }
}
