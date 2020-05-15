package ramdan.file.line.token.data;

import lombok.val;
import ramdan.file.line.token.LineToken;

public class TokenMapping extends TokenEditable {

    private final LineToken source;

    public TokenMapping(LineToken source) {
        this(source,null);

    }
    public TokenMapping(LineToken source, String name, int ... idx) {
        super(source,name,idx);
        this.source=source;
    }
    public String map(int idxSource, int idxTarget){
        val str = source.get(idxSource);
        return set(idxTarget,str);
    }
}
