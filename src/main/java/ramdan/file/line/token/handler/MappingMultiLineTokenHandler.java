package ramdan.file.line.token.handler;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.data.MultiLineData;
import ramdan.file.line.token.filter.MultiLineTokenFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MappingMultiLineTokenHandler extends DefaultLineTokenHandler {

    protected final MultiLineTokenFilter filter;
    // return empty when tag between start & end
    protected final boolean returnEmpty;
    private List<LineToken> buffer = new ArrayList<>();
    private Map<String,String> mapping = new HashMap<>();

    private boolean start;
    private Integer startLine;
    private String startValue;
    private Integer endLine;
    private String endValue;
    private String endFileName;


    public MappingMultiLineTokenHandler(MultiLineTokenFilter filter,boolean returnEmpty) {
        this.filter = filter;
        this.returnEmpty=returnEmpty;
    }
    public MappingMultiLineTokenHandler(MultiLineTokenFilter filter) {
        this(filter,true);
        reset();
    }
    protected void reset(){
        buffer.clear();
        mapping.clear();
        startValue=null;
        startLine=null;
        endValue=null;
        endLine=null;
    }
    @Override
    public LineToken process(LineToken lineToken) {
        String tag = lineToken.get(0);
        if(start){
            if(filter.isMatchEnd(tag)){
                start = false;
                endLine = lineToken.getEnd();
                endFileName = lineToken.getFileName();
                endValue = lineToken.get(1);
                LineToken capture=capture();
                if(returnEmpty){
                    if(capture == LineTokenData.EMPTY){
                        buffer.add(lineToken);
                        lineToken= MultiLineData.newInstance(buffer);
                    }else {
                        lineToken= capture;
                    }
                }else{
                    lineToken= MultiLineData.merge(lineToken,capture);
                }
                reset();
                return lineToken;
            }else
            if(filter.isMatchContent(tag)){
                matchContent(lineToken);
            }
        }else if(filter.isMatchStart(tag)){
            start = true;
            startLine= lineToken.getStart();
            startValue = lineToken.get(1);
        }

        if(start && returnEmpty){
            // put to buffer
            buffer.add(lineToken);
            lineToken = LineTokenData.EMPTY;
        }

        return lineToken;
    }

    protected void matchContent(LineToken lineToken){
        mapping.put(lineToken.get(0),lineToken.get(1));
    }

    protected LineToken capture(){
        String[] data = new String[filter.length()+3];
        data[0]=filter.name();
        data[1]=startValue;
        data[2]=endValue;
        for (Map.Entry<String,String> entry: mapping.entrySet()) {
            int i = filter.getMatchIndex(entry.getKey());
            if(i>-1){
                data[i+3]=entry.getValue();
            }
        }
        return LineTokenData.newInstance(endFileName,startLine,endLine,data);
    }
}
