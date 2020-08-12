package ramdan.file.line.token.data;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.LineTokenCache;
import ramdan.file.line.token.StringSave;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Statistic {

    private Map<String,Long> mapCount = new HashMap<>();
    private String input;
    private String inputExt;
    private String output;
    private int lineCount =0;
    private int fileCount =0;
    private long fileSize = 0;
    private long fileSizeMax = 0;
    private long fileSizeMin = Long.MAX_VALUE;
    public void setInput(String input) {
        this.input = input;
    }

    public void setInputExt(String inputExt) {
        this.inputExt = inputExt==null?"":inputExt;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    private void count(String str){
        Long c = mapCount.get(str);
        if(c == null){
            c = 0l;
        }
        c++;
        mapCount.put(str,c);
    }
    public synchronized void  add(LineToken lineToken){
        if(lineToken == null) return;
        lineCount++;
        for (int i = 0; i < lineToken.length(); i++) {
            count(lineToken.get(i));
        }
    }
    public synchronized void add(File file){
        if(file == null || !file.exists() || file.isDirectory()) return;
        fileCount++;
        fileSize+=file.length();
        fileSizeMax = Math.max(fileSizeMax,file.length());
        fileSizeMin = Math.min(fileSizeMin,file.length());
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
        out.printf("Output              : %s \n", output!=null?output:"stdout");
        out.println();
        if(fileCount>0){
            out.println("Statistic File");
            out.printf("Count file          : %16d \n", fileCount);
            out.printf("File Sum Length     : %16d \n", fileSize);
            out.printf("File Max Length     : %16d \n", fileSizeMax);
            out.printf("File Min Length     : %16d \n", fileSizeMin);
            out.println();
        }

        out.println("Statistic Data");
        out.printf("Count Line          : %16d \n", lineCount);
        out.printf("Count Token Empty   : %16d \n", mapCount.get(""));
        out.printf("Count Token         : %16d \n", tokenCount);
        out.printf("Size Token          : %16d \n", tokenSize);
        out.printf("Count Token Variant : %16d \n", tokenVariantCount);
        out.printf("Size Token Variant  : %16d \n", tokenVariantSize);
        //out.printf("Ratio               : %16d \n",(tokenSize/tokenVariantSize));
        StringSave.print(out);
        LineTokenCache.print(out);
    }
}
