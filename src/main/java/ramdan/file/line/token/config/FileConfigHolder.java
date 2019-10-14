package ramdan.file.line.token.config;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.StreamUtils;
import ramdan.file.line.token.StringUtils;
import ramdan.file.line.token.listener.LineTokenListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileConfigHolder {
    private final static List<LineToken> configLineToken = new ArrayList<>();
    private final static LineTokenListener configListener= new LineTokenListener() {
        @Override
        public void event(LineToken lineToken) {
            if(lineToken.get(0).startsWith("#")) {
                return;
            }
            configLineToken.add(lineToken);
        }
    };
    public static void load(String config) throws IOException {
        if(StringUtils.isEmpty(config)) return;
        String  [] fileConfigs =config.split(",");
        for (String fc : fileConfigs) {
            load(new File(fc));
        }
    }
    public static void load(File config) throws IOException {
        StreamUtils.readLine(config,configListener);
    }
    public static void loadTo(LineTokenListener listener) {
        for (LineToken lt: configLineToken) {
            listener.event(lt);
        }
    }
}
