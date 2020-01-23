package ramdan.file.line.token.handler;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.val;
import lombok.var;
import ramdan.file.line.token.*;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.filter.DefaultMultiLineTokenFilter;
import ramdan.file.line.token.filter.RegexMatchRule;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

/**
 * ramdan.file.line.token.handler.TagSampleHandlerFactory
 */
public class DocSampleHandlerFactory extends AbstractHandlerFactory {

    private String tagStart;
    private String tagEnd;
    private RegexMatchRule tagSample;
    private String[] tagValue;
    private boolean ready=false;
    private boolean exclude=false;

    @Override
    public void prepare() {
        super.prepare();
        if(ready) return;
        ready=true;
        tagStart = StringUtils.emptyDefault(parameters.get("-dstart"),"DOCSTART_\\d+");
        tagEnd = StringUtils.emptyDefault(parameters.get("-dend"),"DOCEND");

        var tags =StringUtils.emptyDefault(parameters.get("-dtag"),"ACCOUNTNO");
        if(tags.contains(",")){
            tags = tags.replaceAll(",","|");
        }
        exclude = Boolean.valueOf(parameters.get("-dexclude"));
        tagSample = new RegexMatchRule(tags);
        val contents =StringUtils.emptyDefault(parameters.get("-dsample"),"");
        if(contents.contains(",")) {
            tagValue = StringUtils.trim(contents.trim().split("\\s*,\\s*"));
        }else if(StringUtils.notEmpty(contents)) {
            tagValue = new String[]{contents};
        }else {
            tagValue = new String[0];
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
            System.out.print("End");

        }
    }


    class DataSampleCapture extends MappingContentLineTokenHandler implements OutputLineTokenHandler{
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
        String extension;
        private boolean found;
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
            if(tagSample.accept(lineToken) && lineToken.equalIgnoreCase(1,tagValue)){
                //tags.add(lineToken.getTagname());
                System.out.printf("found at file : %s for data : %s \n",lineToken.getFileName(), lineToken.getSource().getLine());

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

        }


    }
}
