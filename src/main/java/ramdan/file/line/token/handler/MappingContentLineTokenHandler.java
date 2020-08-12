package ramdan.file.line.token.handler;

import lombok.val;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.Tokens;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.data.MultiLineData;
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
    public Tokens process(LineToken lineToken) {
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
        if(start && filter.isMatchContent(tag)){
            return matchContent(lineToken);
        }
        return removeNotMatch? LineTokenData.EMPTY:lineToken;
    }

    protected Tokens notReadyEndTagHandle(LineToken lineToken) {
        return lineToken;
    }

    protected Tokens endTagHandle(LineToken lineToken) {
        return lineToken;
    }

    protected Tokens startTagHandle(LineToken lineToken) {
        return lineToken;
    }

    protected Tokens alreadyStartTagHandle(LineToken lineToken) {
        val result = endTagHandle(LineTokenData.EMPTY);
        reset();
        return MultiLineData.merge(
                result,
                startTagHandle(lineToken));
    }

    protected Tokens matchContent(LineToken lineToken){
        return lineToken;
    }

}
