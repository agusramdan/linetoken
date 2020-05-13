package ramdan.file.line.token.handler;

import ramdan.file.line.token.Line;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.data.LineTokenData;

public class DefaultLineTokenConverter implements LineTokenConverter {
    @Override
    public LineToken convert(Line line) {
        if(line==null){
            return LineTokenData.EMPTY;
        }
        if(line.isEOF()){
            return LineTokenData.EOF;
        }

        return LineTokenData.parse(line);
    }
}
