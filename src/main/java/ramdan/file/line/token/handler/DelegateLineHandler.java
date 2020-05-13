package ramdan.file.line.token.handler;

import ramdan.file.line.token.*;
import ramdan.file.line.token.data.Traceable;

import java.util.ArrayList;
import java.util.List;

public class DelegateLineHandler implements LineHandler {

    private final LineHandler[] handlers;

    public DelegateLineHandler(LineHandler ... handlers) {
        this.handlers = handlers;
    }

    @Override
    public Line process(Line lineToken) {
        for (LineHandler h: handlers) {
            lineToken=h.process(lineToken);
        }
        return lineToken;
    }
}
