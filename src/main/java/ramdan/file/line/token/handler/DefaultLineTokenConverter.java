package ramdan.file.line.token.handler;

import ramdan.file.line.token.Line;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.data.LineData;
import ramdan.file.line.token.data.LineTokenData;

public class DefaultLineTokenConverter implements LineTokenConverter {
    public static final DefaultLineTokenConverter DEFAULT = new DefaultLineTokenConverter();
    @Override
    public LineToken convert(Line line) {
        if(line==null){
            return LineTokenData.EMPTY;
        }
        if(line.isEOF()){
            return LineTokenData.newEOF(line);
        }
        if(line== LineData.REMOVE){
            return LineTokenData.REMOVE;
        }
        return LineTokenData.parse(line);
    }
}
