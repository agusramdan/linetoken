package ramdan.file.line.token.handler;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.Tokens;

public class SimpleMappingLineTokenHandler implements LineTokenHandler {

    String newname;
    int[] mapping;

    public SimpleMappingLineTokenHandler(String newname, int ... mapping) {
        this.newname = newname;
        this.mapping = mapping;
    }

    @Override
    public Tokens process(LineToken lineToken) {
        return lineToken.mapping(newname,mapping);
    }
}
