package ramdan.file.line.token.handler;

import ramdan.file.line.token.Line;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.data.LineTokenData;

public class DefaultLineTokenConverter implements LineTokenConverter {
    @Override
    public LineToken convert(Line lineToken) {
        if(lineToken==null){
            return LineTokenData.EMPTY;
        }
        if(lineToken.isEOF()){
            return LineTokenData.EOF;
        }

        return LineTokenData.parse(lineToken);
    }
}
