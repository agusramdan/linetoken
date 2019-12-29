package ramdan.file.line.token;
import lombok.val;
import ramdan.file.line.token.config.FileConfigHolder;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.data.Statistic;
import ramdan.file.line.token.filter.FilterComplex;
import ramdan.file.line.token.filter.MultiLineTokenFilter;
import ramdan.file.line.token.filter.RegexMatchRule;
import ramdan.file.line.token.handler.*;


import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static Main main = new Main();

    private Map<String,String> parameters = new HashMap<>();
    //private List<LineToken> lineTokenHolder ;
    //private Set<String> filterRule;
    //private Map<String,String> mappingRule;
    private PrintStream output;
    private Statistic statistic;
    private HandlerFactory handlerFactory;
    private FilterComplex filterComplex;
    private ExecutorService executorService;
    public String getArgumentParameter(String key){
        return parameters.get(key);
    }
    /**
     * Populate Argument
     * -i         : input directory of file
     * -ix        :
     * -trim      : trim token
     * -save      : save / canonical string
     * -empty     : null to empty
     * -intern    :
     * -statistic :
     * -silent    :
     * -hf        : handler file factory
     * -hfcfg     : file config for handler factory
     * -df        : distribution file to multiple directory method copy,move ex. copy,move,sample,
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
                if(StringUtils.equalOnce(param,"-trim","-save", "-empty","-intern","-statistic", "-silent")){
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

    private Object getInstance(String c){
        if(c == null || c.isEmpty()) return null;
        try{
            val obj = Class.forName(c).newInstance();
            if(obj instanceof HandlerFactory){
                val hf = (HandlerFactory) obj;
                hf.loadConfig(parameters.get("-hfcfg"));
            }
            if(obj instanceof AbstractHandlerFactory){
                val ahf = (AbstractHandlerFactory) obj;
                ahf.setParameters(new HashMap<>(parameters));

            }
            return obj;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setupHandlerFactory(){
        if(!parameters.containsKey("-hf")){
            return;
        }
        Object obj = getInstance(parameters.get("-hf"));
        if(obj instanceof HandlerFactory){
            handlerFactory= (HandlerFactory) obj;
        }
    }

    private void processDirectory(final  File directoryInput, final File directoryOutput) throws IOException {
        String df = parameters.get("-df");
        boolean silent = Boolean.valueOf(parameters.get("-silent"));
        List<File> fileList = StreamUtils.listFilesRecursive(directoryInput, new ExtensionFileFilter(parameters.get("-ix")));
        long maxLength = Long.MAX_VALUE;
        /**
         * Get sample data and copy to directory
         */
        if(df.matches("sample\\d*\\w*")){
            String length = df.substring(6);
            maxLength = StringUtils.parseLength(length);
            if(!silent){
                System.out.printf("sample %d byte  %s -> %s \n",maxLength, directoryInput,directoryOutput);
            }
            long sumLength = 0;
            int count = 0;
            for (int idx = 0;sumLength < maxLength && idx < fileList.size();idx++){
                File file = fileList.get(idx);
                if(file.length()+sumLength< maxLength){
                    sumLength +=file.length();
                    String relativ = StreamUtils.relative(directoryInput,file);
                    File target = new File(directoryOutput,relativ);
                    File parent =target.getParentFile();
                    if(!parent.exists()){
                        if(!parent.mkdirs()){
                            System.err.printf("fail mkdir %s \n",parent);
                        }
                    }

                    if(!silent){
                        System.out.printf("copy %d size  %s \n",file.length(),relativ);
                    }
                    StreamUtils.copy(file,target);
                    count ++;
                }
            }
            if(!silent){
                System.out.printf("copy %d files,  %d bytes \n",count,sumLength);
            }
        }
    }
    private void processContent(final File input, File output) throws IOException{
        val dh = new DefaultDirectoryHandler();
        dh.setExecutorService(executorService);
        dh.setParameters(new HashMap<>(parameters));
        dh.setStatistic(statistic);
        dh.setHandlerFactory(handlerFactory);
        dh.setFilterComplex(filterComplex);
        if(output!=null){
            if(output.isDirectory()){
                dh.setOutputDirectory(output);
            }else {
                dh.setOutputFile(output);
            }
        }
        if(input !=null ) {
            if(input.isDirectory()) {
                dh.setInputDirectory(input);
                if(output==null){
                    dh.setOutputDirectory(input);
                }
            }else{
                dh.setInputFile(input);
                if(output==null){
                    dh.setOutputDirectory(input.getParentFile());
                }
            }
        }
        dh.prepare();
        dh.run();
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
        FileConfigHolder.load(parameters.get("-cfg"));
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
        int threadSize = 2;
        val thread=parameters.get("-thread");

        if(thread!=null){
            threadSize = Integer.parseInt(thread);
        }
        executorService =Executors.newFixedThreadPool(threadSize);
        if(Boolean.parseBoolean(parameters.get("-statistic"))){
            statistic = new Statistic();
            statistic.setInput(parameters.get("-i"));
            statistic.setInputExt(parameters.get("-ix"));
            statistic.setOutput(parameters.get("-o"));
        }
        if(parameters.containsKey("-fc")){
            File file = new File(parameters.get("-fc"));
            filterComplex = FilterComplex.read(file);
        }
        val tsample = parameters.get("-tsample");
        if(StringUtils.notEmpty(tsample)){
            if(StringUtils.isEmpty(parameters.get("-ox"))){
                parameters.put("-ox",".spl");
            }
            parameters.put("-hf",TagSampleHandlerFactory.class.getName());
        }

        setupHandlerFactory();

        if(parameters.containsKey("-df")){
            processDirectory(inputFile,outputFile);
        }else {
            processContent(inputFile, outputFile);
        }
        executorService.shutdown();
    }

    public static void main(String ... args) throws IOException {
        main.args(args);
        main.run();
    }

    public static class ExtensionFileFilter implements FileFilter{
        private String[] extensions;

        public ExtensionFileFilter(String inputExtension) {
            this.extensions = inputExtension!=null? inputExtension.trim().split("\\s*,\\s*"):new String[0];
        }

        public boolean accept(String name){
            if(extensions.length==0) return true;
            for (String str: extensions) {
                if(name.endsWith(str)){
                    return true;
                }
            }
            return false;
        }
        @Override
        public boolean accept(File file) {
            return file.isDirectory() ||
                    (file.isFile() && accept(file.getName()));
        }
    }

}
