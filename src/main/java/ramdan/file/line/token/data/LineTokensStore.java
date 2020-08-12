package ramdan.file.line.token.data;

import ramdan.file.line.token.Destroyable;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.LineTokens;
import ramdan.file.line.token.callback.Callback;

import java.io.Closeable;
import java.io.IOException;

public interface LineTokensStore extends LineTokens, Closeable, Callback<LineToken>, Destroyable {
    void release();
    void addAll(LineTokensStore lts) throws IOException;
    void addAll(Iterable<LineToken>it) throws IOException;
}
