package ramdan.file.line.token.handler;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.data.LineTokenData;

import java.util.Set;

public class FilterLineTokenHandler extends DefaultLineTokenHandler {

    private Set<String> mapping;

    public FilterLineTokenHandler(Set<String> mapping) {
        this.mapping = mapping;
    }

    @Override
    public LineToken process(LineToken lineToken) {

        if(!mapping.contains(lineToken)){
            lineToken = LineTokenData.EMPTY;
        }
        return lineToken;
    }
}
