package ramdan.file.line.token;
import ramdan.file.line.token.filter.FilterComplex;
import ramdan.file.line.token.filter.MultiLineTokenFilter;
import ramdan.file.line.token.filter.RegexMatchRule;
import ramdan.file.line.token.handler.*;
import ramdan.file.vfree.InvoiceHandlerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    private Map<String,String> parameters = new HashMap<>();
    private List<LineToken> lineTokenHolder ;
    private Set<String> filterRule;
    private Map<String,String> mappingRule;
    private PrintStream output;
    private Statistic statistic;
    /**
     * Populate Argument
     * -i         : input directory of file
     * -ix        :
     * -trim      : trim token
     * -save      : save / canonical string
     * -empty     : null to empty
     * -map       :
     * -mb        : map block start tag to end ex -mb PSTS,BSTARTPSTS,BENDPSTS,PSTS_1,PSTS_2;
     * -intern    :
     * -statistic :
     * -hold      :
     * -silent    :
     * -fc        : filter complex
     * -fx        : filter regex
     * -filter    :
     * -df        : distribution file to multiple directory method copy2,move ex. copy,move
     * -dfn       : pre neame drectory ex: P  (optional)
     * -max       : partition max length in byte per parition(optional with default 6gb)
     */
    private boolean equalOnce(String param,String ...compare){
        for (String c: compare) {
            if(param.equals(c)) return true;
        }
        return false;
    }

    public void args(String ... args){
        String param = null;
        int count = 0;
        for (String arg : args) {
            if(param!=null){
                parameters.put(param,arg);
                param = null;
            }else
            if(arg.startsWith("-")){
                param = arg;
                if(equalOnce(param,"-trim","-save", "-empty","-intern","-statistic","-hold", "-silent")){
                    parameters.put(param,"true");
                    param = null;
                }else
                if("-notrim".equals(param)){
                    parameters.put("-trim","false");
                    param = null;
                }else
                if("-nosave".equals(param)){
                    parameters.put("-save","false");
                    param = null;
                }else
                if("-null".equals(param)){
                    parameters.put("-empty","false");
                    param = null;
                }
            }
        }
        StringSave.args(args);
        LineTokenData.args(args);
    }
    private void process() throws IOException {
        process(null);
    }
    private void process(File input) throws IOException {
        process(input,null);
    }
    void process(File input,File output) throws IOException {
        InputStreamReader reader = input!=null? new FileReader(input) : new InputStreamReader(System.in);
        List<LineTokenHandler> list = new ArrayList<>();
        HandlerFactory handlerFactory = new InvoiceHandlerFactory();
        if(statistic!=null){
            list.add(new StatisticLineTokenHandler(statistic));
        }
        list.add(handlerFactory.getStartLineTokenHandler());
        if(parameters.containsKey("-fcs")){
            String cls =parameters.get("-fcs");
            addFilterHandlers(list,cls);
        }
        if(parameters.containsKey("-fc")){
            //String cls =parameters.get("-fc");
            File file = new File(parameters.get("-fc"));
            list.add(new FilterComplexLineTokenHandler(FilterComplex.read(file)));
        }

        if(parameters.containsKey("-fx")){
            list.add(new RegexLineTokenHandler(parameters.get("-fx")));
        }

        if(filterRule!=null){
            list.add(new FilterLineTokenHandler(filterRule));
        }

        if(parameters.containsKey("-fd")){
            list.add(new DuplicateFilterLineTokenHandler(
                    "BSTARTEVSUMMARY_\\d*|TSTARTEVSUMMROW_\\d*|EVSUMMROWNAME_\\w*|EVSUMMROW\\d*" ,
                    "BENDEVSUMMARY_\\d*|TENDEVSUMMARY_\\d*","DOCSTART\\w*|DOCEND|BSTARTGROUP|BENDGROUP|BSTARTEVSUMMGROUP|BENDEVSUMMGROUP|BSTARTPRODITEM|BENDPRODITEM"));
        }

        if(parameters.containsKey("-mcs")){
            String cls =parameters.get("-mcs");
            addMappingHandlers(list,cls);
        }
        // mapping
        if(mappingRule !=null){
            list.add(new MappingLineTokenHandler(mappingRule));
        }

        if(lineTokenHolder != null){
            list.add(new ListLineTokenHandler(lineTokenHolder));
        }


        if(output !=null){
            list.add(new PrintStreamLineTokenHandler(new PrintStream(output)));
        }else if(this.output!=null){
            list.add(new PrintStreamLineTokenHandler(this.output));
        }
        list.add(handlerFactory.getFinallyLineTokenHandler());
        process(reader,new DelegatedLineTokenHandler(list));
    }
    private Map <String,Object> cacheInstance = new HashMap<>();

    private Object getInstance(String c){
        Object obj =null;
        if(cacheInstance.containsKey(c)){
            obj = cacheInstance.get(c);
        }
        if(obj== null) {
            try {
                obj = Class.forName(c).newInstance();
                if(obj instanceof FilterComplex||
                        obj instanceof RegexMatchRule||
                        obj instanceof MultiLineTokenFilter){
                    cacheInstance.put(c,obj);
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
        return obj;
    }
    private void addFilterHandlers(List<LineTokenHandler> list, String cls)  {
        String[] clss = cls.split(",");
        for (String c: clss) {
            Object obj = getInstance(c);
            if(obj!= null) {
                if(obj instanceof FilterComplex){
                    list.add(new FilterComplexLineTokenHandler((FilterComplex)obj));
                }else if(obj instanceof RegexMatchRule){
                    list.add(new RegexLineTokenHandler((RegexMatchRule)obj));
                }else{
                    list.add((LineTokenHandler) obj);
                }
            }
        }
    }
    private void addMappingHandlers(List<LineTokenHandler> list, String cls)  {
        String[] clss = cls.split(",");
        for (String c: clss) {
            Object obj = getInstance(c);
            if(obj!= null) {
                if(obj instanceof MultiLineTokenFilter){
                    list.add(new MappingMultiLineTokenHandler((MultiLineTokenFilter) obj));
                }else if(obj instanceof LineTokenHandler){
                    list.add((LineTokenHandler) obj);
                }
            }
        }
    }

    void process(InputStreamReader streamReader, LineTokenHandler handler) throws IOException {
        LineNumberReader reader = new LineNumberReader(streamReader);
        String line;
        while ((line =reader.readLine())!=null){
            handler.process(LineTokenData.parse(reader.getLineNumber(),line));
        }
    }

    List<File> listFilesRecursive(File file,FileFilter fileFilter){
        List<File> fileList = new ArrayList<>();
        if(file.isDirectory()){
            File[] files=file.listFiles(fileFilter);
            for (File f:files) {
                fileList.addAll(listFilesRecursive(f,fileFilter));
            }
        }else
        if(file.isFile()){
            fileList.add(file);
        }
        return fileList;
    }
    long parseLength(String str){
        long pengali = 1;
        if(str.endsWith("k")){
            pengali = 1024;
            str = str.replace("g","").trim();
        }else if(str.endsWith("m")){
            pengali = 1024*1024;
            str = str.replace("m","").trim();
        } if(str.endsWith("g")){
            pengali = 1024*1024*1024;
            str = str.replace("g","").trim();
        }
        return Long.parseLong(str)*pengali;
    }
    void distributionFile(final  File directoryInput,final File directoryOutput) throws IOException {
        String df = parameters.get("-df");
        List<File> fileList = listFilesRecursive(directoryInput, new ExtensionFileFilter(parameters.get("-ix")));
        long maxLength = Long.MAX_VALUE;
        /**
         * Get sample data and copy to directory
         */
        if(df.startsWith("sample")){
            String length = df.substring(6);
            maxLength = parseLength(length);
            long sumLength = 0;
            int idx = 0;
            while (sumLength < maxLength && idx < fileList.size()){
                File file = fileList.get(idx);
                if(file.length()+sumLength< maxLength){
                    sumLength +=file.length();
                    File target = new File(directoryOutput,relativize(directoryInput,file));
                    copy(file,target);
                }
            }
        }

    }
    void processDirectoryFile(final File input, File output) throws IOException{
        final boolean isDirectoryOutput = output!=null && output.isDirectory();
        if(Boolean.parseBoolean(parameters.get("-statistic"))){
            statistic = new Statistic();
            statistic.setInput(parameters.get("-i"));
            statistic.setInputExt(parameters.get("-ix"));
        }
        if(Boolean.parseBoolean(parameters.get("-hold"))){
            lineTokenHolder = new ArrayList<>();
        }

        if(input !=null && input.isDirectory()){
            List<File> fileList = listFilesRecursive(input,  new ExtensionFileFilter(parameters.get("-ix")));

            for (File fileInput:fileList) {
                File fileOutput =isDirectoryOutput
                        ?new File(output,relativize(input,fileInput))
                        :output;
                process(fileInput,fileOutput);
            }
        }else{
            if(input != null){
                File fileOutput = isDirectoryOutput
                        ?new File(output,input.getName())
                        :output;

                process(input,fileOutput);
            }else{
                process();
            }
        }
        if(statistic!=null) {
            this.statistic.print(System.out);

        }
    }

    private String relativize(File input, File fileInput) {
        Path inputPathParent = input.toPath();
        Path fileInputPath = fileInput.getParentFile().toPath();
        Path path = fileInputPath.relativize(inputPathParent);
        return Paths.get(path.toString(),fileInput.getName()).toString();
    }

    public void runNoFileInput(){
        List<String> list = new ArrayList<>();
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            list.add(StringSave.save("String "+ i));
            if(i % 100000 ==0) {
                System.out.printf("count %d , %d \n", StringSave.count(), i);
                list.clear();
            }
        }
    }

    public void run() throws IOException {
        String inputString=parameters.get("-i");
        if(inputString==null){
            runNoFileInput();
            return;
        }
        File inputFile = new File(inputString);

        String outputString=parameters.get("-o");
        File outputFile = null;
        if(outputString==null){
            if(!Boolean.parseBoolean(parameters.get("-silent"))){
                output = System.out;
            }
        }else{
            outputFile = new File(outputString);
        }
        if(parameters.containsKey("-df")){
            distributionFile(inputFile,outputFile);
        }else {
            processDirectoryFile(inputFile, outputFile);
        }
    }

    public static void main(String ... args) throws IOException {
        Main main = new Main();
        main.args(args);
        main.run();
    }

    public static class ExtensionFileFilter implements FileFilter{
        private String extension;

        public ExtensionFileFilter(String inputExtension) {
            this.extension = inputExtension;
        }

        @Override
        public boolean accept(File file) {
            return file.isDirectory() ||
                    (file.isFile() && (extension==null ||file.getName().endsWith(extension)));
        }
    }

    private static void copy(File source, File dest) throws IOException {
        try (
                InputStream is = new FileInputStream(source);
                OutputStream os = new FileOutputStream(dest);
        ){
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }

}

