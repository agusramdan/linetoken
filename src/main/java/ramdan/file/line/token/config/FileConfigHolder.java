package ramdan.file.line.token.config;

import lombok.val;
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
            if(lineToken.getTagname().startsWith("#")) {
                return;
            }
            configLineToken.add(lineToken);
        }
    };
    public static void load(String dir,String config) throws IOException {
        if(StringUtils.isEmpty(config)) return;
        //val list = new ArrayList<File>();
        //load(configListener,list);

        load(configListener,config.split(","));
    }
    public static void load(String config) throws IOException {
        if(StringUtils.isEmpty(config)) return;
        load(configListener,config.split(","));
    }

    public static void load(File config) throws IOException {
        StreamUtils.readLine(config,configListener);
    }

    public static void load(LineTokenListener configListener,String...fileConfigs) throws IOException {
        for (String fc : fileConfigs) {
            if(StringUtils.notEmpty(fc)) {
                StreamUtils.readLine(new File(fc), configListener);
            }
        }
    }
    public static void load(LineTokenListener configListener,List<File>fileConfigs) throws IOException {
        for (File fc : fileConfigs) {
            StreamUtils.readLine(fc,configListener);
        }
    }
    public static void load(LineTokenListener configListener,File...fileConfigs) throws IOException {
        for (File fc : fileConfigs) {
            StreamUtils.readLine(fc,configListener);
        }
    }
    public static void read(LineTokenListener listener) {
        for (LineToken lt: configLineToken) {
            listener.event(lt);
        }
    }
}
