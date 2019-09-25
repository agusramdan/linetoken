package ramdan.file.line.token.filter;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.LineTokenData;

public interface LineTokenFilter {
    boolean accept(LineToken lineToken);
}
