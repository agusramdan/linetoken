package ramdan.file.line.token.factory;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.val;
import lombok.var;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.StreamUtils;
import ramdan.file.line.token.StringUtils;
import ramdan.file.line.token.Tokens;
import ramdan.file.line.token.callback.Callback;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.filter.DefaultMultiLineTokenFilter;
import ramdan.file.line.token.filter.RegexMatchRule;
import ramdan.file.line.token.handler.LineTokenHandler;
import ramdan.file.line.token.handler.MappingContentLineTokenHandler;
import ramdan.file.line.token.handler.OutputLineTokenHandler;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

/**
 * ramdan.file.line.token.factory.TagSampleHandlerFactory
 */
public class TagSampleHandlerFactory extends AbstractHandlerFactory {

    private String tokenStart;
    private String tokenEnd;
    private RegexMatchRule tokenSample;
    private String[] tokenContents;
    private boolean ready=false;
    private int maxData=3;
    private long maxLength= 1024*1024; //1mb
    private Map<String,List<DataHolder>> tagsData = new HashMap<>();
    private Comparator<DataHolder> dataHolderComparable = new Comparator<DataHolder>() {
        @Override
        public int compare(DataHolder h1, DataHolder h2) {
            return Long.compare(h1.temporary.length(),h2.temporary.length());
        }
    };
    private synchronized void add(DataHolder holder){
        add1(holder);
    }
    private  void add1(DataHolder holder){
        var tagsHolder = new HashSet<String>(holder.tag);
        tagsHolder.removeAll(tagsData.keySet());
        if(tagsHolder.isEmpty()){
            var maxDataTagname = new HashSet<String>();
            List<DataHolder> minDataHolder=null;
            for (String tagname: holder.tag) {
                val data = tagsData.get(tagname);
                if(data.size()>=maxData){
                    maxDataTagname.add(tagname);
                }else
                if(minDataHolder==null||data.size()<minDataHolder.size()){
                    minDataHolder=data;
                }
            }
            Iterator<String> it = maxDataTagname.iterator();
            while(minDataHolder==null&& it.hasNext()){
                val data = tagsData.get(it.next());
                if(data.get(maxData-1).temporary.length()>holder.temporary.length()){
                    minDataHolder =data;
                }
            }

            if(minDataHolder!=null){
                minDataHolder.add(holder);
                Collections.sort(minDataHolder,dataHolderComparable);
                holder=null;
                if(minDataHolder.size()>maxData){
                    add1(minDataHolder.remove(maxData));
                }
            }
        }else{
            val list = new ArrayList<DataHolder>();
            list.add(holder);
            tagsData.put(tagsHolder.iterator().next(),list);
            holder =null;
        }

        if(holder!=null){
            // rejected remove
            holder.temporary.delete();
        }
    }
    @Override
    public void prepare() {
        super.prepare();
        if(ready) return;
        ready=true;
        tokenStart = StringUtils.emptyDefault(parameters.get("-tstart"),"DOCSTART_\\d+");
        tokenEnd = StringUtils.emptyDefault(parameters.get("-tend"),"DOCEND");
        var tokens =StringUtils.emptyDefault(parameters.get("-tsample"),"EVENT_\\d+");
        if(tokens.contains(",")){
            tokens = tokens.replaceAll(",","|");
        }
        tokenSample = new RegexMatchRule(tokens);
        val contents =StringUtils.emptyDefault(parameters.get("-tsample"),"");
        if(contents.contains(",")) {
            tokenContents = contents.trim().split("\\s*,\\s*");
        }else if(StringUtils.notEmpty(contents)) {
            tokenContents = new String[]{contents};
        }else {
            tokenContents = new String[0];
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
            val dataHolderList = tagsData.entrySet();

            for (Map.Entry<String,List<DataHolder>> e : dataHolderList) {
                File outputTag =null;
                for ( DataHolder dh: e.getValue()) {
                    if(outputTag==null) {
                        outputTag = new File(dh.output, e.getKey());
                        outputTag.mkdirs();
                    }
                    var filename = dh.input.getName();
                    if(dh.extension!=null && !filename.endsWith(dh.extension)){
                        if(!dh.extension.startsWith(".")){
                            filename=filename+".";
                        }
                        filename=filename+dh.extension;
                    }
                    try {
                        StreamUtils.copy(dh.temporary, new File(outputTag,filename),true);
                    } catch (IOException er) {
                        er.printStackTrace();
                    }
                    dh.temporary.delete();
                }

            }
        }
    }

    @AllArgsConstructor
    class DataHolder{
        File input;
        File output;// idr
        File temporary;
        String extension;
        Set<String> tag;
    }


    class DataSampleCapture extends MappingContentLineTokenHandler implements OutputLineTokenHandler {
        public DataSampleCapture() {
            super(new DefaultMultiLineTokenFilter("token",tokenStart,tokenEnd,"\\w*"), true);
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
        private boolean skip=false;
        private File temporary;
        private PrintStream printStream;
        private Set<String> tags ;

        @Override
        protected Tokens startTagHandle(LineToken lineToken) {
            tags = new HashSet<>();
            try {
                temporary = File.createTempFile("tag_",".spl.tmp",baseDirectoryOutput);
                printStream= new PrintStream(temporary);
            } catch (IOException e) {
                e.printStackTrace();
                // todo fix this
            }
            if(lineToken.isEmpty()){
                printStream.print(tokenStart);
            }else {
                val line = lineToken.getSource();
                if(line!=null){
                    printStream.print(line.getLine());
                }else {
                    printStream.print(tokenStart);
                }
            }
            printStream.println();
            return LineTokenData.EMPTY;
        }

        @Override
        protected Tokens endTagHandle(LineToken lineToken) {
            if(!skip && found) {
                if (lineToken.isEmpty()) {
                    printStream.print(tokenEnd);
                } else {
                    val line = lineToken.getSource();
                    if (line != null) {
                        printStream.print(line.getLine());
                    } else {
                        printStream.print(tokenEnd);
                    }
                }
                printStream.println();
                printStream.flush();
                printStream.close();
                add(new DataHolder(fileInput, baseDirectoryOutput, temporary,extension,tags));
            }else {
                printStream.close();
                temporary.delete();
            }
            return LineTokenData.EMPTY;
        }

        @Override
        protected void reset() {
            super.reset();
            tags = null;
            found=false;
            skip = false;
            temporary=null;
            printStream=null;
        }

        @Override
        protected Tokens matchContent(LineToken lineToken) {
            if(!skip) {
                boolean accept=tokenSample.accept(lineToken);
                if(!accept){
                    for (String test:tokenContents) {
                        if(lineToken.getSource().containIgnoreCase(test)){
                            accept=true;
                            break;
                        }
                    }
                }
                if(accept){
                    tags.add(lineToken.getTagname());
                    found=true;
                }
                if (!lineToken.isEmpty()) {
                    val line = lineToken.getSource();
                    printStream.print(line.getLine());
                    printStream.println();
                    printStream.flush();
                    skip= temporary.length()> maxLength;
                }
            }
            return LineTokenData.EMPTY;
        }

        @Override
        public void flush() {

        }
        @Override
        public void close() throws IOException {
            if(fileOutput!=null && fileOutput.exists()){
                fileOutput.delete();
            }
        }
    }
}
