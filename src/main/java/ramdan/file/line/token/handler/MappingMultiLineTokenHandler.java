package ramdan.file.line.token.handler;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.LineTokenData;
import ramdan.file.line.token.MultiLine;
import ramdan.file.line.token.filter.MultiLineTokenFilter;

import java.util.HashMap;
import java.util.Map;

public class MappingMultiLineTokenHandler extends DefaultLineTokenHandler {

    private MultiLineTokenFilter filter;
    private Map<String,String> mapping = new HashMap<>();

    private boolean start;
    private Integer startLine;
    private String startValue;
    private Integer endLine;
    private String endValue;

    public MappingMultiLineTokenHandler(MultiLineTokenFilter filter) {
        this.filter = filter;
    }

    @Override
    public LineToken process(LineToken lineToken) {
        String tag = lineToken.get(0);

        if(start){
            if(filter.isMatchEnd(tag)){
                start = false;
                endValue = lineToken.get(1);
                String[] data = new String[filter.length()+3];
                data[0]=filter.name();
                data[1]=startValue;
                data[2]=endValue;
                for (Map.Entry<String,String> entry: mapping.entrySet()) {
                    int i = filter.isMatchIndex(entry.getKey());
                    if(i>-1){
                        data[i+3]=entry.getValue();
                    }
                }
                lineToken = LineTokenData.newInstance(startLine,endLine,data);

                startValue=null;
                startLine=null;
                endValue=null;
                endLine=null;

                return lineToken;
            }else
            if(filter.isMatchContent(tag)){
                mapping.put(lineToken.get(0),lineToken.get(1));
            }
            lineToken= LineTokenData.EMPTY;
        }else if(filter.isMatchStart(tag)){
            start = true;
            startLine= lineToken.getStart();
            startValue = lineToken.get(1);
            lineToken= LineTokenData.EMPTY;
        }

        return lineToken;
    }
}
