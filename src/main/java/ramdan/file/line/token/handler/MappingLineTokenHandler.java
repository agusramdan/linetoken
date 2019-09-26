package ramdan.file.line.token.handler;

import ramdan.file.line.token.LineToken;

import java.util.Map;

public class MappingLineTokenHandler extends DefaultLineTokenHandler{

    private Map<String,String> mapping;

    public MappingLineTokenHandler(Map<String,String> mapping) {
        this.mapping = mapping;
    }

    @Override
    public LineToken process(LineToken lineToken) {
        String target = mapping.get(lineToken.get(0));
        if(target!=null){
            lineToken = lineToken.replaceToken(0,target);
        }
        return lineToken;
    }
}
