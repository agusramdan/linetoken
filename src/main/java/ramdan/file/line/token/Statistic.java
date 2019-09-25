package ramdan.file.line.token;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Statistic {

    private Map<String,Long> mapCount = new HashMap<>();
    private String input;
    private String inputExt;
    private int lineCount =0;

    public void setInput(String input) {
        this.input = input;
    }

    public void setInputExt(String inputExt) {
        this.inputExt = inputExt==null?"":inputExt;
    }
    private void count(String str){
        Long c = mapCount.get(str);
        if(c == null){
            c = 0l;
        }
        c++;
        mapCount.put(str,c);
    }
    public void add(LineToken lineToken){
        if(lineToken == null) return;
        lineCount++;
        for (int i = 0; i < lineToken.length(); i++) {
            count(lineToken.get(i));
        }
    }
    public void print(PrintStream out) {
        long tokenVariantSize = 0;
        long tokenVariantCount = 0;
        long tokenCount = 0;
        long tokenSize = 0;
        Set<Map.Entry<String, Long>> entitys = mapCount.entrySet();
        for (Map.Entry<String, Long> e : entitys) {
            if ("".equals(e.getKey())) {
                continue;
            }
            tokenCount += e.getValue();
            tokenSize += (e.getKey().length() * e.getValue());
            tokenVariantCount++;
            tokenVariantSize += e.getKey().length();
        }
        out.println("Statistic");
        out.printf("Input               : %s \n", input);
        out.printf("File extension      : %s \n", inputExt);
        out.printf("Count Line          : %16d \n", lineCount);
        out.printf("Count Token Empty   : %16d \n", mapCount.get(""));
        out.printf("Count Token         : %16d \n", tokenCount);
        out.printf("Size Token          : %16d \n", tokenSize);
        out.printf("Count Token Variant : %16d \n", tokenVariantCount);
        out.printf("Size Token Variant  : %16d \n", tokenVariantSize);
        //out.printf("Ratio               : %16d \n",(tokenSize/tokenVariantSize));
        StringSave.print(out);
    }
}
