package ramdan.file.line.token;
import lombok.val;
import ramdan.file.line.token.config.FileConfigHolder;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.data.Statistic;
import ramdan.file.line.token.factory.AbstractHandlerFactory;
import ramdan.file.line.token.factory.DocSampleHandlerFactory;
import ramdan.file.line.token.factory.TagSampleHandlerFactory;
import ramdan.file.line.token.filter.FilterComplex;
import ramdan.file.line.token.handler.*;


import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static boolean verbose;
    public static boolean debug;
    public static boolean useStandardOutput =false;
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
                if(StringUtils.equalOnce(param,"-vv","-vvv","--donate","-d","--version","-v","--help","-h","-trim","-save", "-empty","-intern","-statistic", "-silent","-dfx","-dvc")){
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
                }else{
                    val index = param.indexOf("=");
                    if(index>0) {
                        val name = param.substring(0, index);
                        val value = param.substring(index + 1);
                        parameters.put(name,value);
                        param = null;
                    }

                }
            }
        }
        StringSave.args(args);
        LineTokenData.args(args);
    }

    private Object getInstance(String c){
        if(c == null || c.isEmpty()) return null;
        try{
            if(debug){
                System.out.println("Load Class : "+ c);
            }
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
        if(debug){
            System.out.println("HandlerFactory");
        }
        val ht = parameters.get("-hf");
        if(StringUtils.isEmpty(ht)){

            return;
        }
        if(debug){
            System.out.println("HandlerFactory : "+ ht);
        }
        Object obj = getInstance(ht);
        if(obj instanceof HandlerFactory){
            handlerFactory= (HandlerFactory) obj;
        }
    }

//    @Deprecated
//    private void processDirectory(final  File directoryInput, final File directoryOutput) throws IOException {
//        String df = parameters.get("-df");
//        boolean silent = Boolean.valueOf(parameters.get("-silent"));
//        List<File> fileList = StreamUtils.listFilesRecursive(directoryInput, new ExtensionFileFilter(parameters.get("-ix")));
//        long maxLength = Long.MAX_VALUE;
//        /**
//         * Get sample data and copy to directory
//         */
//        if(df.matches("sample\\d*\\w*")){
//            String length = df.substring(6);
//            maxLength = StringUtils.parseLength(length);
//            if(!silent){
//                System.out.printf("sample %d byte  %s -> %s \n",maxLength, directoryInput,directoryOutput);
//            }
//            long sumLength = 0;
//            int count = 0;
//            for (int idx = 0;sumLength < maxLength && idx < fileList.size();idx++){
//                File file = fileList.get(idx);
//                if(file.length()+sumLength< maxLength){
//                    sumLength +=file.length();
//                    String relativ = StreamUtils.relative(directoryInput,file);
//                    File target = new File(directoryOutput,relativ);
//                    File parent =target.getParentFile();
//                    if(!parent.exists()){
//                        if(!parent.mkdirs()){
//                            System.err.printf("fail mkdir %s \n",parent);
//                        }
//                    }
//
//                    if(!silent){
//                        System.out.printf("copy %d size  %s \n",file.length(),relativ);
//                    }
//                    StreamUtils.copy(file,target);
//                    count ++;
//                }
//            }
//            if(!silent){
//                System.out.printf("copy %d files,  %d bytes \n",count,sumLength);
//            }
//        }
//    }
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
        System.out.printf("No file input \n");
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
        debug = parameters.containsKey("-vvv");
        verbose = debug || parameters.containsKey("-vv");

        if(parameters.containsKey("-v") || parameters.containsKey("--version") ){
            runPrintVersion();
            return;
        }
        if(parameters.containsKey("-h")||parameters.containsKey("--help")){
            runPrintHelp();
            return;
        }
        if(parameters.containsKey("-d")||parameters.containsKey("--donate")){
            runPrintDonate();
            return;
        }
        if(debug||verbose){
            runPrintVersion();
        }
        FileConfigHolder.load(parameters.get("-cfgd"),parameters.get("-cfg"));
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
                useStandardOutput=true;
            }
        }else{
            outputFile = new File(outputString);
        }
        int threadSize = 1;
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
        if(StringUtils.notEmpty(parameters.get("-tsample"))||StringUtils.notEmpty(parameters.get("-tcontent"))){
            if(StringUtils.isEmpty(parameters.get("-ox"))){
                parameters.put("-ox",".spl");
            }
            parameters.put("-hf", TagSampleHandlerFactory.class.getName());
        }

        if(StringUtils.notEmpty(parameters.get("-dvf"))||
                StringUtils.notEmpty(parameters.get("-dv"))||
                StringUtils.notEmpty(parameters.get("-dtxv"))){
            parameters.put("-hf", DocSampleHandlerFactory.class.getName());
        }


        setupHandlerFactory();
//        if(parameters.containsKey("-df")){
//            processDirectory(inputFile,outputFile);
//        }else {
//            processContent(inputFile, outputFile);
//        }
        processContent(inputFile, outputFile);
        executorService.shutdown();

        if(!useStandardOutput) {
            System.out.println("Running finish");
        }
    }

    private void runPrintVersion() {
        System.out.println("Version : 1.0.0-alfa");
    }

    private void runPrintHelp() {
        System.out.println("For Support Contact Person ");
        System.out.println("Email : <Agus Muhammad Ramdan> agus.ramdan@gmail.com");
    }

    private void runPrintDonate() {
        System.out.println("For Donate");
        System.out.println("Send to LinkAja Account +6285295252579 an Agus Muhammad Ramdan");
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
