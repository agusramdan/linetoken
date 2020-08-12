package ramdan.file.line.token.listener;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.handler.LineTokenHandler;

public class WarpLineTokenListener implements LineTokenListener {

    private final LineTokenHandler[] handler;

    public WarpLineTokenListener(LineTokenHandler ... handler) {
        this.handler = handler;
    }

    @Override
    public void event(LineToken lineToken) {
        for (int i = 0 ;i <handler.length ; i++) {
            handler[i].process(lineToken);
        }

    }
}
