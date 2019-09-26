package ramdan.file.line.token;
import ramdan.file.line.token.data.LineData;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.data.Statistic;
import ramdan.file.line.token.filter.FilterComplex;
import ramdan.file.line.token.filter.MultiLineTokenFilter;
import ramdan.file.line.token.filter.RegexMatchRule;
import ramdan.file.line.token.handler.*;
import ramdan.file.line.token.listener.LineListener;
import ramdan.file.line.token.listener.LineTokenHandlerLineListener;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static Main main = new Main();

    private Map<String,String> parameters = new HashMap<>();
    private List<LineToken> lineTokenHolder ;
    private Set<String> filterRule;
    private Map<String,String> mappingRule;
    private PrintStream output;
    private Statistic statistic;
    private HandlerFactory handlerFactory;
    private FilterComplex filterComplex;
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
     * -hf        : handler factory
     * -hfcfg     | file config for handler factory
     * -df        : distribution file to multiple directory method copy2,move ex. copy,move
     * -dfn       : pre neame drectory ex: P  (optional)
     * -max       : partition max length in byte per parition(optional with default 6gb)
     */
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
                if(StringUtils.equalOnce(param,"-trim","-save", "-empty","-intern","-statistic","-hold", "-silent")){
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
    public String getArgumentParameter(String key){
        return this.parameters.get(key);
    }
    private void process() throws IOException {
        process(null);
    }
    private void process(File input) throws IOException {
        process(input,null);
    }
    void process(File input,File output) throws IOException {
        List<LineTokenHandler> list = new ArrayList<>();

        if(statistic!=null){
            statistic.add(input);
            list.add(new StatisticLineTokenHandler(statistic));
        }
        if(handlerFactory != null) {
            list.add(handlerFactory.getStartLineTokenHandler());
        }
        if(parameters.containsKey("-fcs")){
            String cls =parameters.get("-fcs");
            addFilterHandlers(list,cls);
        }
        if(filterComplex!=null){
            list.add(new FilterComplexLineTokenHandler(filterComplex));
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

        if(handlerFactory!=null){
            list.add(handlerFactory.getEndLineTokenHandler());
        }

        PrintStream printStream = null;
        OutputLineTokenHandler outputLineTokenHandler = null;
        try {
            LineTokenHandler outputHandler= null;
            if (handlerFactory != null) {
                outputHandler = handlerFactory.getOutputLineTokenHandler();
            }
            if(outputHandler == null){
                if (output != null) {
                    printStream = new PrintStream(output);
                    outputHandler = new PrintStreamLineTokenHandler(new PrintStream(output));
                } else if (this.output != null) {
                    outputHandler = new PrintStreamLineTokenHandler(this.output);
                }
            }
            if(outputHandler!=null){
                if(outputHandler instanceof OutputLineTokenHandler){
                    outputLineTokenHandler = (OutputLineTokenHandler) outputHandler;
                    outputLineTokenHandler.setFileOutput(output);
                    outputLineTokenHandler.setFileInput(input);
                    String directory = parameters.get("-o");
                    if (directory != null) {
                        outputLineTokenHandler.setBaseDirectoryOutput(new File(directory));
                    }
                    if(input != null) {
                        directory = parameters.get("-i");
                        if (directory != null) {
                            outputLineTokenHandler.setBaseDirectoryInput(new File(directory));
                        }
                    }
                }
                list.add(outputHandler);
            }
            if (handlerFactory != null) {
                list.add(handlerFactory.getFinallyLineTokenHandler());
            }
            LineTokenHandler handler = new DelegatedLineTokenHandler(list);
            LineListener listener = new LineTokenHandlerLineListener(handler);
            if(input != null){
                StreamUtils.readLine(input,listener);
            }else {
                StreamUtils.readLine(System.in,listener);
            }

        }finally{
            if(outputLineTokenHandler != null){
                outputLineTokenHandler.flush();
            }
            flushIgnore(printStream);
            closeIgnore(outputLineTokenHandler);
            closeIgnore(printStream);
        }
    }
    void flushIgnore(PrintStream closeable)   {
        if(closeable !=null){
            closeable.flush();
        }
    }
    void closeIgnore(Closeable closeable)   {
        if(closeable !=null){
            try {
                closeable.close();
            } catch (IOException e) {
                ;
            }
        }
    }
    private Map <String,Object> cacheInstance = new HashMap<>();

    private Object getInstance(String c){
        if(c == null || c.isEmpty()) return null;
        Object obj =null;
        if(cacheInstance.containsKey(c)){
            obj = cacheInstance.get(c);
        }
        if(obj== null) {
            try {
                obj = Class.forName(c).newInstance();
                if(obj instanceof FilterComplex||
                        obj instanceof RegexMatchRule||
                        obj instanceof MultiLineTokenFilter||
                        obj instanceof HandlerFactory
                ){
                    cacheInstance.put(c,obj);
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
        return obj;
    }
    private HandlerFactory getHandlerFactory(String c){
        Object obj = getInstance(c);
        return (obj instanceof HandlerFactory)
        ? (HandlerFactory) obj:null;
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
        boolean silent = Boolean.valueOf(parameters.get("-silent"));
        List<File> fileList = listFilesRecursive(directoryInput, new ExtensionFileFilter(parameters.get("-ix")));
        long maxLength = Long.MAX_VALUE;
        /**
         * Get sample data and copy to directory
         */
        if(df.startsWith("sample")){
            String length = df.substring(6);
            maxLength = parseLength(length);
            if(!silent){
                System.out.printf("sample %d byte  %s -> %s \n",maxLength, directoryInput,directoryOutput);
            }
            long sumLength = 0;
            int count = 0;
            for (int idx = 0;sumLength < maxLength && idx < fileList.size();idx++){
                File file = fileList.get(idx);
                if(file.length()+sumLength< maxLength){
                    sumLength +=file.length();
                    String relativ = relativize(directoryInput,file);
                    File target = new File(directoryOutput,relativ);
                    File parent =target.getParentFile();
                    if(!parent.exists()){
                        if(!parent.mkdirs()){
                            if(!silent){
                                System.out.printf("fail mkdir %s \n",parent);
                            }
                        }
                    }

                    if(!silent){
                        System.out.printf("copy %d size  %s \n",file.length(),relativ);
                    }
                    copy(file,target);
                    count ++;
                }
            }
            if(!silent){
                System.out.printf("copy %d files,  %d bytes \n",count,sumLength);
            }
        }

    }
    void processDirectoryFile(final File input, File output) throws IOException{
        final boolean isDirectoryOutput = output!=null && output.isDirectory();
        if(Boolean.parseBoolean(parameters.get("-statistic"))){
            statistic = new Statistic();
            statistic.setInput(parameters.get("-i"));
            statistic.setInputExt(parameters.get("-ix"));
            statistic.setOutput(parameters.get("-o"));
        }
        if(Boolean.parseBoolean(parameters.get("-hold"))){
            lineTokenHolder = new ArrayList<>();
        }
        if(parameters.containsKey("-hf")){
            handlerFactory=getHandlerFactory(parameters.get("-hf"));
            if(handlerFactory!= null){
                handlerFactory.loadConfig(parameters.get("-hfcfg"));
            }
        }
        if(parameters.containsKey("-fc")){
            File file = new File(parameters.get("-fc"));
            filterComplex = FilterComplex.read(file);
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
        Path path = inputPathParent.relativize(fileInputPath);
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
