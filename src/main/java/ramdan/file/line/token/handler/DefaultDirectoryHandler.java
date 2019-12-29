package ramdan.file.line.token.handler;

import lombok.Setter;
import lombok.val;
import ramdan.file.line.token.StreamUtils;
import ramdan.file.line.token.data.Statistic;
import ramdan.file.line.token.filter.FilterComplex;

import java.io.File;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.ExecutorService;

public class DefaultDirectoryHandler extends DirectoryHandler implements Runnable{

    @Setter
    private File outputFile;
    @Setter
    private File inputFile;
    @Setter
    private boolean singeOutputFile;
    @Setter
    private boolean singeInputFile;
    @Setter
    private Statistic statistic;

    @Setter
    private HandlerFactory handlerFactory;

    @Setter
    private FilterComplex filterComplex;

    @Setter
    private PrintStream defaultStreamOutput;

    private boolean ready=false;
    @Override
    public void prepare() {
        super.prepare();
        if(ready) return;
        ready=true;
        if(handlerFactory!= null){
            handlerFactory.prepare();
        }
        if(inputFile!=null){
            singeInputFile=true;
            singeOutputFile=true;
        }
        if(outputFile!=null){
            singeOutputFile=true;
        }
    }
    protected List<File> getFilesInput(){
        if(singeOutputFile) return Arrays.asList(inputFile);
        return super.getFilesInput();
    }
    @Override
    protected void submit(Runnable handler) {
        if(singeInputFile||singeOutputFile){
            handler.run();
        }else {
            super.submit(handler);
        }
    }

    @Override
    protected void waitUntilFinish() {
        super.waitUntilFinish();
        if(statistic!=null) {
            this.statistic.print(System.out);
        }
        if(handlerFactory!=null){
            val cb =handlerFactory.getFinishCallback();
            if(cb!=null) cb.call(this);
        }
    }

    public DefaultFileHandler createFileHandler(File input) {
        List<LineTokenHandler> list = new ArrayList<>();

        if (statistic != null) {
            statistic.add(input);
            list.add(new StatisticLineTokenHandler(statistic));
        }
        if (handlerFactory != null) {
            list.add(handlerFactory.getStartLineTokenHandler());
        }
        addFilterHandlers(list);
        if (filterComplex != null) {
            list.add(new FilterComplexLineTokenHandler(filterComplex));
        }

        if (getParameters().containsKey("-fx")) {
            list.add(new RegexLineTokenHandler(getParameters().get("-fx")));
        }

        addMappingHandlers(list);

        if (handlerFactory != null) {
            handlerFactory.loadContentLineTokenHandlers(list);
        }

        LineTokenHandler outputHandler = null;
        if (handlerFactory != null) {
            outputHandler = handlerFactory.getOutputLineTokenHandler();
        }
        if (outputHandler == null) {
            if (getOutputDirectory() != null||singeOutputFile) {
                outputHandler = new DefaultOutputLineTokenHandler();
            } else if (this.defaultStreamOutput != null) {
                outputHandler = new PrintStreamLineTokenHandler(this.defaultStreamOutput);
            }
        }
        if (outputHandler != null) {
            if (outputHandler instanceof OutputLineTokenHandler) {
                val outputLineTokenHandler = (OutputLineTokenHandler) outputHandler;
                if(singeOutputFile) {
                    outputLineTokenHandler.setFileOutput(outputFile);
                }
                outputLineTokenHandler.setFileInput(input);
                outputLineTokenHandler.setExtension(getOutputExtension());
                outputLineTokenHandler.setBaseDirectoryOutput(getOutputDirectory());
                outputLineTokenHandler.setBaseDirectoryInput(getInputDirectory());
            }
            list.add(outputHandler);
        }
        if (handlerFactory != null) {
            list.add(handlerFactory.getFinallyLineTokenHandler());
        }
        DefaultFileHandler dfh = new DefaultFileHandler();
        dfh.setInput(input);
        dfh.setList(list);
        return dfh;
    }
}
