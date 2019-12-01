package ramdan.file.line.token;

import java.io.IOException;

public interface LineTokens extends Tokens,Iterable<LineToken> {
    boolean empty();
    int count();
    long length();
    LineToken head();
    LineToken tail();
    void add(LineToken lt);
}
