package ramdan.file.line.token.listener;

import ramdan.file.line.token.Line;
import ramdan.file.line.token.handler.DefaultLineTokenConverter;
import ramdan.file.line.token.handler.LineTokenConverter;

public class DelegateLineListener implements LineListener {
    private LineTokenListener tokenListener;
    private LineTokenConverter tokenConverter;

    public DelegateLineListener(LineTokenListener tokenListener) {
        this(tokenListener,new DefaultLineTokenConverter());
    }

    public DelegateLineListener(LineTokenListener tokenListener, LineTokenConverter tokenConverter) {
        this.tokenListener = tokenListener;
        this.tokenConverter = tokenConverter;
    }

    @Override
    public void event(Line line) {

        tokenListener.event(tokenConverter.convert(line));

    }
}
