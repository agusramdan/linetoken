package ramdan.file.line.token.listener;

import ramdan.file.line.token.LineToken;

public class DelegateLineTokenListener implements LineTokenListener {
    private LineTokenListener[] tokenListener;

    public DelegateLineTokenListener(LineTokenListener ... tokenListener) {
        this.tokenListener = tokenListener;
    }
    @Override
    public void event(LineToken lineToken) {
        for (LineTokenListener tl : tokenListener) {
            tl.event(lineToken);
        }
    }
}
