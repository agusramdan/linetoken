package ramdan.file.line.token.filter;

import ramdan.file.line.token.LineToken;

public interface LineTokenFilter {
    boolean accept(LineToken lineToken);
}
