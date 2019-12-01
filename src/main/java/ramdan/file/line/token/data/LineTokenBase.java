package ramdan.file.line.token.data;

import ramdan.file.line.token.Line;
import ramdan.file.line.token.LineToken;

public class LineTokenBase extends LineTokenAbstract {

    public LineTokenBase(String file, Integer start, Integer end) {
        super(file, start, end);
    }

    public LineTokenBase(LineToken lineToken) {
        super(lineToken);
    }
    @Override
    protected LineToken newLineToken(String fileName, Integer start, Integer end, String... tokens) {
        return LineTokenData.newInstance(fileName,start,end,timestamp(),tokens);
    }
    @Override
    protected LineToken newLineToken(String fileName, Integer start, Integer end,long timestamp, String... tokens) {
        return LineTokenData.newInstance(fileName,start,end,timestamp,tokens);
    }

    @Override
    public int length() {
        throw new RuntimeException("not support function length");
    }

    @Override
    public Line getSource() {
        return null;
    }

    @Override
    public String get(int index) {
        throw new RuntimeException("not support function replace get index");
    }
}
