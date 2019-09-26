package ramdan.file.line.token.listener;

import ramdan.file.line.token.Line;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.data.LineData;
import ramdan.file.line.token.data.LineTokenData;

public class DelegateLineListener implements LineListener {
    private LineTokenListener tokenListener;

    public DelegateLineListener(LineTokenListener tokenListener) {
        this.tokenListener = tokenListener;
    }

    @Override
    public void event(Line line) {
        tokenListener.event(LineTokenData.parse(line));
    }
}
