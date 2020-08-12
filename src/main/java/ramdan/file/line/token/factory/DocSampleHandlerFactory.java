package ramdan.file.line.token.factory;

import lombok.Setter;
import lombok.val;
import lombok.var;
import ramdan.file.line.token.*;
import ramdan.file.line.token.callback.Callback;
import ramdan.file.line.token.data.LineData;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.filter.DefaultMultiLineTokenFilter;
import ramdan.file.line.token.filter.RegexMatchRule;
import ramdan.file.line.token.handler.LineTokenHandler;
import ramdan.file.line.token.handler.MappingContentLineTokenHandler;
import ramdan.file.line.token.handler.OutputLineTokenHandler;
import ramdan.file.line.token.listener.LineListener;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * ramdan.file.line.token.factory.TagSampleHandlerFactory
 */
public class DocSampleHandlerFactory extends AbstractHandlerFactory {

    private String tagStart;
    private String tagEnd;
    private RegexMatchRule tagSample;
    private boolean ready=false;
    private boolean exclude=false;

    @Override
    public void prepare() {
        super.prepare();
        if(ready) return;
        ready=true;
        tagStart = StringUtils.emptyDefault(parameters.get("-ds"),"DOCSTART_\\d+");
        tagEnd = StringUtils.emptyDefault(parameters.get("-de"),"DOCEND");
        val tagIndexValue = parameters.get("-dtxv");
        var tags =StringUtils.emptyDefault(parameters.get("-dt"),"ACCOUNTNO");
        if(tags.contains(",")){
            tags = tags.replaceAll(",","|");
        }
        exclude = Boolean.valueOf(parameters.get("-dfx"));
        boolean contain = Boolean.valueOf(parameters.get("-dvc"));

        final List<String> listValues = new ArrayList<String>();
        val listener = new LineListener(){
            @Override
            public void event(Line line) {
                if(line.isEmpty()) return;
                val contents = line.getLine();
                if(contents.contains(",")) {
                    listValues.addAll(Arrays.asList(StringUtils.trim(contents.trim().split("\\s*,\\s*"))));
                }else if(StringUtils.notEmpty(contents)) {
                    listValues.add(contents);
                }
            }
        };

        listener.event(new LineData(parameters.get("-dv")));

        val fsample = parameters.get("-dvf");
        if(StringUtils.notEmpty(fsample)){
            val file = new File(fsample);
            if(!file.exists() || !file.isFile()){
                System.err.println("-dfx file no found "+fsample);
                throw new RuntimeException("-dfx file no found "+fsample);
            }
            try {
                StreamUtils.readLine(new File(fsample),listener);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if(StringUtils.notEmpty(tagIndexValue)){
            val data= tagIndexValue.split(",");
            if(data.length!=3){
                System.err.println("chek paramtert -dv or -dvf");
                throw new RuntimeException("Error listValues");
            }
            tagSample = new TagIndexValueMatchRule(data[0],Integer.valueOf(data[1]),data[2]);
        }else {
            if(listValues.isEmpty()){
                // error no value
                System.err.println("NO value filter/exclude");
                if(parameters.get("-dv")==null){
                    System.err.println("chek paramtert -dv or -dvf");
                }
                throw new RuntimeException("Error listValues");
            }

            String[] tagValue = listValues.toArray(new String[listValues.size()]);
            tagSample = new TagValueMatchRule(tags, tagValue, contain);
        }

        if(Main.verbose) {
            System.out.println("Running with parameter:");

            val args = new String[]{"-i", "-ix", "-o", "-ox", "-ds", "-de", "-dt", "-dfx", "-dvc", "-dv", "-dvf"};

            for (String str : args) {
                val value = parameters.get(str);
                if (StringUtils.notEmpty(value)) {
                    System.out.format(" %s = %s \n", str, value);
                }
            }
            System.out.println();
        }
    }

    @Override
    public void loadConfig(String string) {

    }

    @Override
    public LineTokenHandler getStartLineTokenHandler() {
        return null;
    }

    @Override
    public void loadContentLineTokenHandlers(Collection<LineTokenHandler> holder) {

    }

    @Override
    public LineTokenHandler getOutputLineTokenHandler() {
        return new DataSampleCapture();
    }

    @Override
    public LineTokenHandler getFinallyLineTokenHandler() {
        return null;
    }

    @Override
    public Callback getFinishCallback() {
        return new FinishCallback();
    }

    class FinishCallback implements Callback{
        @Override
        public void call(Object o) {
            System.out.println("exclude = "+exclude);
        }
    }


    class DataSampleCapture extends MappingContentLineTokenHandler implements OutputLineTokenHandler {
        public DataSampleCapture() {
            super(new DefaultMultiLineTokenFilter("token", tagStart, tagEnd,"\\w*"), true);
        }
        @Setter
        private File fileInput;

        @Setter
        private File baseDirectoryInput;

        @Setter
        private File fileOutput;

        @Setter
        private File baseDirectoryOutput;
        @Setter
        private String tagdelimiter=null;
        @Setter
        private String tokendelimiter=null;
        @Setter
        private boolean printLine;

        @Setter
        String extension;
        private boolean found;
        private List<String> foundData=new ArrayList<>();
        //private boolean skip=false;
        private File temporary;
        private PrintStream printStream;
        //private Set<String> tags ;
        public void ensureFileOutputReady(){
            if(fileOutput==null){
                if(baseDirectoryOutput==null){
                    throw new RuntimeException("Base Directory Output not found");
                }
                if(baseDirectoryInput==null){
                    throw new RuntimeException("Base Directory Input not found");
                }
                if(fileInput==null){
                    throw new RuntimeException("Input not found");
                }
                String fileName = StreamUtils.relative(baseDirectoryInput,fileInput);
                if(extension!=null && !fileName.endsWith(extension)){
                    fileName = fileName+extension;
                }
                fileOutput = new File(baseDirectoryOutput,fileName);
                fileOutput.getParentFile().mkdirs();
                if(fileOutput.equals(fileInput)) throw new RuntimeException("Same file Input Output");
            }
        }
        @Override
        protected Tokens startTagHandle(LineToken lineToken) {
            //tags = new HashSet<>();
            try {
                temporary = File.createTempFile("tag_",".spl.tmp",baseDirectoryOutput);
                printStream= new PrintStream(temporary);
            } catch (IOException e) {
                e.printStackTrace();
                // todo fix this
            }
            if(lineToken.isEmpty()){
                printStream.print(tagStart);
            }else {
                val line = lineToken.getSource();
                if(line!=null){
                    printStream.print(line.getLine());
                }else {
                    printStream.print(tagStart);
                }
            }
            printStream.println();
            return LineTokenData.EMPTY;
        }

        @Override
        protected Tokens endTagHandle(LineToken lineToken) {
            if((exclude && !found) || (!exclude && found)) {
                if (lineToken.isEmpty()) {
                    printStream.print(tagEnd);
                } else {
                    val line = lineToken.getSource();
                    if (line != null) {
                        printStream.print(line.getLine());
                    } else {
                        printStream.print(tagEnd);
                    }
                }
                printStream.println();
                printStream.flush();
                printStream.close();
                try {
                    ensureFileOutputReady();
                    StreamUtils.copy(temporary, fileOutput,true);
                } catch (IOException er) {
                    er.printStackTrace();
                }
            }else {
                printStream.close();
            }

            temporary.delete();

            return LineTokenData.EMPTY;
        }

        @Override
        protected void reset() {
            super.reset();
            //tags = null;
            found=false;
            //skip = false;
            temporary=null;
            printStream=null;
        }

        @Override
        protected Tokens matchContent(LineToken lineToken) {
            if(tagSample.accept(lineToken)){
                foundData.add(lineToken.getSource().getLine());
                found=true;
            }
            if (!lineToken.isEmpty()) {
                val line = lineToken.getSource();
                printStream.print(line.getLine());
                printStream.println();
                printStream.flush();
                //skip= temporary.length()> maxLength;
            }
            return LineTokenData.EMPTY;
        }

        @Override
        public void flush() {

        }
        @Override
        public void close() throws IOException {
            val out = System.out;
            synchronized (out){
                if(this.foundData.isEmpty()){
                    out.printf("not found at file : %s \n\n",fileInput.getName());
                }else {
                    out.printf("found at file : %s \n",fileInput.getName());
                    for (String line : foundData) {
                      out.println(line);
                    }
                    out.println();
                }
            }
        }
    }

    private class TagValueMatchRule extends RegexMatchRule {
        private String[] tagValue;
        private boolean contain=false;

        public TagValueMatchRule(String regex, String[] tagValue) {
            super(regex);
            this.tagValue = tagValue;
        }

        public TagValueMatchRule(String regex, String[] tagValue, boolean contain) {
            this(regex,tagValue);
            this.contain = contain;
        }

        @Override
        public boolean accept(LineToken lineToken) {
            return super.accept(lineToken)&& (lineToken.equalIgnoreCase(1,tagValue) || contain && lineToken.containIgnoreCase(1,tagValue));
        }
    }
    private class TagIndexValueMatchRule extends RegexMatchRule {
        private int index;
        private String value;

        public TagIndexValueMatchRule(String regex, int index, String value) {
            super(regex);
            this.index = index;
            this.value = value;
        }

        @Override
        public boolean accept(LineToken lineToken) {
            return super.accept(lineToken)&& (lineToken.equalIgnoreCase(index,value));
        }
    }
}
