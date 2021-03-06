package ramdan.file.line.token.handler;

import ramdan.file.line.token.Line;
import ramdan.file.line.token.data.LineData;

public class DelegateLineHandler implements LineHandler {

    private final LineHandler[] handlers;

    public DelegateLineHandler(LineHandler ... handlers) {
        this.handlers = handlers;
    }

    @Override
    public Line process(Line lineToken) {
        for (LineHandler h: handlers) {
            if(lineToken == LineData.REMOVE) break;
            lineToken=h.process(lineToken);
        }
        return lineToken;
    }
}
