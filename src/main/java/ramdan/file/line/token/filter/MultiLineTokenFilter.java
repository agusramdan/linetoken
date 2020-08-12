package ramdan.file.line.token.filter;

import ramdan.file.line.token.LineToken;

public interface MultiLineTokenFilter {
    String name();
    int length();
    boolean isMatchStart(String value);
    boolean isMatchEnd(String value);
    boolean isMatchContent(String value);
    int getMatchIndex(String value);
}
