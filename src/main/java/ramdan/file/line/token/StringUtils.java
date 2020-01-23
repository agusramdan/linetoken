package ramdan.file.line.token;

public class StringUtils {

    /**
     * tag delimiter accept one of this
     * 1. "| "
     * 2. "|"
     * 3. " " default
     *
     * @param line
     * @return
     */
    public static String getGenevaTagDelimiter(String line){
        int idx = line.indexOf("| ");
        String delimiter =" ";
        if(idx>0){
            delimiter="| ";
        }
        int newIdx = line.indexOf("|");
        if(newIdx>1 && (idx == -1 || idx > newIdx) ){
            idx = newIdx;
            delimiter="|";
        }
        newIdx = line.indexOf(" ");
        if(newIdx>1 && (idx == -1 || idx > newIdx) ){
            idx = newIdx;
            delimiter=" ";
        }
        return delimiter;
    }
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
        chek= chek.toLowerCase();
        for (String p: parameter) {
            if(chek.contains(p.toLowerCase()))return true;
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

    public static boolean notEmpty(String value) {
        return value!=null && !"".equals(value);
    }

    public static boolean isEmpty(String value) {
        return value==null || "".equals(value);
    }

    public static long parseLength(String str){
        long pengali = 1;
        if(str.matches("\\d*kb?")){
            pengali = 0x400;
            str = str.replaceFirst("kb?","").trim();
        }else if(str.matches("\\d*mb?")){
            pengali = 0x100000;
            str = str.replaceFirst("mb?","").trim();
        }else if(str.matches("\\d*gb?")){
            pengali = 0x40000000;
            str = str.replaceFirst("gb?","").trim();
        }
        return Long.parseLong(str)*pengali;
    }

    public static boolean isNegativeAmount(String amount){
        return  amount.startsWith("-");
    }
    public static String removeNegativeSignAmount(String amount){
        return amount
                .replaceFirst("-","")
                .replaceFirst("\\(","").replaceAll("\\)","");
    }

    public static String emptyDefault(String value, String defaultString) {
        return isEmpty(value)?defaultString:value;
    }

    public static String[] trim(String... values) {
        for (int i = 0; i < values.length; i++) {
            if( values[i]!=null){
                values[i]=values[i].trim();
            }
        }
        return values;
    }
}
