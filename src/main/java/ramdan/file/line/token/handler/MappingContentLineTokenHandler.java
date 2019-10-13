package ramdan.file.line.token.handler;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.filter.MultiLineTokenFilter;

public class MappingContentLineTokenHandler extends DefaultLineTokenHandler {

    protected final MultiLineTokenFilter filter;
    protected final boolean removeNotMatch;
    protected boolean start;

    public MappingContentLineTokenHandler(MultiLineTokenFilter filter, boolean removeNotMatch) {
        this.filter = filter;
        this.removeNotMatch = removeNotMatch;
    }
    public MappingContentLineTokenHandler(MultiLineTokenFilter filter) {
        this(filter,false);
    }
    protected void reset(){

    }
    @Override
    public LineToken process(LineToken lineToken) {
        String tag = lineToken.get(0);
        if(filter.isMatchStart(tag)){
            try {
                if (start) {
                    return alreadyStartTagHandle(lineToken);
                } else {
                    return startTagHandle(lineToken);
                }
            }finally {
                start = true;
            }
        }else
        if(filter.isMatchEnd(tag)){
            try {
                if (start) {
                    return endTagHandle(lineToken);
                } else {
                    return notReadyEndTagHandle(lineToken);
                }
            }finally {
                start = false;
                reset();
            }
        }else
        if(filter.isMatchContent(tag)){
            return matchContent(lineToken);
        }
        return removeNotMatch? LineTokenData.EMPTY:lineToken;
    }

    private LineToken notReadyEndTagHandle(LineToken lineToken) {
        return lineToken;
    }

    protected LineToken endTagHandle(LineToken lineToken) {
        return lineToken;
    }

    protected LineToken startTagHandle(LineToken lineToken) {
        return lineToken;
    }

    protected LineToken alreadyStartTagHandle(LineToken lineToken) {
        return lineToken;
    }

    protected LineToken matchContent(LineToken lineToken){
        return lineToken;
    }

}
